package it.tomazzoli.ai.bim.graphics;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.List;

import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JPanel;

import org.locationtech.jts.geom.Envelope;

import it.tomazzoli.ai.bim.beans.Pole;
import it.tomazzoli.ai.bim.beans.RiskArea;
import it.tomazzoli.ai.bim.beans.SecurityCamera;
import it.tomazzoli.ai.bim.beans.Wall;
import it.tomazzoli.ai.bim.geneticalgorithm.BuiltEnvironment;
import it.tomazzoli.ai.bim.utils.RenderingUtil;

public class Renderer extends JPanel
{
	private final List<RiskArea> _aree;
	private final List<Wall> _muri;
	private final List<Pole> _pali;
	private final List<SecurityCamera> _telecamere;
	private final int _bordoBianco = 100;
	private final Envelope bbox;
	
	public Renderer(BuiltEnvironment ambiente,List<SecurityCamera> telecamere) 
	{
		_aree = ambiente.getRiskAreas();
		_muri = ambiente.getObstacles();
		_pali = ambiente.getPoles();
		_telecamere = telecamere;
		bbox = ambiente.getMaxBoundingBox();
	}
	
	public void paint()
	{
		 JFrame.setDefaultLookAndFeelDecorated(true);
	     JFrame frame = new JFrame("Visualizzazione Grafica");
	     frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	     frame.setBackground(Color.white);
	     
	     //frame.setSize(1200, 800);
	     frame.setSize((int)bbox.getWidth()+_bordoBianco, (int)bbox.getHeight()+_bordoBianco);
	     frame.add(this);
	     frame.setVisible(true);

	}

	public void save(String filename)
	{
		this.setBackground(Color.white);
		BufferedImage bImg = new BufferedImage(this.getWidth(), this.getHeight(), BufferedImage.TYPE_INT_RGB);
		Graphics2D cg = bImg.createGraphics();
		cg.setBackground(Color.white);
		this.paintAll(cg);
		try {
			if (ImageIO.write(bImg, "png", new File(filename)))
			{
				System.out.println("-- saved");
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void paintComponent(Graphics g)
	{    
		RenderingUtil util=new RenderingUtil(bbox);
		Graphics2D g2 = (Graphics2D) g;
		for(RiskArea questaarea:_aree)
		{
			Color colreDiQuestaArea=util.getColor(questaarea.getRiskFactor());
			g2.setColor(colreDiQuestaArea);
			java.awt.Polygon boundaries = util.getRenderingBoundaries(questaarea.getShape());
		
			g2.fillPolygon(boundaries);
			
			g2.setColor(Color.BLACK);
			List<Rectangle2D> renderingTiles=util.getRenderingTiles(questaarea.getTiles());
			for(Rectangle2D r:renderingTiles)
	        {
				g2.drawRect((int)r.getMinX(), (int)r.getMinY(), (int)r.getWidth(), (int)r.getHeight());
	        }
		}
		for(Wall questomuro:_muri)
		{
			g2.setColor(Color.darkGray);
			g2.fillPolygon(util.getRenderingBoundaries(questomuro.getShape()));
		}
		for(Pole questopalo:_pali)
		{
			g2.setColor(Color.darkGray);
			g2.fillPolygon(util.getRenderingBoundaries(questopalo.getShape()));
		}
		for(SecurityCamera questatelecamera:_telecamere)
		{
			//Arc2D camera=util.getArc2D(questatelecamera);
			/**
			 *  creo un colore con delle trasparenze (100 su 255) a partire da grigio chiaro;
			 */
			Color grigio= new Color(Color.lightGray.getRed(),Color.lightGray.getGreen(),Color.lightGray.getBlue(),100);
			g2.setColor(grigio);
			
			//g2.fill(camera);
			
			//Color verde= new Color(Color.green.getRed(),Color.green.getGreen(),Color.green.getBlue(),100);
			//g2.setColor(verde);
			g2.fillPolygon(util.getCoverageAsArc2D(questatelecamera));
		
			//int x = (int) questatelecamera.getRegistrationPoint().getX();
			//int y = (int) questatelecamera.getRegistrationPoint().getY();
			//int raggio = questatelecamera.getRaggio();
			//g.fillArc(x,y,raggio,raggio,(int) camera.getAngleStart(),(int) camera.getAngleExtent());
			//g.fillArc((int)camera.getMinX(),(int) camera.getMinY(),(int) camera.getWidth(),(int) camera.getHeight(),(int) camera.getAngleStart(),(int) camera.getAngleExtent());
		}
		
	}
	
	private Rectangle defineBoundingBox(BuiltEnvironment ambiente)
	{
		Envelope bbox = ambiente.getMaxBoundingBox();
		int minx = (int)bbox.getMinX();
		int miny = (int)bbox.getMinY();
		int deltaX = (int) (bbox.getWidth());
		int deltaY = (int) (bbox.getHeight());
		java.awt.Rectangle result = new java.awt.Rectangle(minx,miny,deltaX,deltaY);
		return result;
	}
	
	
	
}
