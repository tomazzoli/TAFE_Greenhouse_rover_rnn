package it.tomazzoli.ai.bim.utils.json;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.locationtech.jts.geom.Polygon;

import it.tomazzoli.ai.bim.beans.Element;
import it.tomazzoli.ai.bim.beans.ValuableArea;

public class ValuableAreaJSONFactory extends JSONFactory 
{
	protected final static String UNITVALUEFACTOR="VALOREUNITARIO";
	public ValuableAreaJSONFactory() 
	{
		super();
	}
	
	public ValuableArea fromJSonObject(JSONObject input) throws JSONException
	{
		if(input.has(JSONFactory.NAME))
		{
			String nome=input.getString(JSONFactory.NAME);
			if(input.has(UNITVALUEFACTOR))
			{
				double valoreUnitario=input.getDouble(UNITVALUEFACTOR);
				
				if(input.has(PERIMETRO))
				{
						JSONArray jbox=input.getJSONArray(PERIMETRO);
						Polygon perimetro=getPerimeterFronJSonObject(jbox);
						ValuableArea result=new ValuableArea(perimetro,valoreUnitario);
						result.setName(nome);
						return result;
				}
			}	
		}
		throw new JSONException(input.toString());
	}
	
	public ValuableArea parseJSon(String jsonobj) throws JSONException
	{
		JSONObject input = new JSONObject(jsonobj);
		ValuableArea obj=fromJSonObject(input);
		return obj;
	}
	
	public JSONObject toJSonObject(ValuableArea questo)
	{
		JSONObject root = new JSONObject();
        root.put(JSONFactory.NAME, questo.getName());
        root.put(UNITVALUEFACTOR, questo.getValueFactor());
        JSONArray jperimetro=getPolygonAsJsonObject(questo.getShape());
        root.put(PERIMETRO, jperimetro);
        return root;
	}	
	
}
