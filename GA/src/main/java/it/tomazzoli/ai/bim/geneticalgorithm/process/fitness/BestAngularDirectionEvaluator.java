package it.tomazzoli.ai.bim.geneticalgorithm.process.fitness;

import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Hashtable;
import java.util.List;

import org.locationtech.jts.geom.Coordinate;

import it.tomazzoli.ai.bim.beans.SecurityCamera;
import it.tomazzoli.ai.bim.geneticalgorithm.BuiltEnvironment;
import it.tomazzoli.ai.bim.geometry.SmallComputationalGeometryCalculator;
import it.tomazzoli.ai.bim.utils.CameraPositioningParameters;

public class BestAngularDirectionEvaluator 
{
	private final SingleItemEvaluator _valutatore;
	private final int FULLCIRCLE = SmallComputationalGeometryCalculator.CERCHIO_COMPLETO;// il cerchio in gradi
	public final int QUANTIANGOLI = CameraPositioningParameters.getInt("CameraPositioning.quantiMiglioriDirezioni");// ne prendo  i migiori 12, così...
	
	public BestAngularDirectionEvaluator(BuiltEnvironment bimenv)  
	{
		_valutatore = new SingleItemEvaluator(bimenv);
	}
	
	public List<Integer> direzioniMigliori(SecurityCamera baseCamera,Coordinate posizione)
	{
		List<SimpleEntry<SecurityCamera,Double>> migliori = new ArrayList<SimpleEntry<SecurityCamera,Double>>();
		List<SecurityCamera> telecamere=new ArrayList<SecurityCamera>();
		int conta=0;
		int raggio=baseCamera.getRaggio();
		int apertura=Math.abs(baseCamera.getAmpiezzaAngolare());
		
		for(int direzioneInGradi=0; direzioneInGradi < FULLCIRCLE; direzioneInGradi = direzioneInGradi + 5)
		{
			SecurityCamera s=new SecurityCamera("Tentativo_"+conta++,posizione,raggio, direzioneInGradi, apertura);
			telecamere.add(s);
		}
		
		Hashtable<SecurityCamera,Double> valutate = _valutatore.singlePerformance(telecamere);
		
		conta = 0;
		for(SecurityCamera t:telecamere)
		{
			double valore = valutate.get(t);
			if(migliori.size() <= QUANTIANGOLI)
			{
				migliori.add(new SimpleEntry<SecurityCamera,Double>(t,valore));
			}
			else
			{
				Collections.sort(migliori, new ValueComparator());
				SimpleEntry<SecurityCamera,Double> peggiore = migliori.get(0);
				double minimo = peggiore.getValue();
				if(valore > minimo)
				{
					migliori.remove(0);
					migliori.add(new SimpleEntry<SecurityCamera,Double>(t,valore));
				}
			}
		}
		List<Integer> result = new ArrayList<Integer>();
		for(SimpleEntry<SecurityCamera,Double> item:migliori)
		{
			SecurityCamera t = item.getKey();
			int direzioneGradi = t.getDirezioneInGradi();
			result.add(direzioneGradi);
		}
		return result;
	}
	
	private class ValueComparator implements Comparator<SimpleEntry<SecurityCamera,Double>> {
        public int compare(SimpleEntry<SecurityCamera,Double> i1, SimpleEntry<SecurityCamera,Double> i2) {
            Double f1 = i1.getValue();
            Double f2 = i2.getValue();
            return f1.compareTo(f2);
        }
    }
}
