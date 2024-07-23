package it.tomazzoli.ai.bim.utils;

import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Envelope;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Polygon;

public class GeometryUtil 
{
	public static final int CERCHIO_COMPLETO=360;
	public static final double FATTORE_CONVERSIONE_RADIANTI=Math.PI/180;
	public GeometryUtil() 
	{
		// al momento non serve
	}
	
	public boolean intersects(Envelope env, Polygon poly)
	{
		Coordinate upperLeft=new Coordinate(env.getMinX(),env.getMinY());
		Coordinate upperRight=new Coordinate(env.getMaxX(),env.getMinY());
		Coordinate lowerLeft=new Coordinate(env.getMinX(),env.getMaxY());
		Coordinate lowerRight=new Coordinate(env.getMaxX(),env.getMaxY());
		Coordinate[] envhell= {upperLeft,upperRight,lowerRight,lowerLeft,upperLeft};
		Polygon envpoly=poly.getFactory().createPolygon(envhell);
		boolean result=poly.intersects(envpoly);
		return result;
	}
	
	public Polygon createRectangle(Coordinate point, double width, double height)
	{
		Coordinate upperLeft=new Coordinate(point.x,point.y);
		Coordinate upperRight=new Coordinate(point.x+width,point.y);
		Coordinate lowerLeft=new Coordinate(point.x,point.y+height);
		Coordinate lowerRight=new Coordinate(point.x+width,point.y+height);
		Coordinate[] shell= {upperLeft,upperRight,lowerRight,lowerLeft,upperLeft};
		Polygon poly=new GeometryFactory().createPolygon(shell);
		return poly;
	}
	
	private int getQuadrant(int angloloInGradi)
    {
		int angoloNormalizzato = normalizzato(angloloInGradi);	
		
		if (angoloNormalizzato >= 0 && angoloNormalizzato < 90)
            return 1;
        if (angoloNormalizzato >= 90 && angoloNormalizzato < 180)
            return 2;
        if (angoloNormalizzato >= 180 && angoloNormalizzato < 270)
            return 3;
        if (angoloNormalizzato >= 270 && angoloNormalizzato < 360)
            return 4;
        return 0;
    }
	
	public int normalizzato(int angloloInGradi)
	{
		int angoloSemplice = angloloInGradi % CERCHIO_COMPLETO;
		int angoloNormalizzato = (angoloSemplice >= 0) ? angoloSemplice : angoloSemplice + CERCHIO_COMPLETO;;	
		return angoloNormalizzato;
	}
	
	public Envelope getEnvelope(Coordinate center,int angleStartInGradi, int angleExtentInGradi, int raggio)
    {
		Envelope originBased = getEnvelope(angleStartInGradi,angleExtentInGradi,raggio);
		Coordinate upperLeft=new Coordinate(originBased.getMinX()+center.x,originBased.getMaxY()+center.y);
		Coordinate lowerRight=new Coordinate(originBased.getMaxX()+center.x,originBased.getMinY()+center.y);
		Envelope result = new Envelope(upperLeft,lowerRight);
        return result;
    }
	
    private Envelope getEnvelope(int angleStartInGradi, int angleExtentInGradi, int raggio)
    {
        int startQuad = getQuadrant(angleStartInGradi) - 1;// peerchè gli indici delle matrici iniziano da zero
        int angleEndInGradi = normalizzato(angleStartInGradi+angleExtentInGradi);
        int endQuad = getQuadrant(angleEndInGradi) - 1;// perchè gli indici delle matrici iniziano da zero

        // Convert to Cartesian coordinates.
        double startAngle = (double)angleStartInGradi * FATTORE_CONVERSIONE_RADIANTI;
        double endAngle = (double)angleEndInGradi * FATTORE_CONVERSIONE_RADIANTI;
        
        int startX = (int) Math.round(Math.cos(startAngle)*raggio);
        int startY = (int) Math.round(Math.sin(startAngle)*raggio);
        
        int endX = (int) Math.round(Math.cos(endAngle)*raggio);
        int endY = (int) Math.round(Math.sin(endAngle)*raggio);
       
        Coordinate stPt = new Coordinate(startX, startY);
        Coordinate enPt = new Coordinate(endX, endY);

        // Find bounding box excluding extremum.
        double minX = stPt.x;
        double minY = stPt.y;
        double maxX = stPt.x;
        double maxY = stPt.y;
        if (maxX < enPt.x) maxX = enPt.x;
        if (maxY < enPt.y) maxY = enPt.y;
        if (minX > enPt.x) minX = enPt.x;
        if (minY > enPt.y) minY = enPt.y;

        // Build extremum matrices.
        double[][] xMax = {{maxX, raggio, raggio, raggio}, {maxX, maxX, raggio, raggio}, {maxX, maxX, maxX, raggio}, {maxX, maxX, maxX, maxX}};
        double[][] yMax = {{maxY, maxY, maxY, maxY}, {raggio, maxY, raggio, raggio}, {raggio, maxY, maxY, raggio}, {raggio, maxY, maxY, maxY}};
        double[][] xMin = {{minX, -raggio, minX, minX}, {minX, minX, minX, minX}, {-raggio, -raggio, minX, -raggio}, {-raggio, -raggio, minX, minX}};
        double[][] yMin = {{minY, -raggio, -raggio, minY}, {minY, minY, -raggio, minY}, {minY, minY, minY, minY}, {-raggio, -raggio, -raggio, minY}};

        // Select desired values
        Coordinate startPt =new Coordinate(xMin[endQuad][startQuad], yMin[endQuad][startQuad]);
        Coordinate endPt=new Coordinate(xMax[endQuad][startQuad], yMax[endQuad][startQuad]);
        Envelope result = new Envelope(startPt,endPt);
        return result;
    }
    
   
    
}
