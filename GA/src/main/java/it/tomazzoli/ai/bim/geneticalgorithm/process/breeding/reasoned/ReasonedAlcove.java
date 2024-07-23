package it.tomazzoli.ai.bim.geneticalgorithm.process.breeding.reasoned;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.locationtech.jts.geom.Envelope;

import it.tomazzoli.ai.bim.beans.CoveredTile;
import it.tomazzoli.ai.bim.beans.SecurityCamera;
import it.tomazzoli.ai.bim.geneticalgorithm.Individual;
import it.tomazzoli.ai.bim.geneticalgorithm.process.breeding.Alcove;

public abstract class ReasonedAlcove extends Alcove 
{
	protected double tolerance=0.9;
	
	public ReasonedAlcove() 
	{
		super();
	}

	public abstract Individual figlio(Individual padre, Individual madre);
	
	protected Individual incubatrice(List<SecurityCamera> telecamerePadre,List<SecurityCamera> telecamereMadre,int quantiElementiDalPadre,int quantiElementiDallaMadre)
	{
		List<SecurityCamera> elementi = new ArrayList<SecurityCamera>();
		List<SecurityCamera> daAggiungere = sottolista(telecamerePadre,quantiElementiDalPadre);
		elementi.addAll(daAggiungere);
		
		daAggiungere.clear();
		daAggiungere = sottolista(telecamereMadre,quantiElementiDallaMadre);
		elementi.addAll(daAggiungere);
		
		Individual result=new Individual(elementi);
		
		return result;
	}

	protected List<SecurityCamera> leastConflictual(Individual individuo,List<SecurityCamera> certe,List<SecurityCamera> daDeterminare)
	{
		/**
		 * prendo tutte le piastrelle già coperte dalla telecamere del primo genitore
		 */
		List<Envelope> poligoniCoperti = poligoniCoperti(individuo,certe);
		List<SecurityCamera> result=new ArrayList<SecurityCamera>();
		for(SecurityCamera cam:daDeterminare)
		{
			int quanteAreeCopreDellAltroGenitore=inside(poligoniCoperti,cam);
			SecurityCamera valutata = new SecurityCamera(cam);
			valutata.setCost(quanteAreeCopreDellAltroGenitore);
			result.add(valutata);
		}
		Collections.sort(result,new CostComparator().reversed());	
		return result;
	}
	
	private int inside(List<Envelope> poligoniCoperti,SecurityCamera cam)
	{
		int result=0;
		for(Envelope p:poligoniCoperti)
		{
			if(cam.getCoverage().getEnvelopeInternal().intersects(p))
			{
				result++;
			}
		}
		return result;
	}
	
	private List<SecurityCamera> sottolista(List<SecurityCamera> telecamere,int limiteMax)
	{
		List<SecurityCamera> result = new ArrayList<SecurityCamera>();
		if(telecamere.size() > limiteMax)
		{
			result.addAll(telecamere.subList(0, limiteMax));
		}
		else
		{
			result.addAll(telecamere);
		}
		return result;
	}
	
	/***
	 * Ritorna le piastrelle coperte da una certa lista di telecamere (che si crede sia un sottoinsieme delle telecamere dell'individuo
	 * @param individuo
	 * @param telecamere
	 * @return
	 */
	private List<Envelope> poligoniCoperti(Individual individuo,List<SecurityCamera> telecamere) 
	{
		List<Envelope> result = new ArrayList<Envelope>();
		for(SecurityCamera cam:telecamere)
		{
			List<CoveredTile> listaCT=individuo.getPiastrelleDiRischioCoperte(cam);
			for(CoveredTile ct:listaCT)
			{
				result.add(ct.getTile());
			}
		}
		return result;
	}

	protected List<SecurityCamera> numeroGiusto(List<SecurityCamera> selezione, int numeroDesiderato, List<SecurityCamera> telecamereTutte,List<SecurityCamera> daEscludere)
	{
		List<SecurityCamera> result = new ArrayList<SecurityCamera>();
		int quanteSono=selezione.size();
		
		if(quanteSono < numeroDesiderato)
		{
			int quanteNeMancano = numeroDesiderato - quanteSono;
			List<SecurityCamera> ulterioriCandidate = new ArrayList<SecurityCamera>();
			ulterioriCandidate.addAll(telecamereTutte);
			ulterioriCandidate.removeAll(selezione);
			List<SecurityCamera> telecamereDaAggiungere =  aCaso(ulterioriCandidate,quanteNeMancano,daEscludere);
			result.addAll(selezione);
			result.addAll(telecamereDaAggiungere);
		}
		else
		{
			result =  aCaso(selezione, numeroDesiderato,daEscludere);
		}
		return result;
	}
	
	private class CostComparator implements Comparator<SecurityCamera> {
        public int compare(SecurityCamera i1, SecurityCamera i2) {
            Double f1 = i1.getCost();
            Double f2 = i2.getCost();
            return f1.compareTo(f2);
        }
    }
	
	/*
	 protected List<SecurityCamera> nonConflictual(Individual individuo,List<SecurityCamera> certe,List<SecurityCamera> daDeterminare)
	 {
		List<Envelope> poligoniCoperti = poligoniCoperti(individuo,certe);
		List<SecurityCamera> result=new ArrayList<SecurityCamera>();
		for(SecurityCamera cam:daDeterminare)
		{
			boolean copreareeNuove=!insideAtLeastOne(poligoniCoperti,cam);
			if(copreareeNuove)
			{
				result.add(cam);
			}
		}
		return result;
	}

	private boolean insideAtLeastOne(List<Envelope> poligoniCoperti,SecurityCamera cam)
	{
		boolean result=false;
		for(Envelope p:poligoniCoperti)
		{
			if(cam.getCoverage().getEnvelopeInternal().intersects(p))
			{
				return true;
			}
		}
		return result;
	}
	
	
	
	 * ho un problema che non capisco di "found non-noded intersection between LINESTRING " quindi per il momento accantono voronoi
	private List<Polygon> voronoi(List<SecurityCamera> certe, double tolerance)
	{
		Geometry complessivo = new GeometryFactory().createPolygon();
		List<Polygon> result = new ArrayList<Polygon>();
		for(SecurityCamera cam:certe)
		{
			Polygon area=cam.getCoverage();
			if(area.isValid())
			{
				if(complessivo.isEmpty())
				{
					complessivo=area.copy();
				}
				else
				{
					complessivo= complessivo.union(area);
				}
			}
		}
		SmallComputationalGeometryCalculator calc = new SmallComputationalGeometryCalculator();
		complessivo = complessivo.union();
		complessivo = complessivo.convexHull();
		
		Geometry voronoi = calc.getVoronoiDiagram(complessivo, tolerance);
		int quante=voronoi.getNumGeometries();	
		for(int i=0;i<quante;i++)
		{
			Geometry r = voronoi.getGeometryN(i);
			List<Polygon> toAdd = calc.getPolygons(r);
			result.addAll(toAdd);
		}
		return result;
	}
	*/
}
