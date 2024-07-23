package it.tomazzoli.ai.bim.utils.json;

import java.util.AbstractMap;
import java.util.Scanner;
import java.util.StringTokenizer;

import org.json.JSONArray;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.geom.Polygon;

public abstract class JSONFactory 
{
	protected final static String PARAMETRI="Parametri";
	protected final static String NAME="NOME";
	protected final static String PERIMETRO="Coordinate";
	
	protected JSONFactory() 
	{
		
	}
	
	protected Polygon getPerimeterFronJSonObject(JSONArray input)
	{
		try
		{
			int quante=input.length();
			Coordinate[] shell=new Coordinate[quante/2];
			int j=0;
			for(int i=0;i<quante;i=i+2)
			{
				double x=input.getDouble(i);
				double y=input.getDouble(i+1);
				shell[j++]=new Coordinate(x,y);
			}
			Polygon result=new GeometryFactory().createPolygon(shell);
			return result;
		}
		catch(Throwable t)
		{
			Polygon result = getPerimeterFronPointJSonArray(input);
			return result;
		}
		
	}
	
	protected AbstractMap.SimpleImmutableEntry<Coordinate, Double> getCircleFronJSonArray(JSONArray input)
	{
		int quante=input.length();
		if (quante<3)
		{
			return null;
		}
		double x=input.getDouble(0);
		double y=input.getDouble(1);
		double raggio=input.getInt(2);
		
		AbstractMap.SimpleImmutableEntry<Coordinate, Double> result=new AbstractMap.SimpleImmutableEntry<Coordinate, Double>(new Coordinate(x,y), raggio);
		return result;
	}
	
	protected JSONArray getPolygonAsJsonObject(Polygon boundaries)
	{
		JSONArray jarr=new JSONArray();
		for(Coordinate c:boundaries.getCoordinates())
		{
			jarr.put(c.x);
			jarr.put(c.y);
		}
		return jarr;
	}
	
	protected Polygon getPerimeterFronPointJSonArray(JSONArray input)
	{
		//"Point(X = -396.508, Y = -58.295, Z = 0.000)"
		int quante=input.length();
		Coordinate[] shell=new Coordinate[quante+1];
		for(int i=0;i<quante;i++)
		{
			String punto = input.getString(i);
			shell[i] = fromString(punto);
		}
		shell[quante] = shell[0];
		Polygon result=new GeometryFactory().createPolygon(shell);
		return result;
	}
	
	private Coordinate fromString(String in)
	{
		StringTokenizer st = new StringTokenizer(in,",");
		
		String perX = st.nextToken();
		perX = perX.substring(perX.indexOf('=')+1);
		double x = Double.parseDouble(perX);
		String perY = st.nextToken();
		perY = perY.substring(perY.indexOf('=')+1);
		double y = Double.parseDouble(perY);
		
		
		Coordinate result = new Coordinate(x,y);
		
		return result;
	}
	
}
