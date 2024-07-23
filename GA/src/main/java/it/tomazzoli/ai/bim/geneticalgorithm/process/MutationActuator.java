package it.tomazzoli.ai.bim.geneticalgorithm.process;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.SplittableRandom;

import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Envelope;

import it.tomazzoli.ai.bim.beans.SecurityCamera;
import it.tomazzoli.ai.bim.geneticalgorithm.BuiltEnvironment;
import it.tomazzoli.ai.bim.geneticalgorithm.Individual;
import it.tomazzoli.ai.bim.geneticalgorithm.process.fitness.BestAngularDirectionEvaluator;
import it.tomazzoli.ai.bim.geneticalgorithm.process.fitness.PerformanceEvaluator;
import it.tomazzoli.ai.bim.geneticalgorithm.process.fitness.SingleItemEvaluator;
import it.tomazzoli.ai.bim.geneticalgorithm.process.fitness.UniqueCoverageEvaluator;
import it.tomazzoli.ai.bim.utils.PositioningUtil;

public class MutationActuator 
{
	private int percentMutazioni=10;
	private int percentSpostamento=30;
	private int percentCambioAngolo=30;
	private int percentTogliAggiungi=10;
	private int percentAggiungi=30;
	private int diQuantoLoSpostamentoInPercentoDelRaggio=100;
	
	private SplittableRandom rand;
	private final BuiltEnvironment _bimenv;
	private final PositioningUtil p_util;
	
	private int contaMutazioni;
	private int contaRotazioni;
	private int contaTraslazioni;
	private int contaAggiunte;
	private int contaRimozioni;
	private int totaleTelecamere;
	private int contaMutazioniTelecamere;
	
	private final BestAngularDirectionEvaluator _valutatore;
	
	public MutationActuator(BuiltEnvironment bimenv) 
	{
		rand = new SplittableRandom(System.currentTimeMillis());
		_bimenv = bimenv;
		p_util = new PositioningUtil(_bimenv);
		_valutatore = new BestAngularDirectionEvaluator(bimenv);
		azzeraContatori();
	}

	public List<Individual> imponiMutazioni(List<Individual> nascituri)
	{
		List<Individual> result = new ArrayList<Individual>();
		azzeraContatori();
		SingleItemEvaluator valutatore = new SingleItemEvaluator(_bimenv);
		UniqueCoverageEvaluator valutatoreDiCopertureUniche = new UniqueCoverageEvaluator(_bimenv);
		
		for(Individual candidato:nascituri)
		{
			if(avviene(percentMutazioni))
			{
				Individual mutato = applicaMutazioni(candidato);
				Individual parzialmenteValutato = valutatore.elaboraCoperturaTelecamere(mutato);
				Individual valutato = valutatoreDiCopertureUniche.elaboraCoperturaTelecamere(parzialmenteValutato);
				result.add(valutato);
				contaMutazioni++;
			}
			else
			{
				totaleTelecamere = totaleTelecamere + candidato.getCameras().size();
				result.add(candidato);
			}
		}
		System.out.printf("Ci sono state %d individui con mutazioni su un totale di %d individui \n",contaMutazioni,nascituri.size());
		return result;
	}
	
	/***
	 * Restituisce una lista di Individual dove ogni componente è stato valutato in base alle piastrelle che copre ed a quelle che lui solo copre
	 * @param parents una lista di  @see it.tomazzoli.ai.bim.geneticalgorithm.Individual
	 * @return la lsita in cui ogni compomente è stato valutato per il valore che porta
	 */
	private List<Individual> valutaSingoliItem(List<Individual> parents)
	{	
		PerformanceEvaluator valutatore = new PerformanceEvaluator(_bimenv);
		List<Individual> result = valutatore.valutaSingoliItem(parents);
		return result;
		
	}
	
	private Individual applicaMutazioni(Individual candidato)
	{
		List<SecurityCamera> telecamere = new ArrayList<SecurityCamera>();
		for(SecurityCamera originale:candidato.getCameras())
		{
			boolean mutazioneAvvenuta = false;
			totaleTelecamere++;
			SecurityCamera mutata = null;
			if(avviene(percentTogliAggiungi))
			{
				mutazioneAvvenuta = true;
				if(avviene(percentAggiungi))
				{
					SecurityCamera nuova = genera(originale,candidato); // la aggiungo 
					if(nuova!=null)
					{
						telecamere.add(nuova);
						contaAggiunte++;
					}
				}
				else// se la devo togliere (togliaggiungi ma non aggiungi, semplicemente non la aggiuno in uscita
				{
					contaRimozioni++;
					
				}
			}
			if(avviene(percentCambioAngolo))
			{
				// se la telecamera ha un angolo di 360 gradi la rotazione è ininfluente
				if(originale.getAmpiezzaAngolare()!=360)
				{
					SecurityCamera nuova = cambiaAngolo(originale);
					if(nuova!=null)
					{
						mutata = nuova;
						mutazioneAvvenuta = true;
						contaRotazioni++;
					}
				}
			}
			if(avviene(percentSpostamento))
			{
				if(mutata!=null)
				{
					mutata = sposta(mutata,candidato);
				}
				else
				{
					mutata = sposta(originale,candidato);
				}
				mutazioneAvvenuta = true;
				contaTraslazioni++;
			}
			
			if(mutazioneAvvenuta)
			{
				if(mutata!=null)
				{
					if(!telecamere.contains(mutata))
					{
						telecamere.add(mutata);
						contaMutazioniTelecamere++;
					}
				}
			}
			else
			{
				telecamere.add(originale);
			}
		}
		Individual result = new Individual(telecamere);
		return result;
	}
	
	private SecurityCamera cambiaAngolo(SecurityCamera telecamera)
	{
		// se la telecamera ha un angolo di 360 gradi la rotazione è ininfluente
		if(telecamera.getAmpiezzaAngolare()==360)
		{
			return telecamera;
		}
		Coordinate posizione= telecamera.getRegistrationPoint();
		List<Integer> angoliMigliori = _valutatore.direzioniMigliori(telecamera, posizione);
		
		int quale = rand.nextInt(angoliMigliori.size());
		
		int direzioneInGradi = angoliMigliori.get(quale);
		
		SecurityCamera result = new SecurityCamera(telecamera.getName(),telecamera.getRegistrationPoint(),telecamera.getRaggio(),direzioneInGradi,telecamera.getAmpiezzaAngolare());
		return result;
	}
	
	
	private Coordinate nuoveCoordinate(SecurityCamera telecamera, Individual individuo,int spostamentoPossibile)
	{
		Coordinate result = null;
		List<Envelope> piastrelleDisponibili = piastrelleLibere(individuo);
		PositioningUtil p_util = new PositioningUtil(_bimenv);
		
		for(int tentativi =0; tentativi < percentMutazioni; tentativi++)
		{
			double x= Math.round(telecamera.getRegistrationPoint().x+rand.nextInt((-1*spostamentoPossibile), spostamentoPossibile+1)); // aggiungo +1 per evitare il caso (-0,0)
			double y= Math.round(telecamera.getRegistrationPoint().y+rand.nextInt((-1*spostamentoPossibile), spostamentoPossibile+1));
			Coordinate point = new Coordinate(x,y);
			List<Envelope> piastrelleOccupateDaQuesto = p_util.occupate(piastrelleDisponibili, point, telecamera.getRaggio());
			for(Envelope occupataDaQuesto:piastrelleOccupateDaQuesto)
			{
				for(Envelope env:piastrelleDisponibili)
				{
					Coordinate libera = env.centre();
					if(occupataDaQuesto.covers(libera)) // il centro di una piastrella libera è all'interno di una delle piastrelle occupate da questa ipotetica telecamera, ovvero questa ipotetica telecamera copre almeno una piastrella lasciata libera
					{
						result = point;
						return result;
					}
				}
			}
		}
		// ci ho provato a spostarlo di poco, nnon ci sono riuscito = lo spostamento di poco non porta vantaggio => la genero nuova nuova
		if(piastrelleDisponibili.size()>0)
		{
			result = nuoveCoordinate(piastrelleDisponibili);
		}
		else
		{
			double x= Math.round(telecamera.getRegistrationPoint().x+rand.nextInt((-1*spostamentoPossibile), spostamentoPossibile+1)); // aggiungo +1 per evitare il caso (-0,0)
			double y= Math.round(telecamera.getRegistrationPoint().y+rand.nextInt((-1*spostamentoPossibile), spostamentoPossibile+1));
			result = new Coordinate(x,y);
		}
		
		return result;
	}
	
	/***
	 * Restituisce le coordinate di registrazione della telecamera in una piastrella libera
	 * @param individuo
	 * @return Le coordinate all'interno dell'area monitorata oppure null dopo un certo numero di tentativi
	 */
	private Coordinate nuoveCoordinate(Individual individuo)
	{
		Coordinate result = null;
		List<Envelope> piastrelleDisponibili = piastrelleLibere(individuo);
		if(piastrelleDisponibili.size()>0)
		{
			result = nuoveCoordinate(piastrelleDisponibili);
		}
		result = nuoveCoordinate(p_util.tutteLePiastrelle());
		
		return result;
	}
	
	private Coordinate nuoveCoordinate(List<Envelope> piastrelleDisponibili)
	{
		int indice = rand.nextInt(piastrelleDisponibili.size());
		Envelope scelta = piastrelleDisponibili.get(indice);
		Coordinate result = scelta.centre();
		return result;
	}
	
	private List<Envelope> piastrelleLibere(Individual individuo)
	{
		List<Envelope> piastrelleOccupate = new ArrayList<Envelope>();
		List<Envelope> piastrelleDisponibili = p_util.tutteLePiastrelle();
		
		for(SecurityCamera telecamera:individuo.getCameras())
		{
			Coordinate p = telecamera.getRegistrationPoint();
			List<Envelope> occupateDaQuesto = p_util.occupate(piastrelleDisponibili,p,telecamera.getRaggio());
			piastrelleOccupate.addAll(occupateDaQuesto);
		}
		
		piastrelleDisponibili.removeAll(piastrelleOccupate);
		return piastrelleDisponibili;
	}
	
	private SecurityCamera genera(SecurityCamera telecamera,Individual individuo)
	{
		SecurityCamera result = null;
		/**
		 * Mi assicuro che le coordinate di registrazione della telecamera siano all'interno delle aree monitorate e che siano su piastrella libera
		 */
		Coordinate point = nuoveCoordinate(individuo);
		result = new SecurityCamera(telecamera.getName()+rand.nextInt(10),point,telecamera.getRaggio(),telecamera.getDirezioneInGradi(),telecamera.getAmpiezzaAngolare());
		
		return result;
	}
	
	
	private SecurityCamera sposta(SecurityCamera telecamera,Individual individuo)
	{
		SecurityCamera result = null;
		int spostamentoPossibile=(int)Math.round(telecamera.getRaggio()*diQuantoLoSpostamentoInPercentoDelRaggio/100);
		/**
		 * Mi assicuro che le coordinate di registrazione della telecamera siano all'interno delle aree monitorate, ci provo per dieci volte poi ne creo una nuova e basta...
		 */
		Coordinate point = nuoveCoordinate(telecamera,individuo,spostamentoPossibile);
		
		result = new SecurityCamera(telecamera.getName(),point,telecamera.getRaggio(),telecamera.getDirezioneInGradi(),telecamera.getAmpiezzaAngolare());
		
		return result;
	}
	
	private boolean avviene(int soglia)
	{
		boolean whoKnows = rand.nextInt(1, 101) <= soglia;
		return whoKnows;
		
	}
	
	private void azzeraContatori()
	{
		contaMutazioni = 0;
		contaRotazioni = 0;
		contaTraslazioni = 0;
		contaAggiunte = 0;
		contaRimozioni = 0;
		totaleTelecamere = 0;
		contaMutazioniTelecamere = 0;
	}
	
	public String stringaContatori()
	{
		StringWriter sw = new StringWriter();
		PrintWriter printWriter = new PrintWriter(sw);
		printWriter.printf("Ci sono state %d mutazioni su %d telecamere, di cui %d aggiunte, %d rimozioni, %d traslazioni e %d rotazioni \n",contaMutazioniTelecamere,totaleTelecamere, contaAggiunte,contaRimozioni,contaTraslazioni,contaRotazioni);
		printWriter.flush();
		String result = sw.toString();
		return result;
	}	
	
}
