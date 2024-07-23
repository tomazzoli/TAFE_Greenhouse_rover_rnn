package it.tomazzoli.ai.bim.geneticalgorithm.process.breeding.random;

import java.util.List;

import it.tomazzoli.ai.bim.beans.SecurityCamera;
import it.tomazzoli.ai.bim.geneticalgorithm.Individual;

public class AlcoveRandomBestFatherSingleMotherUnique extends RandomAlcove 
{

	public AlcoveRandomBestFatherSingleMotherUnique() 
	{
		super();
	}

	/***
	 *  un individuo con metà circa degli elementi da ciascun genitore
	 *  prende dal secondo una scelta di telecamere basata sulla copertura di rischio univoca
	 *  della singola telecamera mentre dal primo  una scelta di telecamere basata sulla singola performance
	 * 	della singola telecamera
	 * @param padre
	 * @param madre
	 * @return un individuo con metà circa degli elementi da ciascun genitore
	 */
	@Override
	public Individual figlio(Individual padre, Individual madre) 
	{
		BreedingParameters param=new BreedingParameters(padre);
		
		List<SecurityCamera> telecamerePadre=padre.getBestItemsOnRiskTiles(param.quantiElementiDaCuiPartire);
		List<SecurityCamera> telecamereMadre=madre.getBestItemsOnUniqueRiskTiles(param.quantiElementiDaCuiPartire);

		Individual result=incubatrice(telecamerePadre,telecamereMadre,param.quantiElementiDalPadre,param.quantiElementiDallaMadre);
		return result;
	}

}
