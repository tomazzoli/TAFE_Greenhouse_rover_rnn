package it.tomazzoli.ai.bim.utils.json;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.locationtech.jts.geom.Polygon;

import it.tomazzoli.ai.bim.beans.RiskArea;
import it.tomazzoli.ai.bim.utils.CameraPositioningParameters;

public class RiskAreaJSONFactory extends JSONFactory 
{
	protected final static String RISKAREANAME="Room Name";//JSONFactory.NAME;
	protected final static String RISKFACTOR="Coefficiente di rischio";
	protected final static String RISKFACTORMULTIPLIERDESCR = CameraPositioningParameters.getString("CameraPositioning.moltiplicatoreFattoreRichio");
	protected int RISKFACTORMULTIPLIER = 100;
	
	public RiskAreaJSONFactory() 
	{
		super();
		try 
		{
			RISKFACTORMULTIPLIER = Integer.parseInt(RISKFACTORMULTIPLIERDESCR);
		}
		catch(NumberFormatException ne)
		{
			; //non faccio nulla, mi tengo il default
		}
	}

	public RiskArea fromJSonObject(JSONObject input, String bimID) throws JSONException
	{
		if(input.has(PARAMETRI))
		{
			JSONObject parametri = input.getJSONObject(PARAMETRI);
			if(parametri.has(RISKAREANAME))
			{
				String nome=parametri.getString(RISKAREANAME);
				if(parametri.has(RISKFACTOR))
				{
					double rischio=parametri.getDouble(RISKFACTOR);
					rischio = rischio * RISKFACTORMULTIPLIER;
					Polygon perimetro=getPerimeter(input);
					RiskArea result= new RiskArea(perimetro, rischio);
					result.setName(nome);
					result.setBimID(bimID);
					return result;
				}
			}
		}
		throw new JSONException(input.toString());
	}
	
	public RiskArea fromJSonObject(JSONObject input) throws JSONException
	{
		if(input.has(RISKAREANAME))
		{
			String nome=input.getString(RISKAREANAME);
			if(input.has(RISKFACTOR))
			{
				double rischio=input.getDouble(RISKFACTOR);
				rischio = rischio * RISKFACTORMULTIPLIER;
				Polygon perimetro=getPerimeter(input);
				RiskArea result= new RiskArea(perimetro, rischio);
				result.setName(nome);
				return result;
			}
		}
		throw new JSONException(input.toString());
	}
	
	public RiskArea parseJSon(String jsonobj) throws JSONException
	{
		JSONObject input = new JSONObject(jsonobj);
		RiskArea obj=fromJSonObject(input);
		return obj;
	}
	
	public JSONObject toJSonObject(RiskArea questo)
	{
		JSONObject root = new JSONObject();
        root.put(RISKAREANAME, questo.getName());
        root.put(RISKFACTOR, questo.getRiskFactor());
        JSONArray jperimetro=getPolygonAsJsonObject(questo.getShape());
        root.put(PERIMETRO, jperimetro);
        return root;
	}
	
	private Polygon getPerimeter(JSONObject input) throws JSONException
	{
		if(input.has(PERIMETRO))
		{
			JSONArray jbox=input.getJSONArray(PERIMETRO);
			Polygon perimetro=getPerimeterFronJSonObject(jbox);
			return perimetro;
		}
		throw new JSONException(input.toString());	
	}
}
