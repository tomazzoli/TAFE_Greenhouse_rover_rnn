package it.tomazzoli.ai.bim.geneticalgorithm;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Envelope;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;

import it.tomazzoli.ai.bim.beans.Element;
import it.tomazzoli.ai.bim.beans.Pole;
import it.tomazzoli.ai.bim.beans.RiskArea;
import it.tomazzoli.ai.bim.beans.ValuableArea;
import it.tomazzoli.ai.bim.beans.Wall;
import it.tomazzoli.ai.bim.utils.json.BuiltEnvironmentJSONFactory;

public class BuiltEnvironment 
{

	List<RiskArea> _aree;
	List<Wall> _ostacoli;
	List<Pole> _pali;
	List<ValuableArea> _elementiDiValore;
	
	public BuiltEnvironment(List<RiskArea> areeDiRischio) 
	{
		_aree=areeDiRischio;
		_ostacoli = new ArrayList<Wall>();
		_pali = new ArrayList<Pole>();
		_elementiDiValore = new ArrayList<ValuableArea>();
	}
	
	/***
	 * Verifica se un punto (espresso come coordinate x,y ,è all'interno di almeno una area di rischio di questo ambiente, o di un muro, o di un palo
	 * @param p un punto (x,y)
	 * @return true se le coordinate sono all'interno di almeno una area di rischio di questo ambiente, o di un muro, o di un palo
	 */
	public boolean insideEnvironmentElements(Coordinate p)
	{
		boolean result=false;
		Point punto = new GeometryFactory().createPoint(p);
		if(insideRiskdAreas(p))
		{
			return true;
		}
		for(Wall w:this.getObstacles())
		{
			if(w.getShape().covers(punto))
			{
				return true;
			}
		}
		for(Pole palo:this.getPoles())
		{
			if(palo.getShape().covers(punto))
			{
				return true;
			}
		}
		return result;
	}
	
	/***
	 * Verifica se un punto (espresso come coordinate x,y ,è all'interno di almeno una area di rischio di questo ambiente
	 * @param p un punto (x,y)
	 * @return true se le coordinate sono all'interno di almeno una area di rischio di questo ambiente
	 */
	public boolean insideRiskdAreas(Coordinate p)
	{
		boolean result=false;
		for(RiskArea r:this.getRiskAreas())
		{
			for(Envelope piastrella:r.getTiles())
			{
				if(piastrella.covers(p))
				{
					return true;
				}
			}
		}
		return result;
	}
	
	/***
	 * Ritorna l'unione di tutti i perimetri delle aree di rischio
	 * @return
	 */
	public Geometry getAllRiskAreaPerimeters()
	{
		List<RiskArea> aree=this.getRiskAreas();
		/**
		 *  devo generare tutti i poligoni e unirli, quandi trovare le coordinate di tutti i vertici.
		 *  a questo punto ho le coordinate che mi servono e mi creo il poligono complessivo
		 */
		Geometry complessivo=new GeometryFactory().createPolygon();
		for(RiskArea area:aree)
		{
			Geometry questo = area.getShape().copy();
			complessivo= complessivo.union(questo);
		}
		return complessivo;
	}
	
	/***
	 * Ritorna il bounding box complessivo di tutte le aree di rischio
	 * @return
	 */
	public Envelope getMaxBoundingBox()
	{
		List<Envelope> bbox = new ArrayList<Envelope>();
		for(RiskArea area:_aree)
		{
			Envelope e = area.getBoundingBox();
			bbox.add(e);
		}
		int minx =0;
		int maxx = 0;
		int miny = 0;
		int maxy = 0;
		for(Envelope boundingBox:bbox)
    	{
    		if(boundingBox.getMinX() < minx)
    		{
    			minx = (int)Math.round(boundingBox.getMinX());
    		}
    		if(boundingBox.getMinY() < miny)
    		{
    			miny = (int)Math.round(boundingBox.getMinY());
    		}
    		if(boundingBox.getMaxX() > maxx)
    		{
    			maxx = (int)Math.round(boundingBox.getMaxX());
    		}
    		if(boundingBox.getMaxY() > maxy)
    		{
    			maxy = (int)Math.round(boundingBox.getMaxY());
    		}
    	}
		Envelope result = new Envelope(minx,maxx,miny,maxy);
		return result;
	}
	
	public List<RiskArea> getRiskAreas()
	{
		return _aree;
	}
	
	public List<Wall> getObstacles()
	{
		return _ostacoli;
	}
	
	public List<Pole> getPoles()
	{
		return _pali;
	}
	
	public List<ValuableArea> getValuables()
	{
		return _elementiDiValore;
	}
	
	public boolean addPoles(List<Pole> pali)
	{
		return _pali.addAll(pali);
	}
	
	public boolean addPole(Pole palo)
	{
		return _pali.add(palo);
	}
	
	public boolean removePoles(List<Pole> pali)
	{
		return _pali.removeAll(pali);
	}
	
	public boolean removePole(Pole palo)
	{
		return _pali.remove(palo);
	}
	
	public boolean addValuables(List<ValuableArea> elementiDiValore)
	{
		return _elementiDiValore.addAll(elementiDiValore);
	}
	
	public boolean addValuable(ValuableArea elementoDiValore)
	{
		return _elementiDiValore.add(elementoDiValore);
	}
	
	public boolean removeValuables(List<ValuableArea> elementiDiValore)
	{
		return _elementiDiValore.removeAll(elementiDiValore);
	}
	
	public boolean removeValuable(ValuableArea elementoDiValore)
	{
		return _elementiDiValore.remove(elementoDiValore);
	}
	
	public boolean addObstacles(List<Wall> ostacoli)
	{
		return _ostacoli.addAll(ostacoli);
	}
	
	public boolean addObstacle(Wall ostacolo)
	{
		return _ostacoli.add(ostacolo);
	}
	
	public boolean removeObstacles(List<Wall> ostacoli)
	{
		return _ostacoli.removeAll(ostacoli);
	}
	
	public boolean removeObstacle(Wall ostacolo)
	{
		return _ostacoli.remove(ostacolo);
	}
	
	
	////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	
	public static BuiltEnvironment parseJSon(String jsonobj) throws JSONException
	{
			JSONObject input = new JSONObject(jsonobj);
			BuiltEnvironment obj=new BuiltEnvironmentJSONFactory().fromJSonObject(input);
			return obj;
	}

	public static BuiltEnvironment fromJSonObject(JSONObject input) throws JSONException
	{
		BuiltEnvironmentJSONFactory factory = new BuiltEnvironmentJSONFactory();
		BuiltEnvironment obj = factory.fromJSonObject(input);
		return obj;
	}

	public JSONObject toJSonObject()
	{
		JSONObject root = new BuiltEnvironmentJSONFactory().toJSonObject(this);
		return root;
	}
	
}
