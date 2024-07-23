package it.tomazzoli.ai.bim.utils.json;

import java.util.AbstractMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Polygon;

import it.tomazzoli.ai.bim.beans.Pole;
import it.tomazzoli.ai.bim.beans.Wall;

public class PoleJSONFactory extends JSONFactory 
{

	public PoleJSONFactory() 
	{
		super();
	}
	
	public Pole fromJSonObject(JSONObject input) throws JSONException
	{
		if(input.has(JSONFactory.NAME))
		{
			String nome=input.getString(JSONFactory.NAME);
			if(input.has(PERIMETRO))
			{
					JSONArray jbox=input.getJSONArray(PERIMETRO);
					AbstractMap.SimpleImmutableEntry<Coordinate, Double> cerchio=getCircleFronJSonArray(jbox);
					Pole result=new Pole(cerchio.getKey(),cerchio.getValue());
					result.setName(nome);
					return result;
			}
		}
		throw new JSONException(input.toString());
	}
	
	public Pole parseJSon(String jsonobj) throws JSONException
	{
		JSONObject input = new JSONObject(jsonobj);
		Pole obj=fromJSonObject(input);
		return obj;
	}
	
	public JSONObject toJSonObject(Pole questo)
	{
		JSONObject root = new JSONObject();
        root.put(JSONFactory.NAME, questo.getName());
        JSONArray jperimetro=getPolygonAsJsonObject(questo.getShape());
        root.put(PERIMETRO, jperimetro);
        return root;
	}
	
	
}
