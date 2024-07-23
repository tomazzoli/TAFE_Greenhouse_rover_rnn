package it.tomazzoli.ai.bim.test;

import java.util.ArrayList;
import java.util.List;
import java.util.SplittableRandom;

import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Polygon;

import it.tomazzoli.ai.bim.beans.RiskArea;
import it.tomazzoli.ai.bim.beans.SecurityCamera;
import it.tomazzoli.ai.bim.beans.Wall;
import it.tomazzoli.ai.bim.geneticalgorithm.BuiltEnvironment;
import it.tomazzoli.ai.bim.geneticalgorithm.Individual;
import it.tomazzoli.ai.bim.geneticalgorithm.process.fitness.BestAngularDirectionEvaluator;
import it.tomazzoli.ai.bim.graphics.Renderer;

public class TestValutazioneTelecamera 
{
	private static final String _filename="/Volumes/Data/temp/provaObjComplesso.txt";
	//private static final String _filename="/Volumes/Data/temp/provaObjConMuro.txt";
	public TestValutazioneTelecamera() 
	{
		// TODO Auto-generated constructor stub
	}

	public static void main(String[] args) 
	{
		TestValutazioneTelecamera myself = new TestValutazioneTelecamera();
		SplittableRandom rand = new SplittableRandom(System.currentTimeMillis());
		int [] x = {100, 400, 400, 100};
        int [] y = {100, 100, 300, 300};
        Polygon rettangolo = myself.generaRettangolo(x,y);
        
        int []x_m = {250, 260, 260, 250};
        int []y_m = {100, 100, 200, 200};
        Polygon muro = myself.generaRettangolo(x_m,y_m);
        Wall questoMuro = new Wall(muro);
        
        //Coordinate[] puntiTriangolo= {new Coordinate(50,150),new Coordinate(150,250),new Coordinate(50,300),new Coordinate(50,150)};
        //Polygon triangle = new GeometryFactory().createPolygon(puntiTriangolo);
        
        RiskArea r1=new RiskArea(rettangolo,0.5);
        r1.setName("R1");
        List<RiskArea> complesso= new ArrayList<RiskArea>();
        complesso.add(r1);
        
        BuiltEnvironment ambiente=new BuiltEnvironment(complesso);
        ambiente.addObstacle(questoMuro);
        
        SecurityCamera telecamera=new SecurityCamera("B",new Coordinate(100, 100), 250, 90, 120);
        List<SecurityCamera> soluzione = new ArrayList<SecurityCamera>();
        soluzione.add(telecamera);
        
        BestAngularDirectionEvaluator _valutatore = new BestAngularDirectionEvaluator(ambiente);
        Coordinate posizione= telecamera.getRegistrationPoint();
		List<Integer> angoliMigliori = _valutatore.direzioniMigliori(telecamera, posizione);
		
		int quale = rand.nextInt(angoliMigliori.size());
		
		int direzioneInGradi = angoliMigliori.get(quale);
		
		SecurityCamera result = new SecurityCamera(telecamera.getName()+"_rotata",telecamera.getRegistrationPoint(),telecamera.getRaggio(),direzioneInGradi,telecamera.getAmpiezzaAngolare());
		soluzione.add(result);
        
        Renderer canvas2=new Renderer(ambiente,soluzione);
        canvas2.paint();
        Individual i=new Individual(soluzione);
        System.out.println(i.toJSonObject().toString());
	}
	
	private Polygon generaRettangolo(int [] x,int [] y )
	{
		Coordinate[] punti=new Coordinate[x.length+1];
        for(int i=0;i<x.length;i++)
        {
        	punti[i]=new Coordinate(x[i],y[i]);
        }
        punti[x.length]=new Coordinate(x[0],y[0]); // la chisusura si fa con il primo punto messo in ultima posizione
        Polygon rettangolo = new GeometryFactory().createPolygon(punti);
        return rettangolo;
	}

}
