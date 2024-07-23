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
import it.tomazzoli.ai.bim.utils.PositioningUtil;

public class TestImportVentotene 
{
	private static final String _IN_filename = "/Volumes/Data/DropBox/UniRoma1/2023/CameraPositioning/portodiventotene/PVT_JSON.txt";
	private static final String _OUT_filename = "/Volumes/Data/DropBox/UniRoma1/2023/CameraPositioning/portodiventotene/ventotene.txt";
	public TestImportVentotene() 
	{
		// TODO Auto-generated constructor stub
	}

	public static void main(String[] args) 
	{
		TestImportVentotene myself = new TestImportVentotene();
		List<RiskArea> complesso= new ArrayList<RiskArea>();
        BuiltEnvironment ambiente=new BuiltEnvironment(complesso);
        try 
        {
        	JSONUtil jutil = new JSONUtil();
            File fIN= new File(_IN_filename);
            
            ambiente=jutil.leggiAmbiente(fIN);
            System.out.println("Letto file");
            
            File fOUT= new File(_OUT_filename);
            jutil.scriviAmbiente(ambiente, fOUT);
            System.out.println("Scritto file");
            
            ambiente = jutil.leggiAmbiente(fOUT);
            System.out.println("Letto file");
		} 
        catch (JSONException e) 
        {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
        catch (IOException e) 
        {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        catch (Exception e) 
        {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        System.out.println(ambiente.toJSonObject().toString());
        SecurityCamera c2=new SecurityCamera("B",new Coordinate(50, 50), 250, 90, 120);
        
        PositioningUtil util = new PositioningUtil(ambiente);
        BuiltEnvironment ambienteNormalizzato = util.normalizza(ambiente,3);
        
        ProblemSolver solver=new ProblemSolver(ambienteNormalizzato);
        List<SecurityCamera> soluzione=solver.findSolution(c2,1500);
        //List<SecurityCamera> soluzione = new ArrayList<SecurityCamera>();
        //soluzione.add(c2);
        Renderer canvas2=new Renderer(ambienteNormalizzato,soluzione);
        canvas2.paint();
        
        //FitnessFunction eval=new FitnessFunction(ambienteNormalizzato);
        //Individual i=new Individual(soluzione);
        //eval.fitness(i);
        //System.out.println(i.toJSonObject().toString());
	}

}
