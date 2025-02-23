package it.tomazzoli.ai.bim.geneticalgorithm.process.breeding.random;

import java.util.List;

import it.tomazzoli.ai.bim.beans.SecurityCamera;
import it.tomazzoli.ai.bim.geneticalgorithm.Individual;

public class AlcoveRandomBestFatherUniqueMotherSingle extends RandomAlcove 
{

	public AlcoveRandomBestFatherUniqueMotherSingle() 
	{
		super();
	}

	/***
	 *  un individuo con metà circa degli elementi da ciascun genitore
	 *  prende dal primo una scelta di telecamere basata sulla copertura di rischio univoca
	 *  della singola telecamera mentre dal secondo  una scelta di telecamere basata sulla singola performance
	 * 	della singola telecamera
	 * @param padre
	 * @param madre
	 * @return un individuo con metà circa degli elementi da ciascun genitore
	 */
	@Override
	public Individual figlio(Individual padre, Individual madre) 
	{
		BreedingParameters param=new BreedingParameters(padre);
		
		List<SecurityCamera> telecamerePadre=padre.getBestItemsOnUniqueRiskTiles(param.quantiElementiDaCuiPartire);
		List<SecurityCamera> telecamereMadre=madre.getBestItemsOnRiskTiles(param.quantiElementiDaCuiPartire);

		Individual result=incubatrice(telecamerePadre,telecamereMadre,param.quantiElementiDalPadre,param.quantiElementiDallaMadre);
		return result;
	}

}
