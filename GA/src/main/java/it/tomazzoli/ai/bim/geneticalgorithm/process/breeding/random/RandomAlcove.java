package it.tomazzoli.ai.bim.geneticalgorithm.process.breeding.random;

import java.util.ArrayList;
import java.util.List;

import it.tomazzoli.ai.bim.beans.SecurityCamera;
import it.tomazzoli.ai.bim.geneticalgorithm.Individual;
import it.tomazzoli.ai.bim.geneticalgorithm.process.breeding.Alcove;
import it.tomazzoli.ai.bim.geneticalgorithm.process.fitness.InstalledSecurityCamera;

public abstract class RandomAlcove extends Alcove {

	public RandomAlcove() 
	{
		super();
	}

	public abstract Individual figlio(Individual padre, Individual madre);
	
	/***
	 * unisce una selezione casuale di elementi da due liste
	 * @param telecamerePadre prima lista di elementi
	 * @param telecamereMadre seconda  lista di elementi
	 * @param quantiElementiDalPadre lo dice il nome
	 * @param quantiElementiDallaMadre lo dice il nome
	 * @return una selezione casuale di elementi da due liste
	 */
	@Override
	protected Individual incubatrice(List<SecurityCamera> telecamerePadre,List<SecurityCamera> telecamereMadre,int quantiElementiDalPadre,int quantiElementiDallaMadre)
	{
		List<SecurityCamera> elementi=new ArrayList<SecurityCamera>();
		
		telecamerePadre=aCaso(telecamerePadre,quantiElementiDalPadre,elementi); // nota, elementi al momento è una lista vuota
		telecamereMadre=aCaso(telecamereMadre,quantiElementiDallaMadre,telecamerePadre);
		elementi.addAll(telecamerePadre);
		elementi.addAll(telecamereMadre);
		Individual result=new Individual(elementi);
		return result;
	}

}
