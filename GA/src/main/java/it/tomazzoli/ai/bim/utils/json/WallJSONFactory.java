package it.tomazzoli.ai.bim.utils.json;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.locationtech.jts.geom.Polygon;

import it.tomazzoli.ai.bim.beans.Wall;

public class WallJSONFactory extends JSONFactory 
{

	public WallJSONFactory() 
	{
		super();
	}
	
	public Wall fromJSonObject(JSONObject input) throws JSONException
	{
		if(input.has(JSONFactory.NAME))
		{
			String nome=input.getString(JSONFactory.NAME);
			if(input.has(PERIMETRO))
			{
					JSONArray jbox=input.getJSONArray(PERIMETRO);
					Polygon perimetro=getPerimeterFronJSonObject(jbox);
					Wall result=new Wall(perimetro);
					result.setName(nome);
					return result;
			}
		}
		throw new JSONException(input.toString());
	}
	
	public Wall parseJSon(String jsonobj) throws JSONException
	{
		JSONObject input = new JSONObject(jsonobj);
		Wall obj=fromJSonObject(input);
		return obj;
	}
	
	public JSONObject toJSonObject(Wall questo)
	{
		JSONObject root = new JSONObject();
        root.put(JSONFactory.NAME, questo.getName());
        JSONArray jperimetro=getPolygonAsJsonObject(questo.getShape());
        root.put(PERIMETRO, jperimetro);
        return root;
	}	
	
}
