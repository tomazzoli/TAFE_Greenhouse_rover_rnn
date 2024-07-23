package it.tomazzoli.ai.bim.beans;

import org.json.JSONException;
import org.json.JSONObject;
import org.locationtech.jts.geom.Polygon;

import it.tomazzoli.ai.bim.utils.json.WallJSONFactory;

public class Wall extends Element 
{
	private Polygon _boundaries;
	
	public Wall(Polygon boundaries) 
	{
		_boundaries=boundaries;
	}
	
	@Override
	public Polygon getShape() 
	{
		return _boundaries;
	}

	public static Wall parseJSon(String jsonobj) throws JSONException
	{
		JSONObject input = new JSONObject(jsonobj);
		Wall obj=new WallJSONFactory().fromJSonObject(input);
		return obj;
	}
	
	public static Wall fromJSonObject(JSONObject input) throws JSONException
	{
		Wall obj=new WallJSONFactory().fromJSonObject(input);
		return obj;
	}
	
	public JSONObject toJSonObject()
	{
		JSONObject root = new WallJSONFactory().toJSonObject(this);
        return root;
	}
	
	
}
