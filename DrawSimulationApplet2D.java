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
import  java.awt.event.*;
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
public class DrawSimulationApplet2D extends JApplet
        implements ActionListener {
    private static double BIG = 1000000.;
    private static double STEP = 0.1;
    private static final double DTR = Math.PI/180.;
    private static final int XSCREENSIZE = 700, YSCREENSIZE = 700;
    private static final int XCONTROLSIZE = 0, YCONTROLSIZE = 200;
    private static final int XDISPLAY = XSCREENSIZE - XCONTROLSIZE;
    private static final int YDISPLAY = YSCREENSIZE - YCONTROLSIZE;
    private final int XMIDDLE = XSCREENSIZE/2;
    private static final int CIRCLE_WIDTH = 10;
    private static final int VERY_LITTLE_ELLIPSE_MAJOR = 10;
    private static final int VERY_LITTLE_ELLIPSE_MINOR = 3;
    private static final int LITTLE_ELLIPSE_MAJOR = 10;
    private static final int LITTLE_ELLIPSE_MINOR = 3;
    private static final int LITTLE_CIRCLE_WIDTH = 3;
    private static final double BALL_RADIUS = 0.2;
    private static final double BALL_RADIUS2 = BALL_RADIUS*BALL_RADIUS;
    private static final int RECTANGLE_WIDTH = 6;
    private static final double MIN_ANGLE_INIT = 30;
    private static final double MAX_ANGLE_INIT = 60;
    private static final double SPREAD_INIT = 120;
    private static final int SKIP_CELL_GROWTH_STEPS = 5;
    private static final int BRANCH_INIT = 2;
    private static final double FRACTION_INIT = 1.0;
    private static final int INTERMEDIATE_BRANCH_INIT = 2;
    private static final String STRING = "ATOM  %5d    P RES          %8.3f%8.3f%8.3f%n";
    private static CellSimulation cs;
    private JButton advance, rewind, newseed,metanephricButton;
    private JLabel minAngleLabel;
    private JTextField minAngleField;
    private JLabel maxAngleLabel;
    private JTextField maxAngleField;
    private JLabel fractionLabel;
    private JTextField fractionField;
    private JPanel controls;
    private JPanel options;
    private JPanel overall;
    private JLabel intermediateLabel;
    private JTextField intermediateField;
    private JLabel spreadLabel;
    private JTextField spreadField;
    private JLabel cellSkipLabel;
    private JTextField cellSkipField;
    private JLabel scaleLabel;
    private JLabel dummyLabel1;
    private JLabel dummyLabel2;
    private JLabel dummyLabel3;
    private JLabel normalLabel,metanephricLabel;
    private JTextField normalField,metanephricField;
    private JTextField scaleField;
    private double scaleX, scaleY;
    private int iter;
    public double minAngle;
    public double maxAngle;
    public double spreadAngle;
    public int cellSkip;
    public int maxBranch;
    public int maxIntermediateBranch;
    public double  secondAngle;
    public double fraction;
    private int count = 0;
    private double probe;
    private double step;
    private double s, sp1;
    // private boolean contourOn = false;
    long seed;
    Random random;
    Ellipse2D.Double circle,littleCircle,ellipse,littleEllipse;
    Ellipse2D.Double veryLittleCircle;
    private Vector currentConfig;
    private Vector track = new Vector();
    private double x,y;
    private int i;
    private final int MAX_ITER = 1000;
    private boolean ellipses = false;
    private double scale;
    double xPrev[][] = new double[MAX_ITER][CellSimulation.NUM_METANEPHRIC_CELLS];
    double yPrev[][] = new double[MAX_ITER][CellSimulation.NUM_METANEPHRIC_CELLS];
    double xDisp = 0.;
    double yDisp = 0.;
    double xCenter = 0.;
    double yCenter = 0.;

    /**
     * put your documentation comment here
     */
    public void init () {
        setSize(XSCREENSIZE, YSCREENSIZE);
        currentConfig = new Vector();
        seed = 87236725L;
        random = new Random(seed);
        setIter(30);
        setMinAngle(MIN_ANGLE_INIT);
        setMaxAngle(MAX_ANGLE_INIT);
        setMaxBranch(BRANCH_INIT);
        setSpreadAngle(SPREAD_INIT);
        setCellSkip(SKIP_CELL_GROWTH_STEPS);
        setScale(1.0);
        setFraction(FRACTION_INIT);
        setMaxIntermediateBranch(INTERMEDIATE_BRANCH_INIT);
        setupSimulation(seed);
        advance = new JButton("ADVANCE");
        metanephricButton = new JButton("ELLIPSES");
        rewind = new JButton("REWIND");
        newseed = new JButton("NEW SEED");
        // contour = new JButton("CONTOUR");
        minAngleLabel = new JLabel("MIN ANGLE");
        minAngleField = new JTextField(Double.toString(getMinAngle()), 5);
        minAngleField.setActionCommand("MIN ANGLE");
        maxAngleLabel = new JLabel("MAX ANGLE");
        maxAngleField = new JTextField(Double.toString(getMaxAngle()), 5);
        maxAngleField.setActionCommand("MAX ANGLE");
        fractionLabel = new JLabel("FRACTION");
        fractionField = new JTextField(Double.toString(getFraction()), 5);
        fractionField.setActionCommand("FRACTION");
        spreadLabel = new JLabel("SPREAD ANGLE");
        spreadField = new JTextField(Double.toString(getSpreadAngle()), 5);
        spreadField.setActionCommand("SPREAD ANGLE");
        cellSkipLabel = new JLabel("SKIP CELL GROWTH");
        cellSkipField = new JTextField(Integer.toString(getCellSkip()), 5);
        cellSkipField.setActionCommand("SKIP CELL GROWTH");
        scaleLabel = new JLabel("SCALE");
        dummyLabel1 = new JLabel("");
        dummyLabel2 = new JLabel("");
        dummyLabel3 = new JLabel("");
        normalLabel = new JLabel("NORMAL CENTER");
        metanephricLabel = new JLabel("METANEPHRIC CENTER");
        scaleField = new JTextField(Double.toString(getScale()), 5);
        scaleField.setActionCommand("SCALE");
        normalField = new JTextField(Integer.toString(0), 5);
        normalField.setActionCommand("NORMAL CENTER");
        metanephricField = new JTextField(Integer.toString(0), 5);
        metanephricField.setActionCommand("METANEPHRIC CENTER");
        advance.addActionListener(this);
        metanephricButton.addActionListener(this);
        rewind.addActionListener(this);
        newseed.addActionListener(this);
        // contour.addActionListener(this);
        minAngleField.addActionListener(this);
        maxAngleField.addActionListener(this);
        fractionField.addActionListener(this);
        fractionField.addActionListener(this);
        spreadField.addActionListener(this);
        cellSkipField.addActionListener(this);
        scaleField.addActionListener(this);
        normalField.addActionListener(this);
        metanephricField.addActionListener(this);
        Container pane = getContentPane();
        controls = new JPanel();
        controls.setLayout(new GridLayout(1, 3));
        controls.add(advance);
        controls.add(rewind);
        controls.add(newseed);
   
        options = new JPanel();
        options.setLayout(new GridLayout(5, 4));
        options.add(minAngleLabel);
        options.add(minAngleField);
        options.add(maxAngleLabel);
        options.add(maxAngleField);
        options.add(fractionLabel);
        options.add(fractionField);
        options.add(spreadLabel);
        options.add(spreadField);
        options.add(cellSkipLabel);
        options.add(cellSkipField);
        options.add(metanephricButton);
        options.add(dummyLabel1);
        options.add(scaleLabel);
        options.add(scaleField);
        options.add(dummyLabel2);
        options.add(dummyLabel3);
        options.add(normalLabel);
        options.add(normalField);
        options.add(metanephricLabel);
        options.add(metanephricField);
        // controls.add(contour);
        overall = new JPanel();
        overall.setLayout(new BorderLayout());
        overall.add(controls,BorderLayout.NORTH);
        overall.add(options,BorderLayout.SOUTH);
        pane.add(overall, BorderLayout.SOUTH);
        setVisible(true);
        controls.setVisible(true);
        setScaleX(cs.getStepLength()*getIter()*1.6/(getCellSkip()*getScale()), XSCREENSIZE);
        setScaleY(cs.getStepLength()*getIter()*1.6/(getCellSkip()*getScale()), YSCREENSIZE);
        circle = new Ellipse2D.Double(0,0 , CIRCLE_WIDTH, CIRCLE_WIDTH);
        littleCircle = new Ellipse2D.Double(0, 0, CellSimulation.ELLIPSE_MAJOR, CellSimulation.ELLIPSE_MINOR);
        veryLittleCircle = new Ellipse2D.Double(0, 0, VERY_LITTLE_ELLIPSE_MAJOR, VERY_LITTLE_ELLIPSE_MINOR);
        ellipse = new Ellipse2D.Double(0, 0, CellSimulation.ELLIPSE_MAJOR*getScaleX(), CellSimulation.ELLIPSE_MINOR*getScaleY());
        littleEllipse = new Ellipse2D.Double(0, 0, LITTLE_ELLIPSE_MAJOR, LITTLE_ELLIPSE_MINOR);
        setXcenter(0.);
        setYcenter(0.);
    }

    /**
     * put your documentation comment here
     * @param g
     */
    public void paint(Graphics g) {
        int i, j,n;
        Types type;
        Line line;
        int cellNum;
        int totalNormalCells;
        int totalMCells;
        int yup = 0;
        int xacross = 0;
        double x0,y0,x1, y1, x2, y2;
        double x1s,y1s,x2s,y2s;
        double dx, dy;
        double dist;
        double theta = 0;
        Cell cell1 = null, cell2 = null;
        Cell tempCell = null;
        Dim temp;
        double bmax, bmin;
        double m,b;
        Rectangle2D.Double rect;
        Graphics2D g2d = (Graphics2D)g;
        g2d.setColor(Color.white);
        g2d.clearRect(0, 0, XSCREENSIZE, YDISPLAY);
        g2d.setClip(0, 0, XSCREENSIZE, YDISPLAY);
        g2d.setColor(Color.black);
        g2d.setTransform(new AffineTransform());
        g2d.translate(XMIDDLE - CIRCLE_WIDTH/2, YDISPLAY - CIRCLE_WIDTH/2);
        g2d.translate(-getXdisp()*getScaleX(),getYdisp()*getScaleY());
        g2d.draw(circle);
        currentConfig.clear();
        totalNormalCells = cs.getNormalCellTotal();
        PrintStream out = null;
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
        g2d.setColor(Color.magenta);
        for (cellNum = 0; cellNum < cs.getAttractiveCellTotal(); ++cellNum) {
             cell1 = cs.getAttractiveCell(cellNum);
             x1 = cell1.getCoordX();
             y1 = cell1.getCoordY();
             g2d.setTransform(new AffineTransform());
             g2d.translate((double) (XMIDDLE - CIRCLE_WIDTH/2), (double) (YDISPLAY - CIRCLE_WIDTH/2));
             g2d.translate(-getXdisp()*getScaleX(),getYdisp()*getScaleY());
             g2d.translate((int) (x1*getScaleX()),(int) (-y1*getScaleY()));
             if(cell1.getPeriodicType() == PeriodicType.CROSSED)
                 g2d.setColor(Color.black);
             g2d.fill(circle);
        }
        g2d.setColor(Color.black);
        g2d.setTransform(new AffineTransform());
        g2d.translate((int) XMIDDLE + getScaleX()*CellSimulation.leftLimit,(int) YDISPLAY-getScaleY()*CellSimulation.upperLimit);
        g2d.translate(-getXdisp()*getScaleX(),getYdisp()*getScaleY());
        rect = new Rectangle2D.Double(0., 0. ,(CellSimulation.rightLimit - CellSimulation.leftLimit)*getScaleX(),(CellSimulation.upperLimit - CellSimulation.lowerLimit)*getScaleY());
         g2d.draw(rect);
        // System.out.println("DRAW CELLS");
        for (cellNum = 0; cellNum < totalNormalCells; ++cellNum) {
            cell1 = cs.getNormalCell(cellNum);
            System.out.println("DRAW CELL NUMBER " + cell1.getCellNumber());
            x1 = cell1.getCoordX();
            y1 = cell1.getCoordY();
            g2d.setColor(Color.black);
            // System.out.println(cell1);
            if (cell1.getSubType() == SubTypes.END)
                g2d.setColor(Color.cyan);
            else if ((cell1.getSubType() == SubTypes.MAIN_R) || (cell1.getSubType() == SubTypes.MAIN_L) || (cell1.getSubType() == SubTypes.MAIN_C)) 
                g2d.setColor(Color.red);
            else if (cell1.getSubType() == SubTypes.MAIN)
                g2d.setColor(Color.green);
            else if (cell1.getSubType() == SubTypes.INTERMEDIATE)
                g2d.setColor(Color.yellow);
            else if (cell1.getSubType() == SubTypes.NORMAL)
                g2d.setColor(Color.pink);
            else if (cell1.getSubType() == SubTypes.LAST)
                g2d.setColor(Color.orange);
            if(cell1.getPeriodicType() == PeriodicType.CROSSED)
                g2d.setColor(Color.black);
            g2d.setTransform(new AffineTransform());
            g2d.translate(XMIDDLE - CIRCLE_WIDTH/2, YDISPLAY - CIRCLE_WIDTH/2);
            g2d.translate(-getXdisp()*getScaleX(),getYdisp()*getScaleY());
            g2d.translate(x1*getScaleX(), -y1*getScaleY());
    
            g2d.drawString(Integer.toString(cell1.getCellNumber()),0,0);
            g2d.fill(circle);
            g2d.setColor(Color.black);
            if (cell1.getLinkCellDown() != null) {
                cell2 = cell1.getLinkCellDown();
                x2 = cell2.getCoordX();
                y2 = cell2.getCoordY();
                dx = (x1 - x2)*getScaleX();
                dy = (y1 - y2)*getScaleY();
                currentConfig.addElement(new Line(x1, x2, y1, y2));
                dist = Math.sqrt(dx*dx + dy*dy);
                rect = new Rectangle2D.Double(0, 0, dist, RECTANGLE_WIDTH);
                theta = CellSimulation.getCalculatedAngle(dx,dy);
                g2d.setTransform(new AffineTransform());
                g2d.translate(-getXdisp()*getScaleX(),getYdisp()*getScaleY());
                g2d.drawLine((int)(x1*getScaleX() + XMIDDLE), (int)(YDISPLAY
                        - y1*getScaleY()), (int)(x2*getScaleX() + XMIDDLE), 
                        (int)(YDISPLAY - y2*getScaleY()));
                g2d.translate(XMIDDLE, YDISPLAY);
                g2d.translate(x2*getScaleX(), -y2*getScaleY());
                g2d.rotate(theta - 90*DTR);
                g2d.translate(0., (-RECTANGLE_WIDTH/2));
                g2d.draw(rect);
            }
        }
        if(count >= CellSimulation.START_METANEPHRIC_CELLS)
        {
           // System.out.println("COUNT " + count);
           for (cellNum = 0; cellNum < cs.getMetanephricCellTotal(); ++cellNum) {
                g2d.setColor(Color.blue);
                cell1 = cs.getMetanephricCell(cellNum);
                tempCell = cell1.getTestCell();
                // System.out.println("DRAW METANEPHRIC CELL " + cell1);
                x0 = cell1.getCoordX();
                y0 = cell1.getCoordY();
                g2d.setTransform(new AffineTransform());
                g2d.translate(XMIDDLE, YDISPLAY);
                g2d.translate(-getXdisp()*getScaleX(),getYdisp()*getScaleY());
                g2d.translate(x0*getScaleX(), -y0*getScaleY());
                if(cell1.getAddedType() == AddedType.ADDED)
                g2d.setColor(Color.red);
                if(cell1.getPeriodicType() == PeriodicType.CROSSED)
                    g2d.setColor(Color.black);
                 if(cell1.getSubType() == SubTypes.LAST)
                    g2d.setColor(Color.gray);
                 if(cell1.getBoundCell() != null)
                   if(cell1.getBoundCell().getNumberOfAttractCells() >= CellSimulation.MAX_ATTRACT)
                       g2d.setColor(Color.blue);
                 // g2d.fill(circle);
                //     if(cs.AllCells[Types.METANEPHRIC.ordinal()].indexOf(cell1) == 71 || cs.AllCells[Types.METANEPHRIC.ordinal()].indexOf(cell1) == 52 || cs.AllCells[Types.METANEPHRIC.ordinal()].indexOf(cell1) == 8)
                  //   {
                 if(ellipses) 
                 {
                    g2d.rotate((cell1.getMangle() - 90)*DTR);
                    g2d.translate(- (CellSimulation.ELLIPSE_MAJOR/2)*getScaleX(), - (CellSimulation.ELLIPSE_MINOR/2)*getScaleY());
                    g2d.fill(ellipse);
                    // if ( cs.AllCells[Types.METANEPHRIC.ordinal()].indexOf(cell1) == 71 ) {
                    if(tempCell != null ) 
                    {
                       g2d.setColor(Color.green);
                       g2d.setTransform(new AffineTransform());
                       g2d.translate(XMIDDLE, YDISPLAY);
                       g2d.translate(-getXdisp()*getScaleX(),getYdisp()*getScaleY());
                       Stroke sTemp = g2d.getStroke();
                       g2d.setStroke(new BasicStroke(10));
                       g2d.drawLine((int) (-(CellSimulation.ELLIPSE_MAJOR/2)*getScaleX()),0,0,0);
                       g2d.setStroke(sTemp);
                       System.out.println("DREW THE LINE");
                       System.out.println("DRAW CELL COORDS " + x0 + " " + y0);
                       System.out.println("TEST ANGLE " + tempCell.getMangle());
                       System.out.println("ATTRACT DRAW " + cs.AllCells[Types.METANEPHRIC.ordinal()].indexOf(tempCell) + " " + tempCell.getCoordX() + " " + tempCell.getCoordY() + " " + tempCell.getCoordZ());
                       g2d.translate(tempCell.getCoordX()*getScaleX(), -tempCell.getCoordY()*getScaleY());
                       g2d.rotate((tempCell.getMangle()-90)*DTR);
                       System.out.println("DRAW SIMULATION TEST NUM " + cs.AllCells[Types.METANEPHRIC.ordinal()].indexOf(tempCell));
                       System.out.println("DRAW SIMULATION MANGLE " + tempCell.getMangle());
//                       g2d.drawLine((int) ((CellSimulation.ELLIPSE_MAJOR/2)*getScaleX()),0,0,0);
//                     g2d.drawLine((int) (-(CellSimulation.ELLIPSE_MAJOR/2)*getScaleX()),0,0,0);
                    }
                }
                else
                {
                g2d.drawString(Integer.toString(cs.AllCells[Types.METANEPHRIC.ordinal()].indexOf(cell1)),0,0);
                g2d.rotate((cell1.getMangle() - 90)*DTR);
                g2d.translate(- LITTLE_ELLIPSE_MAJOR/2, - LITTLE_ELLIPSE_MINOR/2);
                g2d.fill(littleEllipse);
               }
             //}
           }
        }
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
     * @param length
     * @param xscreen
     * @return 
     */
    public double setScaleX (double length, double xscreen) {
        return  (scaleX);
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
    public double getScaleY () {
        return  (scaleY);
    }

    /**
     * put your documentation comment here
     * @param it
     */
    public void setIter (int it) {
        if(it > MAX_ITER)
        {
            System.err.println("MAXIMUM ITERATIONS EXCEEDED " + it);
        }
        iter = it;
    }

    /**
     * put your documentation comment here
     * @return 
     */
    public int getIter () {
        return  (iter);
    }

    private boolean isItAGoodMove (double xBall, double yBall) {
        int j;
        boolean inBall = false;
        double xTrack1, yTrack1, xTrack2, yTrack2;
        double dist1, dist2;
        Dim tr1, tr2;
        for (j = 0; j < track.size() - 1; ++j) {
            tr1 = (Dim)track.elementAt(j);
            xTrack1 = tr1.getX();
            yTrack1 = tr1.getY();
            tr2 = (Dim)track.elementAt(j + 1);
            xTrack2 = tr2.getX();
            yTrack2 = tr2.getY();
            dist1 = getDist2(xTrack1, yTrack1, xBall, yBall);
            dist2 = getDist2(xTrack2, yTrack2, xBall, yBall);
            System.out.println("GOOD MOVE " + dist1 + " " + dist2 + " " + BALL_RADIUS2);
            if (dist1 < BALL_RADIUS2 || dist2 < BALL_RADIUS2)
                return  (false);
        }
        /*
         intersect(xBall,yBall,xtrack2,ytrack2,xtrack3,ytrack3);
         */
        return  (true);
    }

    /**
     * put your documentation comment here
     * @param e
     */
    public void actionPerformed (ActionEvent e) {
        Cell cell1 = null;
        int cellNum;
        int foundCellNum;
        int totalNormalCells;
        int totalMetanephricCells;
        if (e.getActionCommand().compareTo("ADVANCE") == 0) {
            ++count;
//                 if (count < getIter()) {
                cs.updateSimulation(Render.NO_PRINT,count-1);
                  repaint();
//               }
        } 
        else if (e.getActionCommand().compareTo("REWIND") == 0) {
            count = 0;
            Cell.setCellCount(0);
            CellSimulation.updateCount = 0;
            setupSimulation(seed);
            repaint();
        } 
        else if (e.getActionCommand().compareTo("NEW SEED") == 0) {
            count = 0;
            Cell.setCellCount(0);
            CellSimulation.updateCount = 0;
            seed = random.nextLong();
            setupSimulation(seed);
            repaint();
        } 
        else if (e.getActionCommand().compareTo("MIN ANGLE") == 0) {
            count = 0;
            setMinAngle(Double.valueOf(minAngleField.getText()));
            Cell.setCellCount(0);
            setupSimulation(seed);
            repaint();
        } 
        else if (e.getActionCommand().compareTo("MAX ANGLE") == 0) {
            count = 0;
            setMaxAngle(Double.valueOf(maxAngleField.getText()));
            Cell.setCellCount(0);
            setupSimulation(seed);
            repaint();
        } 
        else if (e.getActionCommand().compareTo("FRACTION") == 0) {
            count = 0;
            setFraction(Double.valueOf(fractionField.getText()));
            Cell.setCellCount(0);
            setupSimulation(seed);
            repaint();
        } 
        else if (e.getActionCommand().compareTo("SPREAD ANGLE") == 0) {
            count = 0;
            setSpreadAngle(Double.valueOf(spreadField.getText()));
            Cell.setCellCount(0);
            setupSimulation(seed);
            repaint();
        } 
        else if (e.getActionCommand().compareTo("SKIP CELL GROWTH") == 0) {
            count = 0;
            setCellSkip(Integer.valueOf(cellSkipField.getText()));
            Cell.setCellCount(0);
            setupSimulation(seed);
            repaint();
        } 
        else if (e.getActionCommand().compareTo("SCALE") == 0) {
            setScale(Double.valueOf(scaleField.getText()));
            setScaleX(cs.getStepLength()*getIter()*1.6/(getCellSkip()*getScale()), XSCREENSIZE);
            setScaleY(cs.getStepLength()*getIter()*1.6/(getCellSkip()*getScale()), YSCREENSIZE);
            setXdisp(getXcenter());
            if(getYcenter() != 0)
                 setYdisp(getYcenter() - (cs.getStepLength()*getIter()*1.6/(getCellSkip()*getScale()))/2);
            else
                 setYdisp(0);
            repaint();
        } 
        else if (e.getActionCommand().compareTo("NORMAL CENTER") == 0) {
            foundCellNum = -1;
            totalNormalCells = cs.getNormalCellTotal();
            if(Integer.valueOf(normalField.getText()) >= 0) {
                 for (cellNum = 0; cellNum < totalNormalCells; ++cellNum) {
                    cell1 = cs.getNormalCell(cellNum);
                    if(cell1.getCellNumber() == Integer.valueOf(normalField.getText()))
                    {
                        foundCellNum = cell1.getCellNumber();
                        break;
                    }
                  }
                  System.out.println("CENTER AROUND CELL " + Integer.valueOf(normalField.getText()));
                  if(foundCellNum >= 0) 
                  {
                     setXcenter(cell1.getCoordX());
                     setYcenter(cell1.getCoordY());
                     setXdisp(getXcenter());
                     setYdisp(getYcenter() - (cs.getStepLength()*getIter()*1.6/(getCellSkip()*getScale()))/2);
                     repaint();
                  }
            }
            else
            {
               setXcenter(0);
               setYcenter(0);
               setXdisp(getXcenter());
               setYdisp(getYcenter());
               repaint();
            }
        } 
        else if (e.getActionCommand().compareTo("METANEPHRIC CENTER") == 0) {
            foundCellNum = -1;
            totalMetanephricCells = cs.getMetanephricCellTotal();
            if(Integer.valueOf(metanephricField.getText()) >= 0) {
                 for (cellNum = 0; cellNum < totalMetanephricCells; ++cellNum) {
                      cell1 = cs.getMetanephricCell(cellNum);
                      if(cs.AllCells[Types.METANEPHRIC.ordinal()].indexOf(cell1) == Integer.valueOf(metanephricField.getText()))
                      {
                        foundCellNum = cell1.getCellNumber();
                        break;
                      }
                 }
                 if(foundCellNum >= 0) 
                 {
                    setXcenter(cell1.getCoordX());
                    setYcenter(cell1.getCoordY());
                    setXdisp(getXcenter());
                    setYdisp(getYcenter() - (cs.getStepLength()*getIter()*1.6/(getCellSkip()*getScale()))/2);
                    repaint();
                 }
            }
            else
            {
               setXcenter(0);
               setYcenter(0);
               setXdisp(getXcenter());
               setYdisp(getYcenter());
               repaint();
            }
        } 
        else if (e.getActionCommand().compareTo("ELLIPSES") == 0) {
            ellipses=true;
            metanephricButton.setText("NUMBERS");
            repaint();
        } 
        else if (e.getActionCommand().compareTo("NUMBERS") == 0) {
            ellipses=false;
            metanephricButton.setText("ELLIPSES");
            repaint();
        } 
/*
        else if (e.getActionCommand().compareTo("CONTOUR") == 0) {
            if (contourOn)
                contourOn = false; 
            else 
                contourOn = true;
            repaint();
        }
*/
    }

    /**
     * put your documentation comment here
     * @param x1
     * @param y1
     * @param x2
     * @param y2
     * @return 
     */
    private double getDist2 (double x1, double y1, double x2, double y2) {
        return  ((x2 - x1)*(x2 - x1) + (y2 - y1)*(y2 - y1));
    }

    /**
     * put your documentation comment here
     * @param g
     */
    public void update (Graphics g) {
        paint(g);
    }

    /**
     * put your documentation comment here
     */
    public void setupSimulation () {
        cs = new CellSimulation(getIter(), getMinAngle(), getMaxAngle(),new Cell(0., 0., 0., SubTypes.END),872367251L, 1.0, getFraction(), getMaxBranch(),
                getMaxIntermediateBranch(),getSpreadAngle(),getCellSkip());
    }

    /**
     * put your documentation comment here
     * @param seed
     */
    public void setupSimulation (long seed) {
        cs = new CellSimulation(getIter(), getMinAngle(), getMaxAngle(),new Cell(0., 0., 0., SubTypes.END), seed, 1.0, getFraction(), getMaxBranch(),getMaxIntermediateBranch(),getSpreadAngle(),getCellSkip());
    }

    /**
     * put your documentation comment here
     * @param ang
     */
    public void setMinAngle (double ang) {
        minAngle = ang;
    }
    public void setMaxAngle (double ang) {
        maxAngle = ang;
    }

    /**
     * put your documentation comment here
     * @return 
     */
    public double getMinAngle () {
        return  (minAngle);
    }
    public double getMaxAngle () {
        return  (maxAngle);
    }

    /**
     * put your documentation comment here
     * @param branch
     */
    public void setMaxBranch (int branch) {
        maxBranch = branch;
    }

    /**
     * put your documentation comment here
     * @return 
     */
    public int getMaxBranch () {
        return  (maxBranch);
    }
   public void setSpreadAngle(double a)
   {
        spreadAngle = a;
   }
   public double getSpreadAngle()
   {
        return(spreadAngle);
   }
   public void setCellSkip(int a)
   {
        cellSkip = a;
   }
   public int getCellSkip()
   {
        return(cellSkip);
   }
    /**
     * put your documentation comment here
     * @param branch
     */
    public void setMaxIntermediateBranch (int branch) {
        maxIntermediateBranch = branch;
    }

    /**
     * put your documentation comment here
     * @return 
     */
    public int getMaxIntermediateBranch () {
        return  (maxIntermediateBranch);
    }

    /**
     * put your documentation comment here
     * @param frac
     */
    public void setFraction (double frac) {
        fraction = frac;
    }

    /**
     * put your documentation comment here
     * @return 
     */
    public double getFraction () {
        return  (fraction);
    }
    
    public void setScale(double sc)
    {
         scale = sc;
    }

    public double getScale()
    {
         return(scale);
    }

    public void setXdisp(double x)
    {
         xDisp = x;
    }
    public double getXdisp()
    {
         return(xDisp);
    }
    public void setYdisp(double y)
    {
         yDisp = y;
    }
    public double getYdisp()
    {
         return(yDisp);
    }


    public class Dim {
        double x, y, z;

        /**
         * put your documentation comment here
         * @param         double xin
         * @param         double yin
         * @param         double zin
         */
        public Dim (double xin, double yin, double zin) {
            x = xin;
            y = yin;
            z = zin;
        }

        /**
         * put your documentation comment here
         * @return 
         */
        public double getX () {
            return  (x);
        }

        /**
         * put your documentation comment here
         * @param xin
         */
        public void setX (double xin) {
            x = xin;
        }

        /**
         * put your documentation comment here
         * @return 
         */
        public double getY () {
            return  (y);
        }

        /**
         * put your documentation comment here
         * @param yin
         */
        public void setY (double yin) {
            y = yin;
        }

        /**
         * put your documentation comment here
         * @return 
         */
        public double getZ () {
            return  (z);
        }

        /**
         * put your documentation comment here
         * @param zin
         */
        public void setZ (double zin) {
            z = zin;
        }

        /**
         * put your documentation comment here
         * @param i
         * @return 
         */
        public double getI (int i) {
            switch (i) {
                case 0:
                    return  (getX());
                case 1:
                    return  (getY());
                case 2:
                    return  (getZ());
                default:
                    System.out.println("Dimension Error");
                    System.exit(0);
            }
            return  (0);
        }

        /**
         * put your documentation comment here
         * @param i
         * @param p
         */
        public void setI (int i, double p) {
            switch (i) {
                case 0:
                    x = p;
                    break;
                case 1:
                    y = p;
                    break;
                case 2:
                    z = p;
                    break;
                default:
                    System.out.println("Dimension Error");
                    System.exit(0);
            }
        }
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

    /**
     * put your documentation comment here
     * @param x1
     * @param x2
     * @return 
     */
    private static double lesserOf (double x1, double x2) {
        if (x1 <= x2)
            return  (x1); 
        else 
            return  (x2);
    }

    /**
     * put your documentation comment here
     * @param x1
     * @param x2
     * @return 
     */
    private static double greaterOf (double x1, double x2) {
        if (x1 >= x2)
            return  (x1); 
        else 
            return  (x2);
    }
    private class Line {
        private double x1, x2;
        private double y1, y2;
        private double m;
        private double b;

        /**
         * put your documentation comment here
         */
        private Line () {
        }

        /**
         * put your documentation comment here
         * @param         double x11
         * @param         double x22
         * @param         double y11
         * @param         double y22
         */
        private Line (double x11, double x22, double y11, double y22) {
            x1 = x11;
            x2 = x22;
            y1 = y11;
            y2 = y22;
            if (Math.abs(x2 - x1) < CellSimulation.TOL) {
                m = BIG;
                b = 0;
            } 
            else {
                m = (y2 - y1)/(x2 - x1);
                b = y2 - m*x2;
            }
        }

        /**
         * put your documentation comment here
         * @param yin
         * @return 
         */
        private boolean inYBounds (double yin) {
            if (yin >= lesserOf(y1, y2) && yin <= greaterOf(y1, y2))
                return  (true); 
            else 
                return  (false);
        }

        /**
         * put your documentation comment here
         * @param xin
         * @return 
         */
        private boolean inXBounds (double xin) {
            if (xin >= lesserOf(x1, x2) && xin <= greaterOf(x1, x2))
                return  (true); 
            else 
                return  (false);
        }

        /**
         * put your documentation comment here
         * @param y
         * @return 
         */
        private double getSolutionX (double y) {
            if (m != BIG && m != 0.)
                return  ((y - b)/m); 
            else 
                return  (x1);
        }

        /**
         * put your documentation comment here
         * @param x
         * @return 
         */
        private double getSolutionY (double x) {
            if (m != BIG)
                return  (m*x + b); 
            else 
                return  (BIG);
        }

        /**
         * put your documentation comment here
         * @return 
         */
        private double getX1 () {
            return  (x1);
        }

        /**
         * put your documentation comment here
         * @param x
         */
        private void setX1 (double x) {
            x1 = x;
        }

        /**
         * put your documentation comment here
         * @return 
         */
        private double getX2 () {
            return  (x2);
        }

        /**
         * put your documentation comment here
         * @param x
         */
        private void setX2 (double x) {
            x2 = x;
        }

        /**
         * put your documentation comment here
         * @return 
         */
        private double getY1 () {
            return  (y1);
        }

        /**
         * put your documentation comment here
         * @param y
         */
        private void setY1 (double y) {
            y1 = y;
        }

        /**
         * put your documentation comment here
         * @return 
         */
        private double getY2 () {
            return  (y2);
        }

        /**
         * put your documentation comment here
         * @param y
         */
        private void setY2 (double y) {
            y2 = y;
        }

        /**
         * put your documentation comment here
         * @return 
         */
        private double getM () {
            return  (m);
        }

        /**
         * put your documentation comment here
         * @param mIn
         */
        private void setM (double mIn) {
            m = mIn;
        }

        /**
         * put your documentation comment here
         * @return 
         */
        private double getB () {
            return  (b);
        }

        /**
         * put your documentation comment here
         * @param bIn
         */
        private void setB (double bIn) {
            b = bIn;
        }
    }
    private Line perpendicularLine (double x1, double x2, double y1, double y2) {
        double m;
        double b;
        Line line;
        line = new Line();
        if ((Math.abs(x2 - x1) < CellSimulation.TOL)) {
            m = 0;
            b = x2;
        } 
        else {
            m = (y2 - y1)/(x2 - x1);
            m = -1./m;
            b = y2 - m*x2;
        }
        line.setM(m);
        line.setB(b);
        line.setX2(x2);
        line.setY2(y2);
        line.setX1(x1);
        line.setY1(line.getSolutionY(x1));
        return  (line);
    }
}
