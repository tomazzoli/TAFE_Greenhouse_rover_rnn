package it.tomazzoli.ai.bim.geneticalgorithm.process.fitness;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import it.tomazzoli.ai.bim.beans.CoveredTile;
import it.tomazzoli.ai.bim.beans.SecurityCamera;
import it.tomazzoli.ai.bim.geneticalgorithm.BuiltEnvironment;
import it.tomazzoli.ai.bim.geneticalgorithm.Individual;

public class UniqueCoverageEvaluator extends PerformanceEvaluator 
{
	public UniqueCoverageEvaluator(BuiltEnvironment ambiente)
	{
		super(ambiente);
	}

	
	public Individual elaboraCoperturaTelecamere(Individual individuo)
	{
		List<SecurityCamera> telecamere = individuo.getCameras();
		Individual result = new Individual(telecamere);
		List<InstalledSecurityCamera> telecamereValorizzate = new ArrayList<InstalledSecurityCamera>();
		
		for(SecurityCamera cam:telecamere)
		{
			List<CoveredTile> rischi = individuo.getPiastrelleDiRischioCoperte(cam);
			List<CoveredTile> valori = individuo.getPiastrelleDiValoreCoperte(cam);
			InstalledSecurityCamera valorizzata = new InstalledSecurityCamera(cam);
			valorizzata.addRiskTiles(rischi);
			valorizzata.addValueTiles(valori);
			telecamereValorizzate.add(valorizzata);
		}
		
		List<InstalledSecurityCamera> telecamereConTuttoElaborato = uniqueTilePerformance(telecamereValorizzate);
		
		for(InstalledSecurityCamera telec:telecamereConTuttoElaborato)
		{
			result.addCoveredRiskTiles(telec, telec.getCoveredRiskTiles());
			result.addUniqueCoveredRiskTiles(telec, telec.getUniqueCoveredRiskTTiles());
			result.addCoveredValueTiles(telec, telec.getCoveredValueTiles());
		}
		
		return result;
		
	}
	
	/***
	 * Calcola per ogni telecamera il valore di rischio delle aree coperte solamente da questa telecamera, ne restituisce una tabella <telecamera, valore>
	 * @param telecamere
	 * @return List<InstalledSecurityCamera> delle telecamere in cui il viene aggiunto valore di rischio coperto dalla telecamera e solo da questa
	 */
	public List<InstalledSecurityCamera> uniqueTilePerformance(List<InstalledSecurityCamera> telecamere)
	{
		List<InstalledSecurityCamera> result = new ArrayList<InstalledSecurityCamera>();
		/**
		 * ottengo la tabella di tutte le tabelle coperte da tutte le telecamere
		 */
		Hashtable<CoveredTile,List<InstalledSecurityCamera>> covered=piastrelleCoperte(telecamere);
		/**
		 * ottengo la tabella di tutte le piastrelle di cui ogni telecamera è l'unica ad effettuare la copertura
		 */
		Hashtable<InstalledSecurityCamera,List<CoveredTile>> copertura_x_telecamera=coperturaUnivocaDiOgniTelecamera(covered);
		/**
		 * per ogni telecamera, calcolo la somma dei valori delle piastrelle di cui è l'unica ad effettuare la copertura
		 */
		for(InstalledSecurityCamera cam:copertura_x_telecamera.keySet())
		{
			InstalledSecurityCamera valutata = new InstalledSecurityCamera(cam);
			valutata.addRiskTiles(cam.getCoveredRiskTiles());
			valutata.addValueTiles(cam.getCoveredValueTiles());
			List<CoveredTile> uniqueTiles = copertura_x_telecamera.get(cam);
			valutata.addUniqueRiskTiles(uniqueTiles);
			result.add(valutata);
		}
		return result;
	}
	
	/***
	 * Restituisce la tabella di tutte le piastrelle di cui ogni telecamera è l'unica ad effettuare la copertura
	 * @param covered
	 * @return una tabella <telecamera,lista di piastrelle>
	 */
	private Hashtable<InstalledSecurityCamera,List<CoveredTile>> coperturaUnivocaDiOgniTelecamera(Hashtable<CoveredTile,List<InstalledSecurityCamera>> covered)
	{
		Hashtable<InstalledSecurityCamera,List<CoveredTile>> result=new Hashtable<InstalledSecurityCamera,List<CoveredTile>>();
		for(CoveredTile tile:covered.keySet())
		{
			List<InstalledSecurityCamera> lista=covered.get(tile);
			if(lista.size()==1)
			{
				InstalledSecurityCamera a=lista.get(0);
				List<CoveredTile> listaTiles=new ArrayList<CoveredTile>();
				if(result.containsKey(a))
				{
					listaTiles.addAll(result.get(a));
					listaTiles.add(tile);
					result.remove(a);
				}
				else
				{
					
					listaTiles.add(tile);
				}
				result.put(a,listaTiles);
			}
		}
		return result;
	}
	
	/***
	 * la tabella di tutte le piastrelle coperte da telecamere
	 * @param telecamere le telecamere che possono effettuare la copertura
	 * @return una tabella <piastrelle, lista di telecamera> di tutte le piastrelle coperte dalle telecamere
	 */
	private Hashtable<CoveredTile,List<InstalledSecurityCamera>> piastrelleCoperte(List<InstalledSecurityCamera> telecamere)
	{
		/**
		 * prendo tutte le piastrelle
		 */
		List<CoveredTile> tobecovered=allRiskTiles();
		/**
		 * ottengo la tabella di tutte le tabelle coperte da tutte le telecamere
		 */
		Hashtable<CoveredTile,List<InstalledSecurityCamera>> result=verificaCopertura(telecamere,tobecovered);
	
		return result;
	}
	
	/***
	 * Restituisce la tabella di tutte le piastrelle coperte da tutte le telecamere
	 * @param telecamere tutte le telecamere
	 * @param tobecovered tutte le piastrelle da coprire
	 * @return una tabella <piastrelle, lista di telecamera> di tutte le piastrelle coperte dalle telecamere
	 */
	private Hashtable<CoveredTile,List<InstalledSecurityCamera>> verificaCopertura(List<InstalledSecurityCamera> telecamere,List<CoveredTile> tobecovered)
	{
		Hashtable<CoveredTile,List<InstalledSecurityCamera>> result=new Hashtable<CoveredTile,List<InstalledSecurityCamera>>();
		for(InstalledSecurityCamera cam:telecamere)
		{
			for(CoveredTile a:cam.getCoveredRiskTiles())
			{
				if(result.containsKey(a))
				{
						List<InstalledSecurityCamera> lista=result.get(a);
						lista.add(cam);
						result.remove(a);
						result.put(a,lista);
				}
				else
				{
						List<InstalledSecurityCamera> lista=new ArrayList<InstalledSecurityCamera>();
						lista.add(cam);
						result.put(a,lista);
				}
			}
		}
		return result;
	}
}
