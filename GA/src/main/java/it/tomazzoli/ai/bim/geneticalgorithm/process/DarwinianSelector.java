package it.tomazzoli.ai.bim.geneticalgorithm.process;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import it.tomazzoli.ai.bim.geneticalgorithm.BuiltEnvironment;
import it.tomazzoli.ai.bim.geneticalgorithm.Individual;
import it.tomazzoli.ai.bim.geneticalgorithm.process.fitness.PerformanceEvaluator;

public class DarwinianSelector 
{
	private BuiltEnvironment _ambiente;
	
	public DarwinianSelector(BuiltEnvironment ambiente) 
	{
		_ambiente=ambiente;
	}
	
	/***
	 * 
	 * @param population
	 * @param numeroMassimo un intero positivo che indica il numero massimo di individui da tenere
	 * @return i migliori threshold (percentuale)
	 */
	public List<Individual> naturalSelection(List<Individual> population,int numeroMassimo)
	{
		List<Individual> working=new ArrayList<Individual>();
		
		for(Individual individuo:population)
		{
			working.add(individuo);
		}
		
		Collections.sort(working,new FitnessComparator().reversed());
		
		List<Individual> result=working.subList(0, numeroMassimo);
		
		return result;
	}
	
	
	public Individual best(List<Individual> population)
	{
		List<Individual> working=new ArrayList<Individual>();
		working.addAll(population);
		Collections.sort(working,new FitnessComparator().reversed());
		Individual result= working.get(0);
		return result;
		
	}
	
	private class FitnessComparator implements Comparator<Individual> {
        public int compare(Individual i1, Individual i2) {
            Double f1 = i1.getFitness();
            Double f2 = i2.getFitness();
            return f1.compareTo(f2);
        }
    }
}
