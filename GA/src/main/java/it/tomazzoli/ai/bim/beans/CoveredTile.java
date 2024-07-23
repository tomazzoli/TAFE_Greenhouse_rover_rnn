package it.tomazzoli.ai.bim.beans;

import org.locationtech.jts.geom.Envelope;

/***
 * Classe di utilità come struttura dati per il calcolo
 */
public class CoveredTile
{
	protected Envelope _tile;
	protected double _unitfactor;
	protected double tileValue;
	
	public CoveredTile(Envelope tile,double unitfactor)
	{
		_tile=tile;
		_unitfactor=unitfactor;
		tileValue=_tile.getWidth()*_tile.getHeight()*_unitfactor;
	}
	
	public Envelope getTile()
	{
		return _tile;
	}
	
	public double getValue()
	{
		return tileValue;
	}
	
	public boolean equals(Object another)
	{
		if(another!=null)
		{
			if(another instanceof CoveredTile)
			{
				boolean result = (this.getTile().equals(((CoveredTile) another).getTile()));
				return result;
			}
		}
		return false;
	}
}