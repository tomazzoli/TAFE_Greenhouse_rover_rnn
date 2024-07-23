package it.tomazzoli.ai.bim.domains.agriculture;

import it.tomazzoli.ai.bim.beans.RiskArea;
import it.tomazzoli.ai.bim.beans.Wall;
import it.tomazzoli.ai.bim.geneticalgorithm.BuiltEnvironment;
import it.tomazzoli.ai.bim.utils.JSONUtil;
import org.json.JSONException;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Polygon;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class GreenHouse
{
	private final String _filename;
    private BuiltEnvironment _ambiente;

	public GreenHouse(String filename)
	{
        _filename = filename;
        _ambiente = this.init();
	}
	private Polygon generaRettangolo(int [] x,int [] y )
	{
		Coordinate[] punti=new Coordinate[x.length+1];
        for(int i=0;i<x.length;i++)
        {
        	punti[i]=new Coordinate(x[i],y[i]);
        }
        punti[x.length]=new Coordinate(x[0],y[0]); // la chisusura si fa con il primo punto messo in ultima posizione
        Polygon rettangolo = new GeometryFactory().createPolygon(punti);
        return rettangolo;
	}

    private BuiltEnvironment init()
    {
        int [] xbase = {100, 300, 300, 100};
        int [] y = {100, 100, 500, 500};
        int [] xav = {0,0,0,0};
        int [] xbv = {0,0,0,0};
        Polygon [] rettangoliAltoValore = new Polygon [4];
        Polygon [] rettangoliBassoValore = new Polygon [4];
        for(int j=0;j<4;j++)
        {
            for(int i =0;i<4;i++) {
                xav[i] = xbase[i] + 400*j;
                xbv[i] = xbase[i] + 200 + 400*j;
            }
            rettangoliAltoValore[j] = this.generaRettangolo(xav,y);
            System.out.println(rettangoliAltoValore[j].toString());
            rettangoliBassoValore[j] = this.generaRettangolo(xbv,y);
        }

        int []x_m = {100, 1400, 1400, 100};
        int []y_m = {280, 280, 260, 260};
        Polygon muro = this.generaRettangolo(x_m,y_m);
        Wall questoMuro = new Wall(muro);

        List<RiskArea> complesso= new ArrayList<RiskArea>();
        for(int j=0;j<4;j++)
        {
            RiskArea r1=new RiskArea(rettangoliAltoValore[j],5000+500*j);
            r1.setName("Ra"+j);
            complesso.add(r1);
            RiskArea r2=new RiskArea(rettangoliBassoValore[j],1000+400*j);
            r2.setName("Rb"+j);
            complesso.add(r2);
        }

        BuiltEnvironment ambiente=new BuiltEnvironment(complesso);
        ambiente.addObstacle(questoMuro);

        return ambiente;

    }

    public BuiltEnvironment getAmbiente()
    {
        return _ambiente;
    }

    public void save()
    {
        try
        {
            JSONUtil jutil = new JSONUtil();
            File f= new File(this._filename);
            jutil.scriviAmbiente(this._ambiente, f);
            System.out.println(this._ambiente.toJSonObject().toString());

        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

}
