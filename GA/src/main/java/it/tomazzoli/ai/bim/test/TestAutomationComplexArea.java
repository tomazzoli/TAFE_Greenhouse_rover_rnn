package it.tomazzoli.ai.bim.test;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONException;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Polygon;

import it.tomazzoli.ai.bim.beans.RiskArea;
import it.tomazzoli.ai.bim.beans.SecurityCamera;
import it.tomazzoli.ai.bim.beans.Wall;
import it.tomazzoli.ai.bim.geneticalgorithm.BuiltEnvironment;
import it.tomazzoli.ai.bim.geneticalgorithm.Individual;
import it.tomazzoli.ai.bim.geneticalgorithm.ProblemSolver;
import it.tomazzoli.ai.bim.geneticalgorithm.process.FitnessFunction;
import it.tomazzoli.ai.bim.graphics.Renderer;
import it.tomazzoli.ai.bim.utils.JSONUtil;

public class TestAutomationComplexArea 
{
	private static final String _filename="/Volumes/Data/temp/provaObjComplesso.txt";
	//private static final String _filename="/Volumes/Data/temp/provaObjConMuro.txt";
	public TestAutomationComplexArea() 
	{
		// TODO Auto-generated constructor stub
	}

	public static void main(String[] args) 
	{
		TestAutomationComplexArea myself = new TestAutomationComplexArea();
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
        //RiskArea r2=new RiskArea(triangle,"R2",0.2);
        List<RiskArea> complesso= new ArrayList<RiskArea>();
        complesso.add(r1);
        //complesso.add(r2);
        
        BuiltEnvironment ambiente=new BuiltEnvironment(complesso);
        ambiente.addObstacle(questoMuro);
        try 
        {
        	JSONUtil jutil = new JSONUtil();
            File f= new File(_filename);
            //jutil.scriviAmbiente(ambiente, f);
            
            ambiente=jutil.leggiAmbiente(f);
            System.out.println(ambiente.toJSonObject().toString());
          
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        
        
        //SecurityCamera c1=new SecurityCamera("A",new Coordinate(50, 150), 200, -45, 90);
        SecurityCamera c2=new SecurityCamera("B",new Coordinate(500, 550), 250, 90, 120);
        //SecurityCamera c3=new SecurityCamera("C",new Coordinate(300, 600), 200, 0, 270);
        /*
        List<SecurityCamera> telecamere=new ArrayList<SecurityCamera>();
        telecamere.add(c1);
        telecamere.add(c2);
        telecamere.add(c3);
        */
        ProblemSolver solver=new ProblemSolver(ambiente);
        List<SecurityCamera> soluzione=solver.findSolution(c2,1550);
        
        Renderer canvas2=new Renderer(ambiente,soluzione);
        canvas2.paint();
        
        FitnessFunction eval=new FitnessFunction(ambiente);
        Individual i=new Individual(soluzione);
        eval.fitness(i);
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
