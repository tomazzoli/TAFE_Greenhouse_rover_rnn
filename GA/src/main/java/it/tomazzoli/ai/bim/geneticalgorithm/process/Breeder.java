package it.tomazzoli.ai.bim.geneticalgorithm.process;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.locationtech.jts.geom.Envelope;

import it.tomazzoli.ai.bim.beans.SecurityCamera;
import it.tomazzoli.ai.bim.geneticalgorithm.BuiltEnvironment;
import it.tomazzoli.ai.bim.geneticalgorithm.Individual;
import it.tomazzoli.ai.bim.geneticalgorithm.process.breeding.Alcove;
import it.tomazzoli.ai.bim.geneticalgorithm.process.breeding.AlcoveFactory;
import it.tomazzoli.ai.bim.geneticalgorithm.process.breeding.BreedingUtils;
import it.tomazzoli.ai.bim.geneticalgorithm.process.fitness.PerformanceEvaluator;
import it.tomazzoli.ai.bim.geneticalgorithm.process.fitness.SingleItemEvaluator;
import it.tomazzoli.ai.bim.geneticalgorithm.process.fitness.UniqueCoverageEvaluator;
import it.tomazzoli.ai.bim.utils.CameraPositioningParameters;

public class Breeder 
{
	private BuiltEnvironment _ambiente;
	private int maxAccoppiamenti=3;
	private final String maxAccoppiamentiParam = "CameraPositioning.maxAccoppiamenti";
	private BreedingUtils _nursery;
	
	public Breeder(BuiltEnvironment ambiente) 
	{
		_ambiente=ambiente;
		int leggo = CameraPositioningParameters.getInt(maxAccoppiamentiParam);
		if(leggo>0)
		{
			maxAccoppiamenti=leggo;
		}
		_nursery= new BreedingUtils(maxAccoppiamenti);
	}
	
	/***
	 * Restituisce una nuova generazione, realizzando la riproduzione di ogni elemento di questa generazione:
	 * crea le coppie ( stando attento a fare in modo che ogni individuo si acooppi almeno una volta e che nessuno si accoppi troppe volte )
	 * e ne genera per ciascuna un certo numero di figli; ogni figlio ha caratteristiche diverse dall'altro.
	 * Le caratteristiche (ovvero le Telecamere @see SecurityCamera) che passano ai figli vengono scelte valutando le migliori telecamere di ciascun genitore 
	 * oppure evitando sovrapposizioni tra telecamere del padre e della madre
	 * @param parents  una lista di @see it.tomazzoli.ai.bim.geneticalgorithm.Individual
	 * @return una lista di @link{it.tomazzoli.ai.bim.geneticalgorithm.Individual} individui generati a partire da una lista di @see it.tomazzoli.ai.bim.geneticalgorithm.Individual
	 */
	public List<Individual> crossover(List<Individual> parents)
	{	
		List<Individual> result=new ArrayList<Individual>();
		/**
		 * Valuta, per ogni individuo,  ogni componente in base alle piastrelle che questo copre ed a quelle che lui solo copre
		 */
		List<Individual> valutati=valutaSingoliItem(parents);
		/**
		 * crea le coppie ( stando attento a fare in modo che ogni individuo si acooppi almeno una volta e che nessuno si accoppi troppe volte )
		 * e ne genera per ciascuna un certo numero di figli; ogni figlio ha caratteristiche diverse dall'altro
		 */
		boolean ancora=true;
		List<Set<Individual>> alreadycoupled=new ArrayList<Set<Individual>>();
		while (ancora)
		{
				/**
				 * crea la coppia riproduttrice stando attento a fare in modo che ogni individuo si acooppi almeno una volta e che nessuno si accoppi troppe volte
				 * se non ci riesce vuol dire che tutti si sono accoppiati almeno una volta
				 */
				Set<Individual> coppia=matingCouple(valutati,alreadycoupled);
				if(coppia!=null)
				{
					/**
					 * genera un certo numero di figli; ogni figlio ha caratteristiche diverse dall'altro
					 */
					List<Individual> daAggiungere=figli(coppia);
					/**
					 * segna la coppia come già riproduttrice  ed aggiunge i figli alla generazione
					 */
					alreadycoupled.add(coppia);
					result.addAll(daAggiungere);
				}
				else
				{
					ancora = false;
				}
		}
		return result;
	}
	
	/***
	 * Restituisce una lista di Individual dove ogni componente è stato valutato in base alle piastrelle che copre ed a quelle che lui solo copre
	 * @param parents una lista di  @see it.tomazzoli.ai.bim.geneticalgorithm.Individual
	 * @return la lsita in cui ogni compomente è stato valutato per il valore che porta
	 */
	private List<Individual> valutaSingoliItem(List<Individual> parents)
	{	
		PerformanceEvaluator valutatore = new PerformanceEvaluator(_ambiente);
		List<Individual> result = valutatore.valutaSingoliItem(parents);
		return result;
		
	}
	
	/***
	 * Restituisce un Set di Individui contenente due individui, ovvero i parenti da fare accoppiare
	 * Verifica che ci siano ancora individui che non si sono accoppiati un numero eccessivo di volte.
	 * Se non è possibile formare la coppia ritorno null
	 * @param population una lista di  @see it.tomazzoli.ai.bim.geneticalgorithm.Individual
	 * @param alreadycoupled una lista di coppie
	 * @return un Set di Individui contenente due individui, ovvero i parenti,
	 * 			oppure null se ho finito la popolazione disponibile
	 */
	private Set<Individual> matingCouple(List<Individual> population, List<Set<Individual>> alreadycoupled)
	{
		Set<Individual> result =_nursery.matingCouple(population, alreadycoupled);
		return result;
	}
	
	/***
	 * Restituisce una lista di elementi creati a partire dai due parenti in input
	 * @param coppia un Set di Individui contenente due individui, ovvero i parenti
	 * @return una lista di   @see it.tomazzoli.ai.bim.geneticalgorithm.Individual
	 */
	private List<Individual> figli(Set<Individual> coppia)
	{
		List<Individual> result=new ArrayList<Individual>();
		Iterator<Individual> iter=coppia.iterator();
		Individual padre=iter.next();
		Individual madre=iter.next();
		
		AlcoveFactory alcoveFactory=new AlcoveFactory();
		for(String s:alcoveFactory.possibiliRiproduzioni())
		{
			Alcove talamo = alcoveFactory.getInstance(s);
			Individual figlio= talamo.figlio(padre, madre);
			result.add(figlio);
		}
		
		return result;
	}
	
}
