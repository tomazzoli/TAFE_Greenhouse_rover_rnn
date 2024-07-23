package it.tomazzoli.ai.bim.geneticalgorithm.process.fitness;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Envelope;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;

import it.tomazzoli.ai.bim.beans.CoveredTile;
import it.tomazzoli.ai.bim.beans.Element;
import it.tomazzoli.ai.bim.beans.Pole;
import it.tomazzoli.ai.bim.beans.SecurityCamera;
import it.tomazzoli.ai.bim.beans.Wall;
import it.tomazzoli.ai.bim.geneticalgorithm.BuiltEnvironment;
import it.tomazzoli.ai.bim.geneticalgorithm.Individual;

public class SingleItemEvaluator extends PerformanceEvaluator
{
	
	
	public SingleItemEvaluator(BuiltEnvironment ambiente) 
	{
		super(ambiente);
	}
	
	public Individual elaboraCoperturaTelecamere(Individual individuo)
	{
		List<SecurityCamera> telecamere = individuo.getCameras();
		List<SecurityCamera> installate = conCostoInstallazione(telecamere);
		
		List<CoveredTile> risktobecovered = super.allRiskTiles();
		List<CoveredTile> valuetobecovered = super.allValueTiles();
		Individual result = new Individual(installate);
		
		Hashtable<SecurityCamera,List<CoveredTile>> piastrelleRischio = verificaCopertura(telecamere,risktobecovered);
		Hashtable<SecurityCamera,List<CoveredTile>> piastrelleValore = verificaCopertura(telecamere,valuetobecovered);
		
		for(SecurityCamera cam:telecamere)
		{
			List<CoveredTile> piastrelle = piastrelleRischio.get(cam);
			result.addCoveredRiskTiles(cam, piastrelle);
			piastrelle = piastrelleValore.get(cam);
			result.addCoveredValueTiles(cam, piastrelle);
		}
		
		return result;
	}

	/***
	 * Calcola per ogni telecamera l'eventuale sovraccosto di installazione dovuto al fatto che non è nè su un muro nè sun un palo esistente
	 * @param telecamere
	 * @param tobecovered
	 * @return Hashtable <telecamera, List<CoveredTile> la lista delle piastrelle coperte dalla telecamera
	 */
	private List<SecurityCamera> conCostoInstallazione(List<SecurityCamera> telecamere)
	{
		List<SecurityCamera> installate = new ArrayList<SecurityCamera>();
		for(SecurityCamera cam:telecamere)
		{
			SecurityCamera installata  =new SecurityCamera(cam);
			double costo = aumentaCostoPerMuri(cam,_ambiente.getObstacles());
			if(costo < 0)
			{
				costo = aumentaCostoPerPali(cam,_ambiente.getPoles());
			}
			if(costo < 0)
			{
				costo = cam.getCost() * MOLTIPLICATORECOSTOINSTALLAZIONEPALO;
			}
			installata.setCost(costo);
			installate.add(installata);
		}
		return installate;
	}
	
	private double aumentaCostoPerMuri(SecurityCamera cam, List<Wall> muri)
	{
		List<Element> elementiPortanti = new ArrayList<Element> ();
		for(Wall w:muri)
		{
			Element e = (Element)w;
			elementiPortanti.add(e);
		}
		double result = aumentaCosto(cam,elementiPortanti);
		return result;
	}
	
	private double aumentaCostoPerPali(SecurityCamera cam, List<Pole> pali)
	{
		List<Element> elementiPortanti = new ArrayList<Element> ();
		for(Pole p:pali)
		{
			Element e = (Element)p;
			elementiPortanti.add(e);
		}
		double result = aumentaCosto(cam,elementiPortanti);
		return result;
	}
	
	private double aumentaCosto(SecurityCamera cam, List<Element> elementiPortanti)
	{
		double result = cam.getCost();
		Coordinate p = cam.getRegistrationPoint();
		boolean valorizzata = false;
		Point punto = new GeometryFactory().createPoint(p);
		for(Element w:elementiPortanti)
		{
			if(w.getShape().covers(punto))
			{
				valorizzata = true;
				break;
			}
		}
		if(!valorizzata)
		{
			for(Element w:elementiPortanti)
			{
				if(w.getShape().distance(punto) < DISTANZAMAXELEMENTOPORTANTE)
				{
					valorizzata = true;
					result = result * MOLTIPLICATORECOSTOINSTALLAZIONEVICINANZA;
					break;
				}
			}
		}
		if(!valorizzata)
		{
			result = -1;
		}
		return result;
	}
	
	/***
	 * Calcola per ogni telecamera le piastrelle coperte da questa telecamere, e produce una tabella contenente una lista di piastrelle per ogni telecamera
	 * @param telecamere
	 * @param tobecovered
	 * @return Hashtable <telecamera, List<CoveredTile> la lista delle piastrelle coperte dalla telecamera
	 */
	private Hashtable<SecurityCamera,List<CoveredTile>> verificaCopertura(List<SecurityCamera> telecamere,List<CoveredTile> tobecovered)
	{
		Hashtable<SecurityCamera,List<CoveredTile>> coperture = new Hashtable<SecurityCamera,List<CoveredTile>>();
		for(SecurityCamera cam:telecamere)
		{
			List<CoveredTile> piastrelle = new ArrayList<CoveredTile>();
			for(CoveredTile a:tobecovered)
			{
				// se l'arco coperto dalla telecamera interseca la piastrella di rischio (odi valore), la aggiungo a quelle della telecamera
				if(visibleFrom(a, cam))
				{
					piastrelle.add(a);
				}
			}
			coperture.put(cam, piastrelle);
		}
		return coperture;
	}
	
	/***
	 * Calcola per ogni telecamera il valore di rischio delle aree coperte dalla telecamera stessa, ne restituisce una tabella <telecamera, valore>
	 * @param telecamere
	 * @return Hashtable <telecamera, valore> del valore di rischio coperto dalla telecamera
	 */
	public Hashtable<SecurityCamera,Double> singlePerformance(List<SecurityCamera> telecamere)
	{
		Hashtable<SecurityCamera,Double> result=new Hashtable<SecurityCamera,Double>();
		/**
		 * prendo tutte le piastrelle
		 * */
		List<CoveredTile> tobecovered=allRiskTiles();
		/**
		 * per ogni telecamera, calcolo la somma dei valori delle piastrelle di cui effettua la copertura
		 */
		for(SecurityCamera cam:telecamere)
		{
			double singleFitmess=0;
			for(CoveredTile a:tobecovered)
			{
				/**
				 *  se l'arco coperto dalla telecamera interseca la piastrella di rischio, aggiungo alla singola fitness il rischio della stessa per il fattore di forma della telecamera
				 */
				if(visibleFrom(a, cam))
				{
					double singleTileFitmess=a.getValue()*cam.getQualityFactor();
					singleFitmess=singleFitmess+singleTileFitmess;
				}
			}
			result.put(cam, singleFitmess);
		}
		return result;
	}
}
