package it.tomazzoli.ai.bim.beans;

import org.json.JSONObject;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Envelope;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Polygon;
import org.locationtech.jts.util.GeometricShapeFactory;

import it.tomazzoli.ai.bim.geometry.SmallComputationalGeometryCalculator;
import it.tomazzoli.ai.bim.utils.CameraPositioningParameters;
import it.tomazzoli.ai.bim.utils.json.SecurityCameraJSONFactory;

public class SecurityCamera 
{
	private String _name;
	private double _qualityfactor;
	private double _cost;
	private Polygon _coverage;
	private boolean _notturna;
	private int _raggio;
	private int _ampiezzaInGradi;
	private int _bisettriceInGradi;
	private Coordinate _point;
	
	public SecurityCamera(String marcaModello, Coordinate point,int raggio, int direzioneInGradi, int ampiezzaInGradi)
	{
		_name=marcaModello;
		_cost=0;
		_qualityfactor=0.99;
		_notturna=false;
		_ampiezzaInGradi=ampiezzaInGradi;
		_bisettriceInGradi=direzioneInGradi;
		_raggio=raggio;
		_point=point;
		
		_coverage=createCoverage(point,raggio,direzioneInGradi,ampiezzaInGradi);
	}
	
	public SecurityCamera(SecurityCamera another)
	{
		_name=another.getName();
		_cost=another.getCost();
		_qualityfactor=another.getQualityFactor();
		_notturna=another.isNocturnal();
		_ampiezzaInGradi=another.getAmpiezzaAngolare();
		_bisettriceInGradi=another.getDirezioneInGradi();
		_raggio=another.getRaggio();
		_point=another.getRegistrationPoint();
		
		_coverage=createCoverage(another.getRegistrationPoint(),another.getRaggio(),another.getDirezioneInGradi(),another.getAmpiezzaAngolare());
	}
	
	public Polygon getCoverage()
	{
		return _coverage;
	}
	
	public double getArea()
	{
		/*double angoloInGradi=_coverage.getAngleExtent();
		if(angoloInGradi<0)
		{
			angoloInGradi=angoloInGradi+360;
		}
		double raggio=_coverage.getWidth()/2;
		double areaCoperta=Math.PI*raggio*raggio*(angoloInGradi/360);
		*/
		double areaCoperta=_coverage.getArea();
		return areaCoperta;
	}
	
	private Polygon createCoverage(Coordinate point, int raggio, int direzioneInGradi, int ampiezzaInGradi)
	{
		SmallComputationalGeometryCalculator factory=new SmallComputationalGeometryCalculator();
		
		
		Polygon result=factory.createCoverage(point, raggio, direzioneInGradi, ampiezzaInGradi);
		return result;
	}
	
	
	public boolean equals(Object another)
	{
		if(another!=null)
		{
			if(another instanceof SecurityCamera)
			{
				SecurityCamera other=(SecurityCamera) another;
				int direzioneInGradiThisOne =  (this.getDirezioneInGradi() > 0 ) ? this.getDirezioneInGradi()  : this.getDirezioneInGradi() + SmallComputationalGeometryCalculator.CERCHIO_COMPLETO;
				int direzioneInGradiOther   =  (other.getDirezioneInGradi() > 0) ? other.getDirezioneInGradi() : other.getDirezioneInGradi()+ SmallComputationalGeometryCalculator.CERCHIO_COMPLETO;
				int ampiezzaInGradiThisOne =  (this.getAmpiezzaAngolare() > 0 ) ? this.getAmpiezzaAngolare()  : this.getAmpiezzaAngolare() + SmallComputationalGeometryCalculator.CERCHIO_COMPLETO;
				int ampiezzaInGradiOther   =  (other.getAmpiezzaAngolare() > 0) ? other.getAmpiezzaAngolare() : other.getAmpiezzaAngolare()+ SmallComputationalGeometryCalculator.CERCHIO_COMPLETO;
				
				boolean uguale= ( (int)Math.round(this.getRegistrationPoint().x) == (int)Math.round(other.getRegistrationPoint().x) ) &&
								( (int)Math.round(this.getRegistrationPoint().y) == (int)Math.round(other.getRegistrationPoint().y) ) &&
								( this.getRaggio() == other.getRaggio() ) &&
								( this.getQualityFactor() == other.getQualityFactor() ) &&
								( Math.abs(direzioneInGradiThisOne - direzioneInGradiOther) < 30 ) &&
								( Math.abs(ampiezzaInGradiThisOne - ampiezzaInGradiOther) < 30 ) ;
				return uguale;
			}
		}
		return false;
	}
	
	public String positionString()
	{
		StringBuffer st = new StringBuffer();
		st.append(CameraPositioningParameters._openBracket);
		st.append(Math.round(this.getRegistrationPoint().x));
		st.append(CameraPositioningParameters.STR_COMMA);
		st.append(Math.round(this.getRegistrationPoint().y));
		st.append(CameraPositioningParameters.STR_COMMA);
		st.append(this.getDirezioneInGradi());
		st.append(CameraPositioningParameters._closeBracket);
		return st.toString();
	}
	
	////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	
	public static SecurityCamera parseJSon(String jsonobj) throws Exception
	{
		JSONObject input = new JSONObject(jsonobj);
		SecurityCamera obj=new SecurityCameraJSONFactory().fromJSonObject(input);
		return obj;
	}
	
	public static SecurityCamera fromJSonObject(JSONObject input) throws Exception
	{
		SecurityCamera obj=new SecurityCameraJSONFactory().fromJSonObject(input);
		return obj;
	}
	
	public JSONObject toJSonObject()
	{
		JSONObject root = new SecurityCameraJSONFactory().toJSonObject(this);
        return root;
	}
	////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	
	public String getName()
	{
		return _name;
	}
	
	public double getQualityFactor()
	{
		return _qualityfactor;
	}

	public void setQualityFactor(double qualityfactor)
	{
		_qualityfactor=qualityfactor;
	}
	
	public double getCost()
	{
		return _cost;
	}
	
	public void setCost(double cost)
	{
		_cost=cost;
	}
	
	public int getRaggio()
	{
		return _raggio;
	}
	
	public int getAmpiezzaAngolare()
	{
		return _ampiezzaInGradi;
	}
	
	public int getDirezioneInGradi()
	{
		return _bisettriceInGradi;
	}
	
	public boolean isNocturnal()
	{
		return _notturna;
	}
	
	public void setNocturnal(boolean isNocturnal)
	{
		_notturna=isNocturnal;
	}
	
	public Coordinate getRegistrationPoint()
	{
		return _point;
	}
}
