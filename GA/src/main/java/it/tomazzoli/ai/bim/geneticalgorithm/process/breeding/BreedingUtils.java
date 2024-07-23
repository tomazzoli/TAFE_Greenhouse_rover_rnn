package it.tomazzoli.ai.bim.geneticalgorithm.process.breeding;

import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;
import java.util.Random;
import java.util.Set;

import it.tomazzoli.ai.bim.geneticalgorithm.Individual;

public class BreedingUtils 
{
	private int maxAccoppiamenti;
	private Random rand;

	public BreedingUtils(int maxAccoppiamenti) 
	{
		this.maxAccoppiamenti=maxAccoppiamenti;
		rand = new Random();
	}

	/***
	 * Restituisce un Set di Individui contenente due individui, ovvero i parenti da fare accoppiare
	 * Verifica che ci siano ancora individui che non si sono accoppiati un numero eccessivo di volte.
	 * Se non è possibile formare la coppia ritorno null
	 * @param population
	 * @param alreadycoupled
	 * @return un Set di Individui contenente due individui, ovvero i parenti,
	 * 			oppure null se ho finito la popolazione disponibile
	 */
	public Set<Individual> matingCouple(List<Individual> population, List<Set<Individual>> alreadycoupled)
	{
		Set<Individual> result=new HashSet<Individual>();
		Hashtable<Individual,Integer> riproduttori=contaRiproduzioni(alreadycoupled);
		Random rand=new Random();
		for(Individual questo:population)
		{
			boolean mating=rand.nextBoolean();
			if(mating)
			{
				boolean nonesagera=ancoraRiproduttore(questo,riproduttori);
				if(nonesagera)
				{
					result.add(questo);
					if(result.size()>1)
					{
						return result;
					}
				}
			}
		}
		// se sono arrivato qui ho finito la popolazione
		return null;
	}

	/***
	 * Data una lista di coppie che si sono riprodotte, restituisce una tabella di individui
	 * e quante volte si  è riprodotto ciascun individuo
	 * @param alreadycoupled lista di coppie che si sono riprodotte
	 * @return tabella a due colonne <Individuo, quantevolteriprodotto>
	 */
	private Hashtable<Individual,Integer> contaRiproduzioni(List<Set<Individual>> alreadycoupled)
	{
		Hashtable<Individual,Integer> result=new Hashtable<Individual,Integer>();
		for(Set<Individual> set:alreadycoupled)
		{
			for(Individual questo:set)
			{
				if(result.containsKey(questo))
				{
					int quante=result.get(questo)+1;
					result.remove(questo);
					result.put(questo,quante);
				}
				else
				{
					result.put(questo,1); // essendo la prima volta che lo vedo
				}
			}
		}
		return result;
	}

	/***
	 * Restituisce true se l'individuo non appartiene a quelli che si sono già riprodotti
	 * o se si è riprodotto al massimo un mumero accettabile (attributo di classe) di volte
	 * @param questo l'individuo da giuducare
	 * @param riproduttori gli individui che si sono accoppiati ed il numero di volte che lo hanno fatto
	 * @return
	 */
	private boolean ancoraRiproduttore(Individual questo,Hashtable<Individual,Integer> riproduttori)
	{
		boolean result = true;
		if(riproduttori.containsKey(questo))
		{
			int quanteVolteFigliolo = riproduttori.get(questo);
			if(quanteVolteFigliolo > maxAccoppiamenti)
			{
				result = false;
			}
		}
		return result;
	}

}
