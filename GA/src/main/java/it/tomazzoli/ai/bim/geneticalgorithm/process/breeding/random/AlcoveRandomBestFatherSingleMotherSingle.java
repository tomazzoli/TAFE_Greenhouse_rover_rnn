package it.tomazzoli.ai.bim.geneticalgorithm.process.breeding.random;

import java.util.List;

import it.tomazzoli.ai.bim.beans.SecurityCamera;
import it.tomazzoli.ai.bim.geneticalgorithm.Individual;

public class AlcoveRandomBestFatherSingleMotherSingle extends RandomAlcove 
{

	public AlcoveRandomBestFatherSingleMotherSingle() 
	{
		super();
	}

	
	/***
	 *  un individuo con metà circa degli elementi da ciascun genitore
	 *  prende da entrambe una scelta di telecamere basata sulla singola performance
	 *  della singola telecamera
	 * @param padre
	 * @param madre
	 * @return un individuo con metà circa degli elementi da ciascun genitore
	 */
	@Override
	public Individual figlio(Individual padre, Individual madre) 
	{
		BreedingParameters param=new BreedingParameters(padre);
		
		List<SecurityCamera> telecamerePadre=padre.getBestItemsOnRiskTiles(param.quantiElementiDaCuiPartire);
		List<SecurityCamera> telecamereMadre=madre.getBestItemsOnRiskTiles(param.quantiElementiDaCuiPartire);

		Individual result=incubatrice(telecamerePadre,telecamereMadre,param.quantiElementiDalPadre,param.quantiElementiDallaMadre);
		return result;
	}

}
