package it.tomazzoli.ai.bim.geneticalgorithm;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import it.tomazzoli.ai.bim.beans.SecurityCamera;
import it.tomazzoli.ai.bim.geneticalgorithm.process.DarwinianSelector;
import it.tomazzoli.ai.bim.geneticalgorithm.process.IndividualGenerator;
import it.tomazzoli.ai.bim.geneticalgorithm.process.MutationActuator;
import it.tomazzoli.ai.bim.utils.CameraPositioningParameters;

public class ProblemSolver 
{
	private final BuiltEnvironment _bimenv;
	private int quantiIndividuiInPopolazioneIniziale =10;
	private int quantiNeTengoInPercentuale=10;
	private int maxNumGeneration=5;
	private final String quantiIndividuiParam = "CameraPositioning.quantiIndividuiInPopolazioneIniziale";
	private final String quantiNeTengoInPercentualeParam = "CameraPositioning.quantiNeTengoInPercentuale";
	private final String maxNumGenerationParam = "CameraPositioning.maxNumGeneration";
	
	
	public ProblemSolver(BuiltEnvironment bimenv) 
	{
		_bimenv=bimenv;
		int leggo = CameraPositioningParameters.getInt(quantiIndividuiParam);
		if(leggo>0)
		{
			quantiIndividuiInPopolazioneIniziale =leggo;
		}
		leggo = CameraPositioningParameters.getInt(quantiNeTengoInPercentualeParam);
		if(leggo>0)
		{
			quantiNeTengoInPercentuale=leggo;
		}
		leggo = CameraPositioningParameters.getInt(maxNumGenerationParam);
		if(leggo>0)
		{
			maxNumGeneration=leggo;
		}
		
	}
	
	public List<SecurityCamera> findSolution(SecurityCamera baseCamera,int budget)
	{
		IndividualGenerator generator=new IndividualGenerator(_bimenv);
		DarwinianSelector selector=new DarwinianSelector(_bimenv);
		MutationActuator mutator=new MutationActuator(_bimenv);
		
		/**
		 *  creo la prima popolazione
		 */
		List<Individual> polazioneIniziale=generator.initilaPolulation(baseCamera,quantiIndividuiInPopolazioneIniziale,budget);
		System.out.printf("Generata la popolazione iniziale di %d possibili soluzioni \n",polazioneIniziale.size());
		polazioneIniziale=selector.naturalSelection(polazioneIniziale, quantiIndividuiInPopolazioneIniziale);
		System.out.printf("Selezionata la popolazione iniziale e ridotta alle migliori %d possibili soluzioni \n",polazioneIniziale.size());
		/**
		 *  creo la prima generazione dalla popolaizone iniziale
		 */
		List<Individual> primaGenerazione=generator.crossover(polazioneIniziale);
		System.out.printf("Generata la prima generazione di %d possibili soluzioni \n",primaGenerazione.size());
		
		/**
		 *  la prima generazione è la somma della popolazione iniziale e dei loro figli
		 */
		primaGenerazione.addAll(polazioneIniziale);
		
		int quantiNeTengoInTotaleAdOgniGenerazione = Math.round(primaGenerazione.size()*quantiNeTengoInPercentuale/100);
		
		if(quantiNeTengoInTotaleAdOgniGenerazione < quantiIndividuiInPopolazioneIniziale)
		{
			quantiNeTengoInTotaleAdOgniGenerazione = primaGenerazione.size();
		}
		
		/**
		 *  li valuto tutti e quindi trovo il migliore
		 */
		List<Individual> rimanenti=selector.naturalSelection(primaGenerazione, quantiNeTengoInTotaleAdOgniGenerazione);
		Individual best=selector.best(rimanenti);
		System.out.printf("Effettuata la prima selezione, rimangono  %d possibili soluzioni \n",rimanenti.size());
		System.out.printf(Locale.ITALY,"La migliore soluzione finora ha fitness %,d %n e contiene %d telecamere \r\n %s \n",Math.round(best.getFitness()),best.getCameras().size(),best.toJSonObject().toString());
		
		/**
		 *  a questo punto parte il vero e proprio algoritmo genetico
		 */
		for(int i=0;i<maxNumGeneration;i++)
		{
			List<Individual> genitori= new ArrayList<Individual>();
			genitori.addAll(rimanenti);
			System.out.printf("Sono alla generazione %d, ho  %d possibili soluzioni \n",i+1,genitori.size());
			/**
			 * riproduco i migliori
			 */
			List<Individual> figli=generator.crossover(genitori);
			/**
			 * impongo le mutaiozni sui figli
			 */
			figli=mutator.imponiMutazioni(figli);
			
			System.out.printf(Locale.ITALY,"Sono alla generazione %d, ho  generato e mutato %d possibili soluzioni \r\n %s \n",i+1,figli.size(),mutator.stringaContatori());
			
			/**
			 *  la attuale generazione è la somma della popolazione iniziale e dei loro figli
			 */
			List<Individual> generazioneCorrente= new ArrayList<Individual>();
			generazioneCorrente.addAll(genitori);
			generazioneCorrente.addAll(figli);
			/**
			 * applico la selezione darwiniana
			 */
			rimanenti=selector.naturalSelection(generazioneCorrente, quantiNeTengoInTotaleAdOgniGenerazione);
			System.out.printf("Sono alla generazione %d, ho  selezionato e rimangono %d possibili soluzioni \n",i+1,rimanenti.size());
			/**
			 * prendo il migliore e lo confronto con il migliore finora, ne esce il migliore di tutti finora
			 */
			Individual bestOfThisGeneration = selector.best(rimanenti);
			System.out.printf(Locale.ITALY,"La migliore soluzione finora ha fitness %,d %n , quella di questa generazione ha fitness %,d %n \n",Math.round(best.getFitness()),Math.round(bestOfThisGeneration.getFitness()));
			System.out.printf(Locale.ITALY,"La migliore soluzione di questa generazione contiene %d telecamere:  %s \n",bestOfThisGeneration.getCameras().size(),bestOfThisGeneration.cameraPositionString());
			if(bestOfThisGeneration.getFitness()>best.getFitness())
			{
				best=bestOfThisGeneration;
				System.out.printf(Locale.ITALY,"La migliore soluzione finora ha fitness %,d %n e contiene %d telecamere \r\n %s \n",Math.round(best.getFitness()),best.getCameras().size(),best.cameraPositionString());
			}
		}
		List<SecurityCamera> result=best.getCameras();
		return result;
	}

}
