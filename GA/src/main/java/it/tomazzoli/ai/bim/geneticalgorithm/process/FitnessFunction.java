package it.tomazzoli.ai.bim.geneticalgorithm.process;

import it.tomazzoli.ai.bim.geneticalgorithm.BuiltEnvironment;
import it.tomazzoli.ai.bim.geneticalgorithm.Individual;
import it.tomazzoli.ai.bim.geneticalgorithm.process.fitness.PerformanceEvaluator;


public class FitnessFunction extends PerformanceEvaluator
{
	
	public FitnessFunction(BuiltEnvironment ambiente) 
	{
		super(ambiente);
	}
	
	public double fitness(Individual individuo)
	{
		double result=individuo.getFitness();
		return result;
	}

}
