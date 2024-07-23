package it.tomazzoli.ai.bim.geneticalgorithm;

import java.util.*;

import org.json.JSONObject;
import org.locationtech.jts.geom.Envelope;

import it.tomazzoli.ai.bim.beans.CoveredTile;
import it.tomazzoli.ai.bim.beans.SecurityCamera;
import it.tomazzoli.ai.bim.geneticalgorithm.process.fitness.InstalledSecurityCamera;
import it.tomazzoli.ai.bim.utils.CameraPositioningParameters;
import it.tomazzoli.ai.bim.utils.json.IndividualJSONFactory;

public class Individual 
{
	private List<InstalledSecurityCamera> _telecamere;
	private double _fitness;
	
	public Individual(List<SecurityCamera> telecamere) 
	{
		_telecamere=new ArrayList<InstalledSecurityCamera>();
		for(SecurityCamera cam:telecamere)
		{
			_telecamere.add(new InstalledSecurityCamera(cam));
		}
		_fitness = -1;
	}

	public List<SecurityCamera> getCameras()
	{
		List<SecurityCamera> telecamere=new ArrayList<SecurityCamera>();
		for(InstalledSecurityCamera cam:_telecamere)
		{
			telecamere.add(new SecurityCamera(cam));
		}
		return telecamere;
	}
	
	public void addCoveredRiskTiles(SecurityCamera cam,List<CoveredTile> piastrelle)
	{
		InstalledSecurityCamera thisone=get(cam);
		thisone.addRiskTiles(piastrelle);
		double fitness = calcolaFitness();
		this.setFitness(fitness);
	}
	
	public void removeCoveredRiskTiles(SecurityCamera cam,List<CoveredTile> piastrelle)
	{
		InstalledSecurityCamera thisone=get(cam);
		thisone.removeRiskTiles(piastrelle);
		double fitness = calcolaFitness();
		this.setFitness(fitness);
	}
	
	public void addUniqueCoveredRiskTiles(SecurityCamera cam,List<CoveredTile> piastrelle)
	{
		InstalledSecurityCamera thisone=get(cam);
		thisone.addUniqueRiskTiles(piastrelle);
	}
	
	public void removeUniquCoveredRiskTiles(SecurityCamera cam,List<CoveredTile> piastrelle)
	{
		InstalledSecurityCamera thisone=get(cam);
		thisone.removeUniqueRiskTiles(piastrelle);
	}
	
	
	public void addCoveredValueTiles(SecurityCamera cam,List<CoveredTile> piastrelle)
	{
		InstalledSecurityCamera thisone=get(cam);
		thisone.addValueTiles(piastrelle);
	}
	
	public void removeCoveredValueTiles(SecurityCamera cam,List<CoveredTile> piastrelle)
	{
		InstalledSecurityCamera thisone=get(cam);
		thisone.removeValueTiles(piastrelle);
	}
	
	public double getFitness()
	{
		return _fitness;
	}
	
	private void setFitness(double fitness) {
		_fitness = fitness;
		
	}
	
	public List<CoveredTile> getPiastrelleDiRischioCoperte(SecurityCamera cam)
	{
		InstalledSecurityCamera thisone=get(cam);
		List<CoveredTile> result = thisone.getCoveredRiskTiles();
		return result;
	}
	
	public List<CoveredTile> getPiastrelleDiValoreCoperte(SecurityCamera cam)
	{
		InstalledSecurityCamera thisone=get(cam);
		List<CoveredTile> result = thisone.getCoveredValueTiles();
		return result;
	}
	
	public List<SecurityCamera> getBestItemsOnUniqueRiskTiles(int quante)
	{
		List<SecurityCamera> result=getBestItems(new UniqueTilesComparator(),quante);
		return result;
	}

	public List<SecurityCamera> getBestItemsOnRiskTiles(int quante)
	{
		List<SecurityCamera> result=getBestItems(new SingleFitnessComparator(),quante);
		return result;
	}

	public List<SecurityCamera> getBestItemsOnUniqueRiskTilesBudgetConstrained(int budget)
	{
		List<SecurityCamera> working = getBestItems(new UniqueTilesComparator(),_telecamere.size());
		List<SecurityCamera> result = getBestItemsBudgetConstrained(working,budget);
		return result;
	}

	public List<SecurityCamera> getBestItemsOnRiskTilesBudgetConstrained(int budget)
	{
		List<SecurityCamera> working=getBestItems(new SingleFitnessComparator(),_telecamere.size());
		List<SecurityCamera> result = getBestItemsBudgetConstrained(working,budget);
		return result;
	}

	private List<SecurityCamera> getBestItemsBudgetConstrained(List<SecurityCamera> working,int budget)
	{
		List<SecurityCamera> result = new ArrayList<SecurityCamera>();
		int avanza = budget;
		Iterator<SecurityCamera> iter = working.iterator();
		do
		{
			SecurityCamera s = iter.next();
			result.add(s);
			avanza = (int) (avanza - s.getCost());
		}while(avanza>=0);
		return result;
	}
	private List<SecurityCamera> getBestItems(Comparator<InstalledSecurityCamera> thisComparator,int quante)
	{
		List<SecurityCamera> result = new ArrayList<SecurityCamera>();
		Collections.sort(_telecamere,thisComparator.reversed());
		int totale = quante;
		if(_telecamere.size() <= quante)
		{
			totale = _telecamere.size();
		}
		List<InstalledSecurityCamera> working=_telecamere.subList(0, totale);
		
		for(InstalledSecurityCamera elemento:working)
		{
			SecurityCamera elementoValutato=new SecurityCamera(elemento);
			result.add(elementoValutato);
		}
		return result;
	}
	
	private InstalledSecurityCamera get(SecurityCamera cam)
	{
		for(InstalledSecurityCamera thisone:_telecamere)
		{
			if(thisone.equals(cam))
			{
				return thisone;
			}
		}
		return null;
	}
	
	public String cameraPositionString()
	{
		StringBuffer st = new StringBuffer();
		for(InstalledSecurityCamera cam:_telecamere)
		{
			st.append(cam.positionString());
			st.append(CameraPositioningParameters.STR_COMMA);
		}
		return st.toString();
	}
	////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	
	private double calcolaRischioCoperto()
	{
		double riskValue = 0;
		for(InstalledSecurityCamera cam:_telecamere)
		{
			double riskCoveredByThisOne = cam.getCoperturaRischi();
			riskValue = riskValue + riskCoveredByThisOne;
		}
		return riskValue;
	}
	
	private double calcolaCostoInstallazione()
	{
		double result = 0;
		for(InstalledSecurityCamera cam:_telecamere)
		{
			double costOfThisOne = cam.getCost();
			
			
			result = result + costOfThisOne;
		}
		return result;
	}
	
	private double calcolaFitness()
	{
		double result = calcolaRischioCoperto();
		result = result - calcolaCostoInstallazione();
		return result;
	}
	
	////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	
	public static Individual parseJSon(String jsonobj) throws Exception
	{
		JSONObject input = new JSONObject(jsonobj);
		Individual obj=new IndividualJSONFactory().fromJSonObject(input);
		return obj;
	}
	
	public static Individual fromJSonObject(JSONObject input) throws Exception
	{
		Individual obj=new IndividualJSONFactory().fromJSonObject(input);
		return obj;
	}
	
	public JSONObject toJSonObject()
	{
		JSONObject root = new IndividualJSONFactory().toJSonObject(this);
		return root;
	}
	
	////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	
	private class SingleFitnessComparator implements Comparator<InstalledSecurityCamera> {
        public int compare(InstalledSecurityCamera i1, InstalledSecurityCamera i2) {
            Double f1 = i1.getCoperturaRischi();
            Double f2 = i2.getCoperturaRischi();
            return f1.compareTo(f2);
        }
    }
	
	private class UniqueTilesComparator implements Comparator<InstalledSecurityCamera> {
        public int compare(InstalledSecurityCamera i1, InstalledSecurityCamera i2) {
            Double f1 = i1.getSingleFitnessUniqueTiles();
            Double f2 = i2.getSingleFitnessUniqueTiles();
            return f1.compareTo(f2);
        }
    }
}
