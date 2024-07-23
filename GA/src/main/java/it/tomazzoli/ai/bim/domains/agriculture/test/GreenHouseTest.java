package it.tomazzoli.ai.bim.domains.agriculture.test;

import it.tomazzoli.ai.bim.beans.RiskArea;
import it.tomazzoli.ai.bim.beans.SecurityCamera;
import it.tomazzoli.ai.bim.beans.Wall;
import it.tomazzoli.ai.bim.domains.agriculture.GreenHouse;
import it.tomazzoli.ai.bim.geneticalgorithm.BuiltEnvironment;
import it.tomazzoli.ai.bim.geneticalgorithm.Individual;
import it.tomazzoli.ai.bim.geneticalgorithm.ProblemSolver;
import it.tomazzoli.ai.bim.geneticalgorithm.process.FitnessFunction;
import it.tomazzoli.ai.bim.graphics.Renderer;
import it.tomazzoli.ai.bim.utils.JSONUtil;
import org.json.JSONException;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Polygon;

import java.io.File;
import java.io.IOException;
import java.util.AbstractList;
import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.List;

public class GreenHouseTest
{
    public static void main(String[] args)
    {
        GreenHouse myself = new GreenHouse("/Volumes/Data/temp/provaSerraSemplice.txt");

        BuiltEnvironment ambiente=myself.getAmbiente();
        myself.save();

        SecurityCamera c1=new SecurityCamera("B",new Coordinate(200, 200), 250, 90, 360);
        SecurityCamera c2=new SecurityCamera("B",new Coordinate(600, 200), 250, 90, 360);
        SecurityCamera c3=new SecurityCamera("B",new Coordinate(1000, 200), 250, 90, 360);
        SecurityCamera c4=new SecurityCamera("B",new Coordinate(1400, 200), 250, 90, 360);
        SecurityCamera c5=new SecurityCamera("B",new Coordinate(200, 400), 250, 90, 360);
        SecurityCamera c6=new SecurityCamera("B",new Coordinate(600, 400), 250, 90, 360);
        SecurityCamera c7=new SecurityCamera("B",new Coordinate(1000, 400), 250, 90, 360);

        c1.setCost(100);c2.setCost(100);c3.setCost(100);c4.setCost(100);c5.setCost(100);c6.setCost(100);c7.setCost(100);
        GregorianCalendar iniziato = new GregorianCalendar();

        System.out.println(iniziato.getTime());
        System.out.println("iniziato");

        List<SecurityCamera> soluzioneVR = new ArrayList<SecurityCamera>();
        soluzioneVR.add(c1);
        soluzioneVR.add(c2);
        soluzioneVR.add(c3);
        soluzioneVR.add(c4);
        soluzioneVR.add(c5);
        soluzioneVR.add(c6);
        soluzioneVR.add(c7);
        /*
        Renderer canvas2=new Renderer(ambiente,soluzioneVR);
        canvas2.paint();
        canvas2.save("/Volumes/Data/temp/provaSerraSemplice.png");
        */
        ProblemSolver solver=new ProblemSolver(ambiente);
        List<SecurityCamera> soluzione=solver.findSolution(c2,1500);
        Renderer canvas=new Renderer(ambiente,soluzione);
        canvas.paint();
        canvas.save("/Volumes/Data/temp/Serra_"+iniziato.getTime().toString()+".png");

        FitnessFunction eval=new FitnessFunction(ambiente);
        Individual i=new Individual(soluzioneVR);
        eval.fitness(i);
        System.out.println(i.toJSonObject().toString());
        GregorianCalendar finito = new GregorianCalendar();
        System.out.println(finito.getTime());
        System.out.println("Finito dopo" + (finito.getTimeInMillis()- iniziato.getTimeInMillis())/1000 + "secondi");
    }

}
