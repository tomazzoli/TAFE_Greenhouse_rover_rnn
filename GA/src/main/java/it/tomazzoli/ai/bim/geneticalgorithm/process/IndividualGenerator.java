package it.tomazzoli.ai.bim.geneticalgorithm.process;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Envelope;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.Triangle;

import it.tomazzoli.ai.bim.beans.SecurityCamera;
import it.tomazzoli.ai.bim.geneticalgorithm.BuiltEnvironment;
import it.tomazzoli.ai.bim.geneticalgorithm.Individual;
import it.tomazzoli.ai.bim.geneticalgorithm.process.fitness.BestAngularDirectionEvaluator;
import it.tomazzoli.ai.bim.geneticalgorithm.process.fitness.PerformanceEvaluator;
import it.tomazzoli.ai.bim.geometry.SmallComputationalGeometryCalculator;
import it.tomazzoli.ai.bim.utils.PositioningUtil;

public class IndividualGenerator 
{
	private final BuiltEnvironment _bimenv;
	private Random random;
	private final SmallComputationalGeometryCalculator compGeometry;
	private final BestAngularDirectionEvaluator _valutatore;
	
	public IndividualGenerator(BuiltEnvironment bimenv) 
	{
		_bimenv=bimenv;
		random = new Random(System.currentTimeMillis());
		compGeometry= new SmallComputationalGeometryCalculator();
		_valutatore = new BestAngularDirectionEvaluator(bimenv);
	}
	
	/***
	 * Restituisce la prima generazione, realizzando una serie di posizionamenti casuali sulla base 
	 * di una triangolazione di delaunay delle aree di rischio; le telecamere appartengono tutte al modello base fornito
	 * @param baseCamera  una lista di @see it.tomazzoli.ai.bim.beans.SecurityCamera
	 * @param quantiIndividui  dimensione della popolazione iniziale
	 * @param budget  budget di spesa
	 * @return una lista di individui @link{it.tomazzoli.ai.bim.geneticalgorithm.Individual} 
	 */
	public List<Individual> initilaPolulation(SecurityCamera baseCamera,int quantiIndividui,int budget)
	{
		List<Individual> tentativeGeneration = new ArrayList<Individual>();
		
		Geometry complessivo = _bimenv.getAllRiskAreaPerimeters();

		double minTraingleArea = baseCamera.getArea()*0.75; // così...
		List<Triangle> triangoliAccettabili = compGeometry.trovaTriangoliAccettabili(_bimenv,minTraingleArea);
		
		double areaComplessiva=complessivo.getArea();
		int quanteNeVoglioAlMinimo =(int) Math.round((areaComplessiva/minTraingleArea)+2);
		int quanteNeVoglioAlMassimo = (int) Math.round((areaComplessiva/minTraingleArea)*2.5);
		int quanteNeVoglio=random.nextInt(quanteNeVoglioAlMassimo);
		if(quanteNeVoglio < quanteNeVoglioAlMinimo)
		{
			quanteNeVoglio = quanteNeVoglioAlMinimo;
		}
		
		for(int i=0;i<quantiIndividui;i++)
		{
			List<Triangle> _triangoli=compGeometry.randomTriangles(triangoliAccettabili, quanteNeVoglio);
			
			List<Coordinate> puntiDiApplicazione = trovaPuntiAccettabili(_triangoli,baseCamera);
			
			Individual ind=generaIndividuo(puntiDiApplicazione,baseCamera);
			tentativeGeneration.add(ind);
		}

		tentativeGeneration = valutaSingoliItem(tentativeGeneration);
		List<Individual> generation= adattaAlBudget(tentativeGeneration,budget);

		return generation;
	}

	private List<Individual> adattaAlBudget(List<Individual> tentativeGeneration,int budget)
	{
		List<Individual> result = new ArrayList<Individual>();

		for(Individual i:tentativeGeneration)
		{
			List<SecurityCamera> listaUnique = i.getBestItemsOnUniqueRiskTilesBudgetConstrained(budget);
			Individual iu = new Individual(listaUnique);
			List<SecurityCamera> listaRiskNotUnique = i.getBestItemsOnRiskTilesBudgetConstrained(budget);
			Individual inu = new Individual(listaRiskNotUnique);
			result.add(iu);
			result.add(inu);
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
		PerformanceEvaluator valutatore = new PerformanceEvaluator(_bimenv);
		List<Individual> result = valutatore.valutaSingoliItem(parents);
		return result;
		
	}
	
	/***
	 * Restituisce una nuova generazione, realizzando la riproduzione di ogni elemento di questa generazione:
	 * crea le coppie ( stando attento a fare in modo che ogni individuo si acooppi almeno una volta e che nessuno si accoppi troppe volte )
	 * e ne genera per ciascuna un certo numero di figli; ogni figlio ha caratteristiche diverse dall'altro.
	 * Le caratteristiche (ovvero le Telecamere @see SecurityCamera) che passano ai figli vengono scelte valutando le migliori telecamere di ciascun genitore
	 * Metodo delegato alla classe   @see it.tomazzoli.ai.bim.geneticalgorithm.process.Breeder
	 * oppure evitando sovrapposizioni tra telecamere del padre e della madre
	 * @param parents  una lista di @see it.tomazzoli.ai.bim.geneticalgorithm.Individual
	 * @return una lista di @link{it.tomazzoli.ai.bim.geneticalgorithm.Individual} individui generati a partire da una lista di @see it.tomazzoli.ai.bim.geneticalgorithm.Individual
	 */
	public List<Individual> crossover(List<Individual> parents)
	{
		Breeder riproduttore=new Breeder(_bimenv);

		List<Individual> generation = riproduttore.crossover(parents);
		
		return generation;
	}
	
	/***
	 * Restituisce i possibili punti di applicazione delle telecamere sulla base della divisione in triangoli delle aree di rischio
	 * Inserisce un punto solo se è in una piastrella non coperta dalle telecamere precedenti.
	 * @param triangoli la lista di triangoli abbastanza grandi in cui sono divisibili le aree di rischio
	 * @param baseCamera la telecamera stereotipo per la detrminazione del raggio di copertura
	 * @return Una lista di punti di installazione
	 */
	private List<Coordinate> trovaPuntiAccettabili(List<Triangle> triangoli,SecurityCamera baseCamera)
	{
		PositioningUtil p_util = new PositioningUtil(_bimenv);
		List<Coordinate> puntiDiApplicazione = new ArrayList<Coordinate>();
		List<Envelope> piastrelleOccupate = new ArrayList<Envelope>();
		List<Envelope> tutteLePiastrelle = p_util.tutteLePiastrelle();
		
		for(Triangle t:triangoli)
		{
			Coordinate p = t.inCentre();
			if(_bimenv.insideEnvironmentElements(p))
			{
				if(!p_util.alreadyPresent(p,piastrelleOccupate))
				{
					puntiDiApplicazione.add(p);
					List<Envelope> occupateDaQuesto = p_util.occupate(tutteLePiastrelle,p,baseCamera.getRaggio());
					piastrelleOccupate.addAll(occupateDaQuesto);
				}
			}
		}
		
		if(puntiDiApplicazione.size() < triangoli.size())
		{
			int quanti = triangoli.size() - puntiDiApplicazione.size();
			List<Coordinate> puntiUlteriori = p_util.trovaPuntiDaAggiungere(puntiDiApplicazione,baseCamera,quanti);
			puntiDiApplicazione.addAll(puntiUlteriori);
		}
		
		return puntiDiApplicazione;
	}
		
	
	/****
	 * Genera un individuo sulla base di superfici triangolari in ciascuna delle queli verrà posta una telecamera
	 * @param puntiDiApplicazione una lista di punti di applicazione in ciascuno dei quali andrà una telecamera il cui centro è l'incentro del triangolo mentre l'orientamento è casuale
	 * @param baseCamera lo stereotipo da cui partire
	 * @return un individuo, ovvero una lista di telecamere,
	 */
	private Individual generaIndividuo(List<Coordinate> puntiDiApplicazione,SecurityCamera baseCamera)
	{
		int raggio=baseCamera.getRaggio();
		int apertura=Math.abs(baseCamera.getAmpiezzaAngolare());
		
		int conta=0;
		List<SecurityCamera> telecamere=new ArrayList<SecurityCamera>();
		for(Coordinate p:puntiDiApplicazione)
		{
    		//double x=p.x-(raggio/2); // questo è il centro, quando creo la telecamra devo ricordarmi di togliere il raggio (arc2d vuole il minX del bounding box)
    		//double y=p.y-(raggio/2); // questo è il centro, quando creo la telecamra devo ricordarmi di togliere il raggio (arc2d vuole il minY del bounding box)
    		double x = p.x;
    		double y = p.y;
			if (x <0 ) {x = 0;}
    		if (y <0 ) {y = 0;}
    		Coordinate posizione= new Coordinate((int)x,(int)y);
    		List<Integer> angoliMigliori = _valutatore.direzioniMigliori(baseCamera, posizione);
    		
    		int quale = random.nextInt(angoliMigliori.size());
    		
    		int direzioneInGradi = angoliMigliori.get(quale);
    				
    		SecurityCamera s=new SecurityCamera("Sec_"+conta++,posizione, raggio, direzioneInGradi, apertura);
    		//System.out.printf("ho aggiunto una telecamera in (%.1f,%.1f) con copertura %.2f per il poligono %s \r\n",x,y,s.getArea(),t.toString());;
    		s.setCost(baseCamera.getCost());
    		telecamere.add(s);
		}
		Individual result=new Individual(telecamere);
		return result;
	}
	
}
