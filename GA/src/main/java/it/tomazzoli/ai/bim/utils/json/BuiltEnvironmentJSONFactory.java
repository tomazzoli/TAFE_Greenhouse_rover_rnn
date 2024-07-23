package it.tomazzoli.ai.bim.utils.json;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import it.tomazzoli.ai.bim.beans.Pole;
import it.tomazzoli.ai.bim.beans.RiskArea;
import it.tomazzoli.ai.bim.beans.ValuableArea;
import it.tomazzoli.ai.bim.beans.Wall;
import it.tomazzoli.ai.bim.geneticalgorithm.BuiltEnvironment;
import it.tomazzoli.ai.bim.utils.CameraPositioningParameters;

public class BuiltEnvironmentJSONFactory extends JSONFactory 
{
	private String RiskAreaJsonName = CameraPositioningParameters.getString("JSON.RiskAreaName");
	private String WallJsonName = CameraPositioningParameters.getString("JSON.WallName");
	private String PoleJsonName = CameraPositioningParameters.getString("JSON.PoleName");
	private String ValuableAreaJsonName = CameraPositioningParameters.getString("JSON.ValuableAreaName");
	
	public BuiltEnvironmentJSONFactory() 
	{
		super();
		if(RiskAreaJsonName == null)
		{
			RiskAreaJsonName = RiskArea.class.getSimpleName();
		}
		if(WallJsonName == null)
		{
			WallJsonName = Wall.class.getSimpleName();
		}
		if(PoleJsonName == null)
		{
			PoleJsonName = Pole.class.getSimpleName();
		}
		if(ValuableAreaJsonName == null)
		{
			ValuableAreaJsonName = ValuableArea.class.getSimpleName();
		}
	}

	public BuiltEnvironment parseJSon(String jsonobj) throws Exception
	{
		JSONObject input = new JSONObject(jsonobj);
		BuiltEnvironment obj=fromJSonObject(input);
		return obj;
	}
	
	public JSONObject toJSonObject(BuiltEnvironment questo)
	{
		JSONObject root = new JSONObject();
		
		JSONArray jaree= new JSONArray();
		for(RiskArea area:questo.getRiskAreas())
		{
			jaree.put(area.toJSonObject());
		}
		root.put(RiskAreaJsonName, jaree);
		
		JSONArray jostacoli= new JSONArray();
		for(Wall obstacle:questo.getObstacles())
		{
			jostacoli.put((obstacle).toJSonObject());
		}
		root.put(Wall.class.getSimpleName(), jostacoli);
		
		JSONArray jpali = new JSONArray();
		for(Pole palo:questo.getPoles())
		{
			jpali.put(palo.toJSonObject());
		}
		root.put(PoleJsonName, jpali);
		
		JSONArray jvaluables = new JSONArray();
		for(ValuableArea valuable:questo.getValuables())
		{
			jvaluables.put(valuable.toJSonObject());
		}
		root.put(ValuableAreaJsonName, jvaluables);
		
		return root;
	}	
	
	
	public BuiltEnvironment fromJSonObject(JSONObject input) throws JSONException
	{
		
		if(input.has(RiskAreaJsonName))
		{
			List<RiskArea> lista=new ArrayList<RiskArea>();
			Object jaree = input.get(RiskAreaJsonName);
			if(jaree instanceof JSONArray)
			{
				lista = fromJsonArray((JSONArray)jaree);
			}
			else
			{
				lista = fromJsonObject((JSONObject)jaree);
			}
			
			BuiltEnvironment env = new BuiltEnvironment(lista);
			
			if(input.has(WallJsonName))
			{
				List<Wall> listaMuri=new ArrayList<Wall>();
				JSONArray jwall=input.getJSONArray(WallJsonName);
				
				for(int index=0;index <jwall.length();index++)
				{
					JSONObject j=jwall.getJSONObject(index);
					Wall muro=Wall.fromJSonObject(j);
					listaMuri.add(muro);
				}
				env.addObstacles(listaMuri);
			}
			
			if(input.has(PoleJsonName))
			{
				List<Pole> listaPali=new ArrayList<Pole>();
				JSONArray jpali=input.getJSONArray(PoleJsonName);
				
				for(int index=0;index <jpali.length();index++)
				{
					JSONObject j=jpali.getJSONObject(index);
					Pole palo=Pole.fromJSonObject(j);
					listaPali.add(palo);
				}
				env.addPoles(listaPali);
			}
			
			if(input.has(ValuableAreaJsonName))
			{
				List<ValuableArea> listaValuables=new ArrayList<ValuableArea>();
				JSONArray jvaluables=input.getJSONArray(ValuableAreaJsonName);
				
				for(int index=0;index <jvaluables.length();index++)
				{
					JSONObject j=jvaluables.getJSONObject(index);
					ValuableArea v=ValuableArea.fromJSonObject(j);
					listaValuables.add(v);
				}
				env.addValuables(listaValuables);
			}
			
			return env;
		}
		throw new JSONException(input.toString());
	}
	
	List<RiskArea> fromJsonObject(JSONObject container)
	{
		List<RiskArea> lista=new ArrayList<RiskArea>();
		Iterator<String> keys =container.keys();
		while(keys.hasNext())
		{
			String bimId = keys.next();
			JSONObject j=container.getJSONObject(bimId);
			RiskArea area=RiskArea.fromJSonObject(j,bimId);
			lista.add(area);
		}
		return lista;
	}
	
	List<RiskArea> fromJsonArray(JSONArray jaree)
	{
		List<RiskArea> lista=new ArrayList<RiskArea>();
		for(int index=0;index <jaree.length();index++)
		{
			JSONObject j=jaree.getJSONObject(index);
			RiskArea area=RiskArea.fromJSonObject(j);
			lista.add(area);
		}
		return lista;
	}
}
