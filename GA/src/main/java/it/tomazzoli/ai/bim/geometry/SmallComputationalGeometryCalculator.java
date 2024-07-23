package it.tomazzoli.ai.bim.geometry;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Random;

import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Envelope;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Polygon;
import org.locationtech.jts.geom.Triangle;
import org.locationtech.jts.triangulate.DelaunayTriangulationBuilder;
import org.locationtech.jts.triangulate.VoronoiDiagramBuilder;
import org.locationtech.jts.util.GeometricShapeFactory;

import it.tomazzoli.ai.bim.geneticalgorithm.BuiltEnvironment;
import it.tomazzoli.ai.bim.utils.CameraPositioningParameters;
import it.tomazzoli.ai.bim.utils.GeometryUtil;

public class SmallComputationalGeometryCalculator 
{

	private int nRefinements;
	private double tolerance;
	private final String toleranceParam = "CameraPositioning.ComputationalGeometry.tolerance";
	private final String nRefinementsParam = "CameraPositioning.ComputationalGeometry.nRefinements";
	public static final double FATTORE_CONVERSIONE_RADIANTI=GeometryUtil.FATTORE_CONVERSIONE_RADIANTI;
	public static final int CERCHIO_COMPLETO=GeometryUtil.CERCHIO_COMPLETO;
	
	public SmallComputationalGeometryCalculator() 
	{
		nRefinements=20;
		tolerance=0.9;
		double leggo = CameraPositioningParameters.getDouble(toleranceParam);
		if(leggo>0)
		{
			tolerance=leggo;
		}
		
		int leggoInt = CameraPositioningParameters.getInt(nRefinementsParam);
		if(leggo>0)
		{
			nRefinements=leggoInt;
		}
	}
	
	/***
	 * Restituisce una lista di triangoli il cui incentro sia contenuto nelle aree di rischio dell'ambiente
	 * @param complessivo
	 * @param baseCamera
	 * @param minimalArea
	 * @return una lista di triangoli il cui incentro sia contenuto nelle aree di rischio 
	 */
	public List<Triangle> trovaTriangoliAccettabili(BuiltEnvironment bimenv,double minimalArea)
	{
		Geometry complessivo = bimenv.getAllRiskAreaPerimeters();
		Coordinate[] shell= complessivo.getCoordinates();
		Coordinate[] closedshell = new Coordinate[shell.length+1];
		for(int i=0;i<shell.length;i++)
		{
			closedshell[i]=shell[i];
		}
		closedshell[shell.length]=shell[0];
		
		List<Triangle> tuttitriangoli=delaunayTriangulation(closedshell, minimalArea);
		/**
		 * Verifico che l'incentro dei triangoli sia all'interno delle aree di rischio o su muri o pali, e scarto gli altri 
		 * in quanto le relative telecamere potrebbero eseere al di fuori della zona che devo monitorare
		 */
		List<Triangle> triangoliAccettabili = new ArrayList<Triangle>();
		for(Triangle t:tuttitriangoli)
		{
			Coordinate p = t.inCentre();
			if(bimenv.insideEnvironmentElements(p))
			{
				triangoliAccettabili.add(t);
			}
		}
		return triangoliAccettabili;
	}
	
	public List<Triangle> delaunayTriangulation(Coordinate[] shell,double minTriangleArea)
	{
		List<Triangle> result=new ArrayList<Triangle>();
		
		GeometryFactory geometryFactory=new GeometryFactory();
	    Geometry g = geometryFactory.createPolygon(shell);
	    
	    Geometry triangulation=refinedTriangulation(shell,minTriangleArea);
	    List<Polygon> working=getPolygons(triangulation);
	    
	    for(Polygon p:working)
		{	
			Triangle t=new Triangle(p.getCoordinates()[0],p.getCoordinates()[1],p.getCoordinates()[2]);
			result.add(t);
		}
	    
	    return result;
	}
	
	public List<Triangle> randomTriangles(List<Triangle> triangoli,int quante)
	{
		List<Triangle> working=biggestTriangles(triangoli,triangoli.size());
		List<Triangle> result=new ArrayList<Triangle>();
		Random rnd=new Random();
		while(result.size()<quante)
		{
			for(Triangle t:working)
			{
				boolean b=rnd.nextBoolean();
				if((b)&&(result.size()<quante))
				{
					result.add(t);
				}
			}
		}
		return result;
	}
	
	public Geometry getVoronoiDiagram(Coordinate[] shell,double tolerance) 
	{
		Polygon poli = createPolygon(shell);
		VoronoiDiagramBuilder builder = new VoronoiDiagramBuilder();
		GeometryFactory geometryFactory = poli.getFactory();
		Geometry result = builder.getDiagram(geometryFactory);
		return result;
	}
	
	public Geometry getVoronoiDiagram(Geometry shell,double tolerance) 
	{
		VoronoiDiagramBuilder builder = new VoronoiDiagramBuilder();
		List<Coordinate> sites=new ArrayList<Coordinate>();
		for(Coordinate c:shell.getCoordinates())
		{
			sites.add(c);
		}
		builder.setSites(sites);
		GeometryFactory geometryFactory = shell.getFactory();
		Geometry result = builder.getDiagram(geometryFactory);
		return result;
	}
	
	
	public List<Triangle> biggestTriangles(List<Triangle> triangoli,int quante)
	{
		List<Triangle> working=new ArrayList<Triangle>();
		working.addAll(triangoli);
		Collections.sort(working,new TriangleComparator().reversed());
		
		List<Triangle> result=working.subList(0, quante);
		
		return result;
	}
	
	public List<Polygon> getPolygons(Geometry g)
	{
		List<Polygon> poligoniVeri = new ArrayList<Polygon>();
		for (int i = 0; i < g.getNumGeometries(); i++) 
        {
        	Geometry triangle = (Geometry) g.getGeometryN(i);
        	if(triangle instanceof Polygon)
        	{
        		poligoniVeri.add((Polygon)triangle);
        	}
        }
		return poligoniVeri;
	}
	
	public Polygon createPolygon(Coordinate[] shell)
	{
		GeometryFactory geometryFactory=new GeometryFactory();
		Polygon p = geometryFactory.createPolygon(shell);
	    return p;
	}
	
	public Polygon createCoverage(Coordinate point, int raggio, int direzioneInGradi, int ampiezzaInGradi)
	{
		GeometricShapeFactory factory = new GeometricShapeFactory();
		GeometryUtil util = new GeometryUtil();
		
		int _direzioneInGradi =  util.normalizzato(direzioneInGradi);
		
		int _ampiezzaInGradi = util.normalizzato(ampiezzaInGradi);	
		
		double semiampiezza = _ampiezzaInGradi /2;
		
		double angleStartInGradi = (_direzioneInGradi - semiampiezza);
		
		
		double angleStart = angleStartInGradi * FATTORE_CONVERSIONE_RADIANTI;
		
		double angleExtent = _ampiezzaInGradi * FATTORE_CONVERSIONE_RADIANTI;
		
		Envelope env = getEnvelopeForArc(point,(int) (angleStartInGradi),_ampiezzaInGradi,raggio);
		//factory.setEnvelope(env);
		Coordinate base = new Coordinate(point.x-(raggio/2),point.y-(raggio/2));
		factory.setBase(base);
		factory.setSize(raggio);
		Polygon result=factory.createArcPolygon(angleStart, angleExtent);
		
		
		return result;
	}
	
	
	
	
	
	
	
	private Envelope getEnvelopeForArc(Coordinate point,int angleStart,int angleExtent, int raggio)
	{
		GeometryUtil util = new GeometryUtil();
		
		Envelope env=util.getEnvelope(point, angleStart, angleExtent, raggio);
		
		util = null;
		
		return env;
	}
	
	private Geometry refinedTriangulation(Coordinate[] shell,double minTriangleArea) 
	{
		
	    DelaunayTriangulationBuilder builder = new DelaunayTriangulationBuilder();
	    GeometryFactory geometryFactory=new GeometryFactory();
	    Geometry g = geometryFactory.createPolygon(shell);
	   
	    builder.setSites(g); // set vertex sites
	    builder.setTolerance(tolerance); // set tolerance for initial triangulation only
	    
	    Geometry triangulation = builder.getTriangles(geometryFactory); // initial triangulation
	    
	    HashSet<Coordinate> sites = new HashSet<Coordinate>();
	    for (int i = 0; i < triangulation.getCoordinates().length; i++) 
	    {
	        sites.add(triangulation.getCoordinates()[i]);
	    }

	    for (int refinement = 0; refinement < nRefinements; refinement++) 
	    {
	        int quantiNeFaccio=triangulation.getNumGeometries();
	    	for (int i = 0; i < quantiNeFaccio; i++) 
	        {
	            Polygon triangle = (Polygon) triangulation.getGeometryN(i);
	            double questaArea=triangle.getArea();
	            
	            if (questaArea > minTriangleArea) 
	            { // skip small triangles
	                sites.add(new Coordinate(triangle.getCentroid().getX(), triangle.getCentroid().getY()));
	            }
	        }
	        builder = new DelaunayTriangulationBuilder();
	        builder.setSites(sites);
	        triangulation = builder.getTriangles(geometryFactory); // re-triangulate using new centroid sites
	    }

	    //triangulation = triangulation.intersection(g); // restore concave hull and any holes
	    return triangulation;
	}

	private class TriangleComparator implements Comparator<Triangle> {
        public int compare(Triangle i1, Triangle i2) {
            Double f1 = i1.area();
            Double f2 = i2.area();
            return f1.compareTo(f2);
        }
    }
	
	private class AreaComparator implements Comparator<Polygon> {
        public int compare(Polygon i1, Polygon i2) {
            Double f1 = i1.getArea();
            Double f2 = i2.getArea();
            return f1.compareTo(f2);
        }
    }
}
