package it.tomazzoli.ai.bim.test;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.geom.Arc2D;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JPanel;

import org.locationtech.jts.awt.ShapeWriter;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Envelope;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Polygon;
import org.locationtech.jts.geom.Triangle;

import it.tomazzoli.ai.bim.beans.RiskArea;
import it.tomazzoli.ai.bim.beans.SecurityCamera;
import it.tomazzoli.ai.bim.geneticalgorithm.BuiltEnvironment;
import it.tomazzoli.ai.bim.geometry.SmallComputationalGeometryCalculator;
import it.tomazzoli.ai.bim.utils.RenderingUtil;

public class TestGeometriaComputazionale extends JPanel 
{
	private List<Triangle> _triangoli;
	private List<SecurityCamera> risultati;
	private Polygon _star ;
	private SmallComputationalGeometryCalculator compGeometry;
	
	public TestGeometriaComputazionale() 
	{
		_triangoli = new ArrayList<Triangle>();
		risultati = new ArrayList<SecurityCamera>();
		compGeometry= new SmallComputationalGeometryCalculator();
	}
	
	public List<SecurityCamera> posiziona(Polygon shell,SecurityCamera baseCamera)
	{
		List<SecurityCamera> result=new ArrayList<SecurityCamera>();
		double minCameraArea=baseCamera.getArea();
		//Geometry triangoli=compGeometry.refinedTriangulation(shell, minCameraArea );
		//_triangoli=compGeometry.getPolygons(triangoli);
		List<Triangle> triangoli=compGeometry.delaunayTriangulation(shell.getCoordinates(), minCameraArea);
		
		int quanti=triangoli.size();
        
		//double raggio=baseCamera.getCoverage().getWidth()/2;
		int raggio=(int)baseCamera.getCoverage().getEnvelope().getEnvelopeInternal().getWidth();
		
		
		System.out.printf("Generati i triangoli, sono %d , ora trovo dei cerchi di raggio %d ovvero di area %.2f\r\n",quanti,raggio,Math.PI*raggio*raggio);
        
		int quanteNevVglio=(int) (Math.round(this._star.getArea()/baseCamera.getArea())+1);
		_triangoli=compGeometry.randomTriangles(triangoli, quanteNevVglio);
		
		int conta=0;
		for(Triangle t:_triangoli)
		{
			Coordinate p=t.inCentre();
    		double x=p.x; // questo è il centro, quando creo la telecamra devo ricordarmi di togliere il raggio (arc2d vuole il minX del bounding box)
    		double y=p.y; // questo è il centro, quando creo la telecamra devo ricordarmi di togliere il raggio (arc2d vuole il minY del bounding box)
    		Coordinate upperLeft= new Coordinate(x-baseCamera.getRaggio()/2, y-baseCamera.getRaggio()/2);
    		
    		//SecurityCamera s=new SecurityCamera("Sec_"+conta++,upperLeft, raggio*2,0, 360);
    		SecurityCamera s=new SecurityCamera("Sec_"+conta++,upperLeft, baseCamera.getRaggio(),baseCamera.getDirezioneInGradi(),baseCamera.getAmpiezzaAngolare());
    		
    		//SecurityCamera s=new SecurityCamera("Sec_"+conta++,new Arc2D.Double(x-raggio, y-raggio, raggio*2, raggio*2, 0, 360, Arc2D.PIE),1000,0.8,true);
    		//System.out.printf("ho aggiunto una telecamera in (%.1f,%.1f) con copertura %.2f per il poligono %s \r\n",x,y,s.getArea(),t.toString());;
    		result.add(s);
		}
		return result;
		
	}
	
	public static void main(String[] args) 
	{
		int [] x = {450, 550, 750, 550, 630, 430, 170, 310, 120, 350, 450};
        int [] y = {210, 450, 510, 630, 880, 680, 850, 580, 410, 430,210};
        Coordinate[] punti=new Coordinate[x.length+1];
        for(int i=0;i<x.length;i++)
        {
        	punti[i]=new Coordinate(x[i],y[i]);
        }
        punti[x.length]=new Coordinate(x[0],y[0]); // la chisusura si fa con il primo punto messo in ultima posizione
        Polygon star = new GeometryFactory().createPolygon(punti);
        
        Coordinate[] puntiTriangolo= {new Coordinate(50,150),new Coordinate(150,250),new Coordinate(50,300),new Coordinate(50,150)};
        Polygon triangle = new GeometryFactory().createPolygon(puntiTriangolo);
        
        RiskArea r1=new RiskArea(star,0.5);
        r1.setName("R1");
        RiskArea r2=new RiskArea(triangle,0.2);
        r2.setName("R2");
        List<RiskArea> complesso= new ArrayList<RiskArea>();
        complesso.add(r1);
        complesso.add(r2);
        BuiltEnvironment ambiente=new BuiltEnvironment(complesso);
        SecurityCamera c1=new SecurityCamera("A",new Coordinate(50, 150), 200, -45, 90);
        SecurityCamera c2=new SecurityCamera("B",new Coordinate(500, 550), 250, 90, 180);
        SecurityCamera c3=new SecurityCamera("C",new Coordinate(300, 600), 200, 0, 270);
        
        List<SecurityCamera> telecamere=new ArrayList<SecurityCamera>();
        telecamere.add(c1);
        telecamere.add(c2);
        telecamere.add(c3);
        
        SecurityCamera baseCamera=c1;
		
        //SecurityCamera baseCamera=new SecurityCamera("A",new Arc2D.Double(50, 150, 100, 100, 0, 360, Arc2D.PIE),1000,0.8,true);
		TestGeometriaComputazionale myself=new TestGeometriaComputazionale();
		myself._star=star;
		
        System.out.printf("Impostato il problema, devo coprire una superficie di %.2f con telecamere che coprono %.2f: dovrebbero servirmene %.2f \r\n",myself._star.getArea(),baseCamera.getArea(),myself._star.getArea()/baseCamera.getArea() );
        
        myself.risultati=myself.posiziona(star, baseCamera);
        
        
        myself.paint();
        
        System.out.printf("Ci sono %d triangoli in quanto l'area del poligono è %.2f ed ogni telecamea copre %.2f\r\n",myself._triangoli.size(),myself._star.getArea(),baseCamera.getArea());
        //System.out.println(i.toJSonObject().toString());
	}

	
	
	
	
	public void paintComponent(Graphics g)
	{    
		RenderingUtil util=new RenderingUtil(new Envelope(0,100,0,100));
		g.setColor(Color.lightGray);
    	ShapeWriter sw = new ShapeWriter();
    	Shape starShape = sw.toShape(_star);
    	((Graphics2D) g).draw(starShape);
    	g.setColor(Color.blue);
    	GeometryFactory geometryFactory=new GeometryFactory();
 	   
    	for(Triangle t:_triangoli)
		{
    		Coordinate[] shell={t.p0,t.p1,t.p2,t.p0};
    		Polygon p=geometryFactory.createPolygon(shell);
    		Shape polyShape = sw.toShape(p);
    		((Graphics2D) g).draw(polyShape);
		}
    	g.setColor(Color.green);
    	/*for(SecurityCamera s:risultati)
    	{
    		Arc2D a=util.getArc2D(s);
    		g.fillArc((int)a.getMinX(), (int)a.getMinY(),(int) a.getWidth(), (int)a.getHeight(), (int)a.getAngleStart(),(int) a.getAngleExtent());
    	}
    	*/
    	for(Triangle t:_triangoli)
		{
    		g.drawOval((int)t.inCentre().x,(int)t.inCentre().y,3,3);
		}
    	g.setColor(Color.orange);
    	for(SecurityCamera s:risultati)
    	{
    		//Arc2D a=util.getArc2D(s);
    		//g.fillArc((int)a.getMinX(), (int)a.getMinY(),(int) a.getWidth(), (int)a.getHeight(), (int)a.getAngleStart(),(int) a.getAngleExtent());
    		g.fillPolygon(util.getCoverageAsArc2D(s));
    		
    	}
	}
	
	 public void paint()
	 {
		 JFrame.setDefaultLookAndFeelDecorated(true);
	     JFrame frame = new JFrame("Visualizzazione Grafica");
	     frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	     frame.setBackground(Color.white);
	     frame.setSize(1200, 800);
	     
	     frame.add(this);
	    
	     frame.setVisible(true);

	}
}
