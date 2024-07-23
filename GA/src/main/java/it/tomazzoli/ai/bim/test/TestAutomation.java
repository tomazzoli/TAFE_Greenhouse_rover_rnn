package it.tomazzoli.ai.bim.test;

import java.util.ArrayList;
import java.util.List;

import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Polygon;

import it.tomazzoli.ai.bim.beans.RiskArea;
import it.tomazzoli.ai.bim.beans.SecurityCamera;
import it.tomazzoli.ai.bim.geneticalgorithm.BuiltEnvironment;
import it.tomazzoli.ai.bim.geneticalgorithm.Individual;
import it.tomazzoli.ai.bim.geneticalgorithm.ProblemSolver;
import it.tomazzoli.ai.bim.geneticalgorithm.process.FitnessFunction;
import it.tomazzoli.ai.bim.graphics.Renderer;

public class TestAutomation 
{

	public TestAutomation() 
	{
		// TODO Auto-generated constructor stub
	}

	public static void main(String[] args) 
	{
		int [] x = {450, 550, 750, 550, 630, 430, 170, 310, 120, 350, 450};
        int [] y = {210, 450, 510, 630, 880, 680, 850, 580, 410, 430,210};
        Coordinate[] punti=new Coordinate[x.length+1];
        for(int i=0;i<x.length;i++)
        {
        	punti[i]=new Coordinate(x[i],y[i]);
        }
        punti[x.length]=new Coordinate(x[0],y[0]); // la chisusura si fa con il primo punto messo in ultima posizione
        Polygon star = new GeometryFactory().createPolygon(punti);
        
        Coordinate[] puntiTriangolo= {new Coordinate(50,150),new Coordinate(150,250),new Coordinate(50,300),new Coordinate(50,150)};
        Polygon triangle = new GeometryFactory().createPolygon(puntiTriangolo);
        
        RiskArea r1=new RiskArea(star,0.5);
        r1.setName("R1");
        RiskArea r2=new RiskArea(triangle,0.2);
        r2.setName("R2");
        List<RiskArea> complesso= new ArrayList<RiskArea>();
        complesso.add(r1);
        //complesso.add(r2);
        BuiltEnvironment ambiente=new BuiltEnvironment(complesso);
        SecurityCamera c1=new SecurityCamera("A",new Coordinate(50, 150), 200, -45, 90);
        SecurityCamera c2=new SecurityCamera("B",new Coordinate(500, 550), 250, 90, 180);
        SecurityCamera c3=new SecurityCamera("C",new Coordinate(300, 600), 200, 0, 270);
        
        List<SecurityCamera> telecamere=new ArrayList<SecurityCamera>();
        telecamere.add(c1);
        telecamere.add(c2);
        telecamere.add(c3);
        
        ProblemSolver solver=new ProblemSolver(ambiente);
        List<SecurityCamera> soluzione=solver.findSolution(c2,1500);
        
        Renderer canvas=new Renderer(ambiente,telecamere);
        canvas.paint();
        
        Renderer canvas2=new Renderer(ambiente,soluzione);
        canvas2.paint();
        
        FitnessFunction eval=new FitnessFunction(ambiente);
        Individual i=new Individual(soluzione);
        eval.fitness(i);
        System.out.println(i.toJSonObject().toString());
	}

}
