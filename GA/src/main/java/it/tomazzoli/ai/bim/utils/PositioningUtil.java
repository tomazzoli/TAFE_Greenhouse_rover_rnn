package it.tomazzoli.ai.bim.utils;

import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Random;

import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Envelope;
import org.locationtech.jts.geom.Polygon;

import it.tomazzoli.ai.bim.beans.CoveredTile;
import it.tomazzoli.ai.bim.beans.RiskArea;
import it.tomazzoli.ai.bim.beans.SecurityCamera;
import it.tomazzoli.ai.bim.geneticalgorithm.BuiltEnvironment;
import it.tomazzoli.ai.bim.geometry.SmallComputationalGeometryCalculator;

public class PositioningUtil 
{
	private BuiltEnvironment _bimenv;
	private Random random;
	private SmallComputationalGeometryCalculator compGeometry;
	
	public PositioningUtil(BuiltEnvironment bimenv) 
	{
		_bimenv=bimenv;
		random = new Random(System.currentTimeMillis());
		compGeometry= new SmallComputationalGeometryCalculator();
	}

	/***
	 * Restituisce tutte le piastrelle di tutte le aree di rischio
	 * @return ina lista di rettangoli (Envelope) 
	 */
	public List<Envelope> tutteLePiastrelle()
	{
		List<Envelope> tutteLePiastrelle = new ArrayList<Envelope>();
		for(RiskArea r:_bimenv.getRiskAreas())
		{
			List<Envelope> queste = r.getTiles();
			tutteLePiastrelle.addAll(queste);
		}
		return tutteLePiastrelle;
	}
	
	
	public List<Coordinate> trovaPuntiDaAggiungere(List<Coordinate> puntiDefiniti, SecurityCamera baseCamera,int quanti)
	{
		int raggio = baseCamera.getRaggio();
		/**
		 * trovo tutte le piastrelle di tutte le aree di rischio
		 */
		List<Envelope> tutteLePiastrelle = tutteLePiastrelle();
		/**
		 * trovo tutte le piastrelle potenzialmente coperte da ogni telecamera (come se ogni telecamera coprisse 360 gradi)
		 */
		List<Envelope> piastrellePotenzialmenteOccupate = new ArrayList<Envelope>();
		for(Coordinate p:puntiDefiniti)
		{
			List<Envelope> occupateDaQuesto = occupate(tutteLePiastrelle,p,raggio);
			piastrellePotenzialmenteOccupate.addAll(occupateDaQuesto);		
		}
		/**
		 *  trovo quindi le piastrelle certamente libere indipendentemente dall'orientamento e prendo n punti 
		 */
		tutteLePiastrelle.removeAll(piastrellePotenzialmenteOccupate);
		List<Coordinate> puntiDaAggiungere = new ArrayList<Coordinate>();
		puntiDaAggiungere = trovaPuntiInAreeLibere(tutteLePiastrelle,puntiDaAggiungere,raggio,quanti);
		/**
		 *  se non ne ho travati abbastanza, calcolo le piastrelle con meno potenziali sovrapposizioni sulle telecamere
		 *  e da queste ne scelgo la rimanenza
		 */
		int quantiNeMancano = quanti - puntiDaAggiungere.size();
		if(quantiNeMancano > 0)
		{
			List<CoveredTile> piastrelleValorizzate = new ArrayList<CoveredTile>();
			tutteLePiastrelle = tutteLePiastrelle();
			for(Envelope piastrellaSemplice:tutteLePiastrelle)
			{
				CoveredTile nuova = new CoveredTile(piastrellaSemplice,0);
				piastrelleValorizzate.add(nuova);
			}
			
			for(Coordinate centro:puntiDaAggiungere)
			{
				piastrelleValorizzate = valorizzaOccupazione(piastrelleValorizzate,centro,raggio);
			}
			
			List<Coordinate> puntiUlterioriDaAggiungere = new ArrayList<Coordinate>(); 
		    puntiUlterioriDaAggiungere = trovaPuntiInAreePotenzialmenteOccupate(piastrelleValorizzate,puntiUlterioriDaAggiungere,raggio,quantiNeMancano);
			puntiDaAggiungere.addAll(puntiUlterioriDaAggiungere);
		}
		return puntiDaAggiungere;
	}
	
	
	public List<Coordinate> trovaPuntiInAreeLibere(List<Envelope> piastrelleLibere, List<Coordinate> puntiDefiniti, int raggio,int quanti)
	{
		List<Coordinate> puntiDaAggiungere = new ArrayList<Coordinate>();
		/**
		 *  se non ci sono piastrelle libere, inutile cercare punti
		 */
		if(piastrelleLibere.size()>0)
		{
			puntiDaAggiungere.addAll(puntiDefiniti);
			//System.out.printf("Chiamato trovaPuntiInAreeLibere con %d piastrelle, %d punti, %d da aggiungere \r\n", piastrelleLibere.size(),puntiDefiniti.size(),quanti);
			int indice = random.nextInt(piastrelleLibere.size());
			Envelope scelta = piastrelleLibere.get(indice);
			Coordinate p = scelta.centre();
			
			puntiDaAggiungere.add(p);
			
			List<Envelope> occupateDaQuesto = occupate(piastrelleLibere,p,raggio);
			List<Envelope> piastrelleLibereAdesso = new ArrayList<Envelope>();
			piastrelleLibereAdesso.addAll(piastrelleLibere);
			piastrelleLibereAdesso.removeAll(occupateDaQuesto);
			
			if(puntiDaAggiungere.size() < quanti)
			{
				if (piastrelleLibereAdesso.size() > 1)
				{
					puntiDaAggiungere = trovaPuntiInAreeLibere(piastrelleLibereAdesso,puntiDaAggiungere,raggio,quanti); // nota RICORSIVO, pericolo!
				}
			}
		}
		return puntiDaAggiungere;
	}
	
	public List<Coordinate> trovaPuntiInAreePotenzialmenteOccupate(List<CoveredTile> piastrelleValorizzate, List<Coordinate> puntiTrovati, int raggio,int quanti)
	{
		List<Coordinate> puntiDaAggiungere = new ArrayList<Coordinate>();
		puntiDaAggiungere.addAll(puntiTrovati);
		/**
		 * Ordino le piastrelle per valore di copertura crescente, ovvero per minor sovrapposione le prime
		 */
		Collections.sort(piastrelleValorizzate, new CoveredTileComparator());
		/**
		 * determino il valore più basso ed estraggo tutte quelle con questo valore
		 */
		CoveredTile piastrellaBase = piastrelleValorizzate.get(0);
		double valoreBase = piastrellaBase.getValue();
		
		List<CoveredTile> piastrelleBuone = new ArrayList<CoveredTile>();
		for(CoveredTile t:piastrelleValorizzate)
		{
			if(t.getValue() > valoreBase)
			{
				break;
			}
			else
			{
				piastrelleBuone.add(t);
			}
		}
		
		int indice = random.nextInt(piastrelleBuone.size());
		CoveredTile scelta = piastrelleBuone.get(indice);
		Envelope piastrellescelta = scelta.getTile();
		Coordinate p = piastrellescelta.centre();
		puntiDaAggiungere.add(p);
		
		List<CoveredTile> piastrelleValorizzateAdesso = valorizzaOccupazione(piastrelleValorizzate,p,raggio);
		
		if(puntiDaAggiungere.size() < quanti)
		{
			if (piastrelleValorizzate.size() > 1)
			{
				puntiDaAggiungere = trovaPuntiInAreePotenzialmenteOccupate(piastrelleValorizzateAdesso,puntiDaAggiungere,raggio,quanti); // nota RICORSIVO, pericolo!
			}
		}
		
		return puntiDaAggiungere;
		
	}
	
	public List<Envelope> occupate(List<Envelope> tutteLePiastrelle,Coordinate p,int raggio)
	{
		List<Envelope> piastrelleOccupate = new ArrayList<Envelope>();
		Polygon coperturaIpotetica = compGeometry.createCoverage(p, raggio, 0, compGeometry.CERCHIO_COMPLETO);
		for(Envelope piastrella:tutteLePiastrelle)
		{
			boolean coperta = coperturaIpotetica.getEnvelopeInternal().intersects(piastrella);
			if(coperta)
			{
				piastrelleOccupate.add(piastrella);
			}
		}
		return piastrelleOccupate;
	}
	
	/***
	 * Restituisce una lista di piastrelle valorizzate; il valore è quello di input addizionato di uno quando la piastrella è toccata dal cerchio in input  
	 * @param piastrelleDiPartenza, la lista di piastrele valorizzate
	 * @param p Centro del cerchio
	 * @param raggio raggio del cerchio
	 * @return la lista di piastrele valorizzate considerando la appartenenza al cerchio
	 */
	public List<CoveredTile> valorizzaOccupazione(List<CoveredTile> piastrelleDiPartenza,Coordinate p,int raggio)
	{
		List<CoveredTile> piastrelleValorizzate = new ArrayList<CoveredTile>();
		Polygon coperturaIpotetica = compGeometry.createCoverage(p, raggio, 0, compGeometry.CERCHIO_COMPLETO);
		for(CoveredTile piastrella:piastrelleDiPartenza)
		{
			Envelope piastrellaSemplice = piastrella.getTile();
			boolean coperta = coperturaIpotetica.getEnvelopeInternal().intersects(piastrellaSemplice);
			if(coperta)
			{
				double valoreBase = piastrella.getValue()+1;
				CoveredTile nuova = new CoveredTile(piastrellaSemplice,valoreBase);
				piastrelleValorizzate.add(nuova);
			}
			else
			{
				piastrelleValorizzate.add(piastrella);
			}
		}
		return piastrelleValorizzate;
	}
	
	public boolean alreadyPresent(Coordinate p,List<Envelope> piastrelle)
	{
		boolean result = false;
		for(Envelope piastrella:piastrelle)
		{
			boolean coperta = piastrella.covers(p);
			if(coperta)
			{
				return true;
			}
		}
		return result;
	}
	
	private class CoveredTileComparator implements Comparator<CoveredTile> {
        public int compare(CoveredTile i1, CoveredTile i2) {
            Double f1 = i1.getValue();
            Double f2 = i2.getValue();
            return f1.compareTo(f2);
        }
    }
	
	public BuiltEnvironment normalizza(BuiltEnvironment ambienteOriginale, double fattoreScala)
	{
    	List<RiskArea> complessoOriginale = ambienteOriginale.getRiskAreas();
    	List<RiskArea> complesso = new ArrayList<RiskArea>();
    	
    	double deltaX = ambienteOriginale.getMaxBoundingBox().getWidth();
    	double deltaY = ambienteOriginale.getMaxBoundingBox().getHeight();
    	
    	for(RiskArea areaOriginale:complessoOriginale)
    	{
    		Coordinate[]  coords = areaOriginale.getShape().getCoordinates();
    		Coordinate[]  newCoords = new Coordinate[coords.length];
    		for(int i=0; i < coords.length; i++)
    		{
    			double x = (coords[i].x + deltaX)*fattoreScala;
    			double y = (coords[i].y + deltaY)*fattoreScala;
    			newCoords[i] = new Coordinate(x,y);
    		}
    		Polygon p = areaOriginale.getShape().getFactory().createPolygon(newCoords);
    		RiskArea normalizzata = new RiskArea(p,areaOriginale.getRiskFactor());
    		normalizzata.setBimID(areaOriginale.getBimID());
    		normalizzata.setName(areaOriginale.getName());
    		complesso.add(normalizzata);
    	}
        BuiltEnvironment result=new BuiltEnvironment(complesso);
		return result;
	}

}
