package it.tomazzoli.ai.bim.utils;

import java.awt.Color;
import java.awt.Rectangle;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Envelope;
import org.locationtech.jts.geom.Polygon;

import it.tomazzoli.ai.bim.beans.SecurityCamera;

public class RenderingUtil
{
	private Envelope _boundingBox;
	private final int _bordoBianco = 100;
	
	public RenderingUtil(Envelope bbox) 
	{
		_boundingBox = bbox;
	}

	
	public List<Rectangle2D> getRenderingTiles(List<Envelope> tiles)
	{
		List<Rectangle2D> result=new ArrayList<Rectangle2D>();
		double deltaY = _boundingBox.getHeight()+_bordoBianco/2;
		for(Envelope env:tiles)
		{
			Rectangle2D boundingBox=new Rectangle2D.Double((env.getMinX()), (-env.getMinY()+deltaY), env.getWidth(), env.getHeight());
			result.add(boundingBox);
		}
		return result;
	}
	
	public Rectangle2D getBoundingBox(Polygon _boundaries)
	{
		Envelope env=_boundaries.getEnvelope().getEnvelopeInternal();
		Rectangle2D boundingBox=new Rectangle2D.Double((env.getMinX()), env.getMinY(), env.getWidth(), env.getHeight());
		return boundingBox;
	}
	
	public java.awt.Polygon getRenderingBoundaries(Polygon _boundaries)
	{
		java.awt.Polygon result=new java.awt.Polygon();
		double deltaY = _boundingBox.getHeight()+_bordoBianco/2;
		for(Coordinate c:_boundaries.getCoordinates())
		{
			int x = (int)c.x;
			int y = (int) ((-c.y) +deltaY);// in computer graphics la y va verso il basso
			result.addPoint(x, y);
		}
		return result;
	}
	/**
	public Arc2D getArc2D(SecurityCamera cam)
	{
		
		int raggio = cam.getRaggio();
		GeometryUtil util = new GeometryUtil();
		
		double semiampiezza = cam.getAmpiezzaAngolare() /2;
		
		double angleStart = (cam.getDirezioneInGradi() - semiampiezza);
		
		double angleExtent =  cam.getAmpiezzaAngolare();
		Envelope env=util.getEnvelope(cam.getRegistrationPoint(),(int) angleStart,(int) angleExtent, raggio);
		
		Arc2D.Double result = new Arc2D.Double(Arc2D.PIE);
		//result.setFrame(env.getMinX(), env.getMinY(), env.getWidth(), env.getHeight());
		result.setFrame(cam.getRegistrationPoint().x,cam.getRegistrationPoint().y,raggio,raggio);
		result.setAngleStart(angleStart);
		result.setAngleExtent(angleExtent);
		//Arc2D result=new Arc2D.Double(x, y,raggio,raggio, angleStart, angleExtent, Arc2D.PIE);
		
		//Arc2D result=new Arc2D.Double(x, y, arc.getEnvelopeInternal().getWidth(),  arc.getEnvelopeInternal().getHeight(), angleStart, angleExtent, Arc2D.PIE);
		return result;
	}
	**/
	public java.awt.Polygon getCoverageAsArc2D(SecurityCamera cam)
	{
		Polygon arc=cam.getCoverage();
		java.awt.Polygon result = getRenderingBoundaries(arc);
		return result;
	}
	
	
	public Color getColor(double riskValue)
	{
		String[] mColors = 
		{
				"#53bbb4", // aqua
	            "#51b46d", // green
				"#c25975", // mauve
	            "#39add1", // light blue
	            "#3079ab", // dark blue
	            "#f092b0", // pink
	            "#e0ab18", // mustard
	            "#838cc7", // lavender
	            "#7d669e", // purple
	            "#f9845b", // orange
	            "#e15258", // red
	            "#637a91", // dark gray
	            "#b7c0c7"  // light gray
	    };

		Hashtable<Integer,String> tabellacolori= new Hashtable<Integer,String>();
		for(int i=0;i<mColors.length;i++)
		{
			tabellacolori.put(i, mColors[i]);
		}
		int quale = (int) Math.round(Math.round(riskValue*1000)/100);
		if(quale > tabellacolori.size())
		{
			quale = quale % tabellacolori.size();
		}
		String esadecimale = tabellacolori.get(quale);
		
		Color colore = Color.decode(esadecimale);
		return colore;
	}
	
}
