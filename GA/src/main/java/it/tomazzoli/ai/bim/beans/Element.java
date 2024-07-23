package it.tomazzoli.ai.bim.beans;

import java.util.ArrayList;
import java.util.List;

import org.locationtech.jts.geom.Envelope;
import org.locationtech.jts.geom.Polygon;

import it.tomazzoli.ai.bim.utils.CameraPositioningParameters;
import it.tomazzoli.ai.bim.utils.GeometryUtil;

public abstract class Element 
{
	private String _name;
	private int _lato=10;
	private final String latoPiastrellaParam = "CameraPositioning.latoPiastrella";
	
	protected Element() 
	{
		int leggo = CameraPositioningParameters.getInt(latoPiastrellaParam);
		if(leggo>0)
		{
			_lato=leggo;
		}
	}
	
	public abstract Polygon getShape();
	
	public String getName()
	{
		if(_name != null)
		{
			return _name;
		}
		else
		{
			_name= this.getClass().getSimpleName()+System.currentTimeMillis();
			return _name;
		}
	}
	
	public void setName(String name)
	{
		_name=name;
	}
	
	protected List<Envelope> defineTiles(Polygon poly)
	{
		List<Envelope> result=new ArrayList<Envelope>();
		Envelope boundingBox=poly.getEnvelope().getEnvelopeInternal(); //prima il bounding box, poi il quadrato massimo contenuto in esso
		GeometryUtil util=new GeometryUtil();
		int upperX=(int)boundingBox.getMinX();
		int upperY=(int)boundingBox.getMinY();
		int quantiSuX=(int) (boundingBox.getWidth()/_lato);
		if((int)boundingBox.getWidth()%_lato!=0)
		{
			quantiSuX++;
		}
		int quantiSuY=(int) (boundingBox.getHeight()/_lato);
		if((int)boundingBox.getHeight()%_lato!=0)
		{
			quantiSuY++;
		}
		for(int i=0;i<quantiSuY;i++)
		{
			for(int j=0;j<quantiSuX;j++)
			{
				//Rectangle r=new Rectangle(upperX+j*_lato,upperY+i*_lato,_lato,_lato);
				double x1 = upperX + j*_lato;
				double y1 = upperY+ i *_lato;
				double x2 = x1 + _lato;
				double y2 = y1 + _lato;
				
				Envelope r=new Envelope(x1,x2,y1,y2);
				if(util.intersects(r,poly))
	        	{
					result.add(r);
	        	}
			}
		}
		
		return result;
	}
}
