package it.tomazzoli.ai.bim.beans;

import org.json.JSONException;
import org.json.JSONObject;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Polygon;
import org.locationtech.jts.util.GeometricShapeFactory;

import it.tomazzoli.ai.bim.utils.json.PoleJSONFactory;

public class Pole extends Element 
{
	private Polygon _boundaries;
	private double size;
	private Coordinate _center;
	
	public Pole(Coordinate center,double radius) 
	{
		size=radius;
		_center=center;
		GeometricShapeFactory factory=new GeometricShapeFactory();
		factory.setCentre(center);
		factory.setSize(size);
		_boundaries=factory.createSquircle();
		
	}
	
	@Override
	public Polygon getShape() 
	{
		return _boundaries;
	}
	
	public Coordinate getCenter()
	{
		return _center;
	}
	
	public static Pole parseJSon(String jsonobj) throws JSONException
	{
		JSONObject input = new JSONObject(jsonobj);
		Pole obj=new PoleJSONFactory().fromJSonObject(input);
		return obj;
	}
	
	public static Pole fromJSonObject(JSONObject input) throws JSONException
	{
		Pole obj=new PoleJSONFactory().fromJSonObject(input);
		return obj;
	}
	
	public JSONObject toJSonObject()
	{
		JSONObject root = new PoleJSONFactory().toJSonObject(this);
        return root;
	}

}
