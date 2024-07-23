package it.tomazzoli.ai.bim.utils.json;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Polygon;

import it.tomazzoli.ai.bim.beans.RiskArea;
import it.tomazzoli.ai.bim.beans.SecurityCamera;

public class SecurityCameraJSONFactory extends JSONFactory 
{
	protected final static String SECURITYCAMERANAME=JSONFactory.NAME;
	protected final static String ANGOLO="ANGOLO";
	protected final static String COSTO="COSTO";
	protected final static String QUALITYFACTOR="QUALITYFACTOR";
	protected final static String NOTTURNA="NOTTURNA";
	
	public SecurityCameraJSONFactory() 
	{
		super();
	}
	
	public SecurityCamera fromJSonObject(JSONObject input) throws Exception
	{
		if(input.has(SECURITYCAMERANAME))
		{
			String nome=input.getString(SECURITYCAMERANAME);
			if(input.has(ANGOLO))
			{
					JSONArray jbox=input.getJSONArray(ANGOLO);
					int quante=jbox.length();
					if (quante<5)
					{
						throw new Exception();
					}
					double x=jbox.getDouble(0);
					double y=jbox.getDouble(1);
					int raggio=jbox.getInt(2);
					int angoloMin=jbox.getInt(3);
					int angoloMax=jbox.getInt(4);
					Coordinate point=new Coordinate(x,y);
					
					SecurityCamera result= new SecurityCamera(nome, point,raggio,angoloMin,angoloMax);
					if(input.has(QUALITYFACTOR))
					{
						double fattore=input.getDouble(QUALITYFACTOR);
						result.setQualityFactor(fattore);
						
					}
					if(input.has(COSTO))
					{
						double cost=input.getDouble(QUALITYFACTOR);
						result.setCost(cost);
						
					}
					if(input.has(NOTTURNA))
					{
						boolean isNocturnal=input.getBoolean(NOTTURNA);
						result.setNocturnal(isNocturnal);
						
					}
					return result;
			}
		}
		throw new JSONException(input.toString());
	}
	
	public SecurityCamera parseJSon(String jsonobj) throws Exception
	{
		JSONObject input = new JSONObject(jsonobj);
		SecurityCamera obj=fromJSonObject(input);
		return obj;
	}
	
	public JSONObject toJSonObject(SecurityCamera questo)
	{
		JSONObject root = new JSONObject();
        root.put(SECURITYCAMERANAME, questo.getName());
        
        JSONArray jangolo=new JSONArray();
        jangolo.put(Math.round(questo.getRegistrationPoint().x));
        jangolo.put(Math.round(questo.getRegistrationPoint().y));
        jangolo.put(questo.getRaggio());
        jangolo.put(questo.getDirezioneInGradi());
        jangolo.put(questo.getAmpiezzaAngolare());
        root.put(ANGOLO, jangolo);
        
        root.put(QUALITYFACTOR, questo.getQualityFactor());
        root.put(COSTO, questo.getCost());
        root.put(NOTTURNA, questo.isNocturnal());
        
        return root;
	}

}
