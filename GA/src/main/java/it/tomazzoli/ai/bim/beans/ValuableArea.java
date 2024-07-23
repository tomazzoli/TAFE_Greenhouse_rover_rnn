package it.tomazzoli.ai.bim.beans;

import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;
import org.locationtech.jts.geom.Envelope;
import org.locationtech.jts.geom.Polygon;

import it.tomazzoli.ai.bim.utils.json.ValuableAreaJSONFactory;

public class ValuableArea extends Element 
{
	private Polygon _boundaries;
	private List<Envelope> _tiles;
	private double _valuefactor;
	
	public ValuableArea(Polygon boundaries,double valuefactor) 
	{
		super();
		_boundaries = boundaries;
		_valuefactor = valuefactor;
		_tiles = defineTiles(_boundaries);
	}
	
	@Override
	public Polygon getShape() 
	{
		return _boundaries;
	}

	public List<Envelope> getTiles()
	{
		return _tiles;
	}
	
	public double getValueFactor()
	{
		return _valuefactor;
	}
	
	////////////////////////////////////////////////////////////////////////////////
	
	public static ValuableArea parseJSon(String jsonobj) throws JSONException
	{
		JSONObject input = new JSONObject(jsonobj);
		ValuableArea obj=new ValuableAreaJSONFactory().fromJSonObject(input);
		return obj;
	}
	
	public static ValuableArea fromJSonObject(JSONObject input) throws JSONException
	{
		ValuableArea obj=new ValuableAreaJSONFactory().fromJSonObject(input);
		return obj;
	}
	
	public JSONObject toJSonObject()
	{
		JSONObject root = new ValuableAreaJSONFactory().toJSonObject(this);
        return root;
	}
	
	
}
