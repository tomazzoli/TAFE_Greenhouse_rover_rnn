package it.tomazzoli.ai.bim.geneticalgorithm.process.breeding;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import it.tomazzoli.ai.bim.beans.SecurityCamera;
import it.tomazzoli.ai.bim.geneticalgorithm.Individual;

public abstract class Alcove 
{
	private Random randomizer;
	
	protected Alcove() 
	{
		randomizer = new Random(System.currentTimeMillis());
	}
	
	public abstract Individual figlio(Individual padre, Individual madre);
	
	
	protected abstract Individual incubatrice(List<SecurityCamera> telecamerePadre,List<SecurityCamera> telecamereMadre,int quantiElementiDalPadre,int quantiElementiDallaMadre);
	
	
	/***
	 * Restituisce le prime n istanze a caso di una lista
	 * @param telecamere
	 * @param rand
	 * @param quante
	 * @return le prime n istanze a caso della lista di input
	 */
	protected List<SecurityCamera> aCaso(List<SecurityCamera> telecamere,int quanteNeVorrei,List<SecurityCamera> daEscludere)
	{
		List<SecurityCamera> result=new ArrayList<SecurityCamera>();
		List<SecurityCamera> working=new ArrayList<SecurityCamera>();
		for(SecurityCamera cam:telecamere)
		{
			if(!daEscludere.contains(cam))
			{
				working.add(cam);
			}
		}
		
		if(working.size() > quanteNeVorrei) 
		{
			while(result.size() < quanteNeVorrei)
			{
				for(SecurityCamera cam:working)
				{
					boolean b=randomizer.nextBoolean();
					if((b) && (result.size() < quanteNeVorrei))
					{
						result.add(cam);
					}
				}
			}
		}
		else              // inutile discutere oltre, le devo mettere tutte....
		{
			result.addAll(working);
		}
		
		return result;
	}
	
	protected class BreedingParameters
	{
		public final int quantiElementiDaCuiPartire;
		public final int quantiElementiDalPadre;
		public final int quantiElementiDallaMadre;
		
		public BreedingParameters(Individual padre)
		{
			quantiElementiDaCuiPartire=padre.getCameras().size(); // li tengo tutti, tanto poi li limito
			quantiElementiDalPadre=padre.getCameras().size()/2;
			quantiElementiDallaMadre=padre.getCameras().size()-quantiElementiDalPadre;
		}
		
	}

}
