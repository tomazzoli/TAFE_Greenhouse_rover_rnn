package it.tomazzoli.ai.bim.test;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Polygon;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JPanel;

public class Delaunay2 extends JPanel  
{
    private int n = 10;                          // number of points
    Polygon star ;
    private int [] x = {450, 550, 750, 550, 630, 430, 170, 310, 120, 350, 450};
    private int [] y = {210, 450, 510, 630, 880, 680, 850, 580, 410, 430,210};
    List<Polygon> triangoli;
    public Delaunay2() 
    {
    	
    	star = new Polygon(x,y,n);
    	triangoli=delaunaizzazione();
    	
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
    
    private synchronized List<Polygon> delaunaizzazione()
    {
    	List<Polygon> result=new ArrayList<Polygon>();
    	// determine if i-j-k is a circle with no interior points
        for (int i = 0; i < n; i++) 
        {
            for (int j = i+1; j < n; j++) 
            {
                for (int k = j+1; k < n; k++) 
                {
                    boolean isTriangle = true;
                    for (int a = 0; a < n; a++) 
                    {
                        if (a == i || a == j || a == k) continue;
                        Point p=new Point();
                    	p.x=x[a];
                    	p.y=y[a];
                    	if(star.contains(p))
                    	{
                    		isTriangle = false;
                            break;
                    	}
                    }
                    if (isTriangle) 
                    {
                    	int [] xt = {x[i],x[j],x[k]};
                    	int [] yt = {y[i],y[j],y[k]};
                    	Polygon triangolo=new Polygon(xt,yt,3);
                    	result.add(triangolo);
                    	System.out.println(triangolo);
                    }
                }
            }
        }
        return result;
    }
    
    public void paintComponent(Graphics g)
	{    
    	g.setColor(Color.lightGray);
    	g.fillPolygon(star);
    	g.setColor(Color.blue);
    	for(Polygon t:triangoli)
		{
			g.drawPolygon(t);
		}
	}
    
    public static void main(String[] args) 
	{
    	Delaunay2 myself=new Delaunay2();
    	
    	myself.paint();
	}
    
}
