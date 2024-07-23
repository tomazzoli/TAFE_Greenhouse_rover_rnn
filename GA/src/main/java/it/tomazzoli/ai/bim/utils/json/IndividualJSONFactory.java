package it.tomazzoli.ai.bim.utils.json;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import it.tomazzoli.ai.bim.beans.SecurityCamera;
import it.tomazzoli.ai.bim.geneticalgorithm.Individual;

public class IndividualJSONFactory extends JSONFactory 
{

	public IndividualJSONFactory() 
	{
		super();
	}

	public Individual parseJSon(String jsonobj) throws Exception
	{
		JSONObject input = new JSONObject(jsonobj);
		Individual obj=fromJSonObject(input);
		return obj;
	}
	
	public JSONObject toJSonObject(Individual questo)
	{
		JSONObject root = new JSONObject();
		
		JSONArray jcamere= new JSONArray();
		for(SecurityCamera camera:questo.getCameras())
		{
			jcamere.put(camera.toJSonObject());
		}
		root.put(SecurityCamera.class.getSimpleName(), jcamere);
		
		return root;
	}	
	
	
	public Individual fromJSonObject(JSONObject input) throws Exception
	{
		
		if(input.has(SecurityCamera.class.getSimpleName()))
		{
			List<SecurityCamera> lista=new ArrayList<SecurityCamera>();
			JSONArray jcamere=input.getJSONArray(SecurityCamera.class.getSimpleName());
			
			for(int index=0;index <jcamere.length();index++)
			{
				JSONObject j=jcamere.getJSONObject(index);
				SecurityCamera area=SecurityCamera.fromJSonObject(j);
				lista.add(area);
			}
			Individual env = new Individual(lista);
			
			return env;
		}
		throw new JSONException(input.toString());
	}
}
