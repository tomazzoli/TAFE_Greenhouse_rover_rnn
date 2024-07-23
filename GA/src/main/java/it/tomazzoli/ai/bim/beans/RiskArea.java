package it.tomazzoli.ai.bim.beans;

import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;
import org.locationtech.jts.geom.Envelope;
import org.locationtech.jts.geom.Polygon;

import it.tomazzoli.ai.bim.utils.json.RiskAreaJSONFactory;

public class RiskArea extends Element
{
	private Polygon _boundaries;
	private List<Envelope> _tiles;
	
	private double _riskfactor;
	private String _name;
	private String _bimID;
	
	public RiskArea(Polygon boundaries,double riskfactor) 
	{
		_riskfactor=riskfactor;
		_boundaries=boundaries;
		_tiles=defineTiles(_boundaries);
	}
	
	////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	
	public static RiskArea parseJSon(String jsonobj) throws JSONException
	{
		JSONObject input = new JSONObject(jsonobj);
		RiskArea obj=new RiskAreaJSONFactory().fromJSonObject(input);
		return obj;
	}
	
	public static RiskArea fromJSonObject(JSONObject input) throws JSONException
	{
		RiskArea obj=new RiskAreaJSONFactory().fromJSonObject(input);
		return obj;
	}
	
	public static RiskArea fromJSonObject(JSONObject input, String bimId) throws JSONException
	{
		RiskArea obj=new RiskAreaJSONFactory().fromJSonObject(input,bimId);
		return obj;
	}
	
	public JSONObject toJSonObject()
	{
		JSONObject root = new RiskAreaJSONFactory().toJSonObject(this);
        return root;
	}
	
	////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	
	public Envelope getBoundingBox()
	{
		Envelope boundingBox=_boundaries.getEnvelope().getEnvelopeInternal();
		return boundingBox;
	}
	
	public Polygon getShape()
	{
		return _boundaries;
	}
	
	public List<Envelope> getTiles()
	{
		return _tiles;
	}
	
	public double getRiskFactor()
	{
		return _riskfactor;
	}
	
	public double totalRisk()
	{
		double result=0;
		for(Envelope r:_tiles)
		{
			double singlerisk=r.getWidth()*r.getHeight()*_riskfactor;
			result+=singlerisk;
		}
		return result;
	}

	public String getName() {
		return _name;
	}

	public void setName(String name) {
		this._name = name;
	}
	
	public String getBimID() {
		return _bimID;
	}

	public void setBimID(String bimID) {
		this._bimID = bimID;
	}
		

}
