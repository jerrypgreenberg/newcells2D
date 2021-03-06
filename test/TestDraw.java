/*

	 * put your module comment here
 * formatted with JxBeauty (c) johann.langhofer@nextra.at
 *
 * formatted with JxBeauty (c) johann.langhofer@nextra.at
 *
 * formatted with JxBeauty (c) johann.langhofer@nextra.at
 *
 * formatted with JxBeauty (c) johann.langhofer@nextra.at
 *
 * formatted with JxBeauty (c) johann.langhofer@nextra.at
 *
 * formatted with JxBeauty (c) johann.langhofer@nextra.at
 *
 * formatted with JxBeauty (c) johann.langhofer@nextra.at
 */


import  java.awt.*;
import  java.util.*;
import  javax.swing.*;
import  java.awt.geom.*;
import  java.io.*;
import java.awt.Component;
import java.awt.Graphics2D;
import java.awt.Stroke;
import java.awt.BasicStroke;

enum Render
{
    PRINT,
    NO_PRINT;
}

/**
 * put your documentation comment here
 */
public class TestDraw extends JApplet {
    private static final int XSCREENSIZE = 700, YSCREENSIZE = 700;
    private static final int XCONTROLSIZE = 0, YCONTROLSIZE = 200;
    private static final int XDISPLAY = XSCREENSIZE - XCONTROLSIZE;
    private static final int YDISPLAY = YSCREENSIZE - YCONTROLSIZE;
    private final int XMIDDLE = XSCREENSIZE/2;
    private static final int CIRCLE_WIDTH = 10;
    private double scale;
    double xDisp = 0.;
    double yDisp = 0.;
    double xCenter = 0.;
    double yCenter = 0.;
    double vLength = 0;
    private static final double DTR = Math.PI/180.;
    private double coords[][]= new double[2][3];
    private double line[] = new double[3];
    private double scaleX, scaleY;
    public static final double ELLIPSE_MAJOR = 0.4;
    public static final double ELLIPSE_MINOR = .07;
    private double x0,y0,x1, y1, x2, y2;
    private double x1s,y1s,x2s,y2s;
    private double dx, dy;
    private double x,y;
    Ellipse2D.Double circle,littleCircle,ellipse,littleEllipse;
    Ellipse2D.Double veryLittleCircle;
    public void init () {
        setSize(XSCREENSIZE, YSCREENSIZE);
        setScale(1.0);
        setScaleX((1.6*30)/(getScale()*5), XSCREENSIZE);
        setScaleY((1.6*30)/(getScale()*5), YSCREENSIZE);
        circle = new Ellipse2D.Double(0,0 , CIRCLE_WIDTH, CIRCLE_WIDTH);
        setXcenter(0.);
        setYcenter(0.);
        setVisible(true);
    }

    /**
     * put your documentation comment here
     * @param g
     */
    public void paint(Graphics g) {
        double angle;
        double alignAngle;
        double x0cos,x0sin;
        double x1cos,x1sin;
        int i, j,n;
        Rectangle2D.Double rect;
        Graphics2D g2d = (Graphics2D)g;
        ellipse = new Ellipse2D.Double(0, 0, ELLIPSE_MAJOR*getScaleX(), ELLIPSE_MINOR*getScaleY());
        g2d.setColor(Color.white);
        g2d.clearRect(0, 0, XSCREENSIZE, YDISPLAY);
        g2d.setClip(0, 0, XSCREENSIZE, YDISPLAY);
        g2d.setColor(Color.black);
        g2d.setTransform(new AffineTransform());
        g2d.translate(XMIDDLE - CIRCLE_WIDTH/2, YDISPLAY - CIRCLE_WIDTH/2);
        g2d.translate(-getXdisp()*getScaleX(),getYdisp()*getScaleY());
        g2d.draw(circle);
        x1 = getScaleX(); 
        n = (int) (x1*10*5);
        x1 *= 5;
        x2 = 0.1*getScaleX();
        for(i=0,x=0;i<n;++i,x += x2)
        {
          g2d.setTransform(new AffineTransform());
          g2d.translate((int) (x+XMIDDLE),(int) (YDISPLAY));
          g2d.translate(-getXdisp()*getScaleX(),getYdisp()*getScaleY());
          if(i%10 == 0)
            g2d.drawLine(0,0,0,-20);
          else if(i%5 == 0)
            g2d.drawLine(0,0,0,-15);
          else
            g2d.drawLine(0,0,0,-10);
          g2d.setTransform(new AffineTransform());
          g2d.translate((int) (-x+XMIDDLE),(int) (YDISPLAY));
          g2d.translate(-getXdisp()*getScaleX(),getYdisp()*getScaleY());
          if(i%10 == 0)
            g2d.drawLine(0,0,0,-20);
          else if(i%5 == 0)
            g2d.drawLine(0,0,0,-15);
          else
            g2d.drawLine(0,0,0,-10);
          g2d.setTransform(new AffineTransform());

        }
        y1 = getScaleY(); 
        n = (int) (y1*10*5);
        y1 *= 5;
        y2 = 0.1*getScaleY();
        for(i=0,y=0;i<n;++i,y += y2)
        {
          g2d.setTransform(new AffineTransform());
          g2d.translate((int) 0,(int) (YDISPLAY - y));
          g2d.translate(0,-getYdisp()*getScaleY());
          if(i%10 == 0)
            g2d.drawLine(0,0,20,0);
          else if(i%5 == 0)
            g2d.drawLine(0,0,15,0);
          else
            g2d.drawLine(0,0,10,0);
        }
        for(i=0,y=0;i<n;++i,y += y2)
        {
          g2d.setTransform(new AffineTransform());
          g2d.translate((int) XDISPLAY,(int) (YDISPLAY - y));
          g2d.translate(0,-getYdisp()*getScaleY());
          if(i%10 == 0)
            g2d.drawLine(0,0,-20,0);
          else if(i%5 == 0)
            g2d.drawLine(0,0,-15,0);
          else
            g2d.drawLine(0,0,-10,0);
        }
        coords[0][0] = 0.;
        coords[0][1] = 1.0;
        coords[0][2] = 0.;
        x0 = coords[0][0];
        y0 = coords[0][1];
        coords[1][0] = 0.;
        coords[1][1] = 4.0;
        coords[1][2] = 0.;
        x1 = coords[1][0];
        y1 = coords[1][1];
        angle=45;
        g2d.setTransform(new AffineTransform());
        g2d.translate(XMIDDLE,YDISPLAY);
        g2d.translate(-getXdisp()*getScaleX(),getYdisp()*getScaleY());
        g2d.translate(x1*getScaleX(), -y1*getScaleY());
        g2d.rotate((angle- 90)*DTR);
        g2d.translate(- (ELLIPSE_MAJOR/2)*getScaleX(), - (ELLIPSE_MINOR/2)*getScaleY());
        g2d.setColor(Color.black);
        g2d.fill(ellipse);
        
        x1cos=Math.cos(angle*DTR);
        x1sin=Math.sin(angle*DTR);
        dx = (x1 - (ELLIPSE_MAJOR/2)*x1sin) - x0;
        dy = (y1 - (ELLIPSE_MAJOR/2)*x1cos) - y0;
        alignAngle = CellSimulation.getCalculatedAngle(dx, dy);
        x0cos=Math.cos(alignAngle);
        x0sin=Math.sin(alignAngle);
        System.out.println("ALIGN ANGLE " + alignAngle/DTR);
        x0 = coords[0][0];
        y0 = coords[0][1];
        g2d.setTransform(new AffineTransform());
        g2d.translate(XMIDDLE, YDISPLAY);
        g2d.translate(-getXdisp()*getScaleX(),getYdisp()*getScaleY());
        g2d.translate(x0*getScaleX(), -y0*getScaleY());
        g2d.translate(dx*getScaleX(),-dy*getScaleY());
        g2d.translate(-(ELLIPSE_MAJOR/2)*x0sin*getScaleX(),(ELLIPSE_MAJOR/2)*x0cos*getScaleY());
        g2d.setColor(Color.black);
        g2d.rotate(alignAngle - 90*DTR);
        g2d.translate(- (ELLIPSE_MAJOR/2)*getScaleX(), - (ELLIPSE_MINOR/2)*getScaleY());
        g2d.fill(ellipse);
    }

    /**
     * put your documentation comment here
     * @param xSize
     * @param xScreen
     */
    public void setScaleX (double xSize, int xScreen) {
        scaleX = (double)(xScreen)/xSize;
    }

    /**
     * put your documentation comment here
     * @param ySize
     * @param yScreen
     */
    public void setScaleY (double ySize, double yScreen) {
        scaleY = (double)yScreen/ySize;
    }

    /**
     * put your documentation comment here
     * @return 
     */
    public double getScaleX () {
        return  (scaleX);
    }

    /**
     * put your documentation comment here
     * @return 
     */
    public double getScaleY ()
    {
        return  (scaleY);
    }
    public void setXdisp(double x)
    {
         xDisp = x;
    }
    public void setYdisp(double y)
    {
         yDisp = y;
    }
    public double getYdisp()
    {
         return(yDisp);
    }
    public double getXdisp()
    {
         return(xDisp);
    }
    public void setScale(double sc)
    {
         scale = sc;
    }
    public double getScale()
    {
         return(scale);
    }
    public void setXcenter(double xCen)
    {
         xCenter = xCen;
    }
    public double getXcenter()
    {
         return(xCenter);
    }
    public void setYcenter(double yCen)
    {
         yCenter = yCen;
    }
    public double getYcenter()
    {
         return(yCenter);
    }
}
