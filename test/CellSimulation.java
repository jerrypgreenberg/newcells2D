import java.lang.Math.*;

import java.util.*;


/**  in the driver, this class in instantiated and thereafter each new iteration is calculated each time the updateSimulation method is called **/

/** Simulation Class **/
public class CellSimulation {
    /** degrees to radians **/
    public static double TOL = 0.00001;
    public static final double ELLIPSE_MAJOR = .2;
    public static final double ELLIPSE_MINOR = .07;
    public static final double DTR = Math.PI / 180.;
    /** cell types : normal, metanephric, attractive **/
    private static final int CELL_TYPES = 3;
    /**  number of static attractive cells **/
    private static final int NUM_ATTRACTIVE_CELLS = 10;
    /** create metanephric cells at this iteration (iters start at 0) **/
    public static final int START_METANEPHRIC_CELLS = 1;
    /** number of metanephric cells created **/
    public static final int NUM_METANEPHRIC_CELLS = 50;
    /** maximum number of cells from main branch **/
    public static final int MAX_NUM_CELLS_BRANCH = 8;
    /** distance from old cell to new cell  **/
    public static final double BRANCH_STEP_LENGTH = 1.;
    /** maximum number of metanephric cell updates **/
    private static final int MAX_METANEPHRIC_ATTRACT_MOVES = 60;
    /** maximum number of attractive cells that are actually used **/
    public static final int MAX_ATTRACT = 10;
    /** max x coordinate **/
    public static double rightLimit;
    /** min x coordinate **/
    public static double leftLimit;
    /** max y coordinate **/
    public static double upperLimit;
    /** min y coordinate **/
    public static double lowerLimit;
    /** origin coordinates **/
    private static final double[] ZERO_COORDS = {
        0., 0., 0.
    };
    /** number of metanephric cellls **/
    public static int updateCount = 0;
    /** main branch step length **/
    private double stepLength;
    /** branch step length **/
    private double branchStepLength;
    /** current iteration count **/
    private int iter;
    /** controls sign for left-right initial branch **/
    private double angleMainSign = 1.;
    /** alternate angle signs **/
    private double angleSign = 1.;
    /** perpendicualar sign for "LAST" cells **/
    private double perpAngleSign = 1.;
    /** seed for random number generation **/
    private long randomSeed = 101010;
    /** upper bound on randomly generated angle **/
    private double maxAngle = 60;
    /** lower bound on randomly generated angle; **/
    private double minAngle = 30;
    /** random number object **/
    private Random random;
    /** maximum branching off main segments **/
    private int maxBranch;
    /** maximum branching off non-main segments **/
    private int maxIntermediateBranch;
    /** if a metanephric cell is with DOCKING_DISTANCE to a normal cell of type LAST or to another metanephric cell that is already docked (maximum of 2 cells docked to one metanephric cell) it will be docked **/
    private double DOCKING_DISTANCE = 0.1;
    /** the angle between the 2 main branchesn **/
    private double spreadAngle = 120.;
    /** number of steps to skip between normal cell updates **/
    private int cellSkip = 4;
    /** fraction that the length between cells is shortened by each iteration (currently initialized at 1) **/
    private double fraction;
    /**  each vector holds references to all cells of a certain type **/
    public Vector[] AllCells;
    /** last cell vector **/
    private Vector lastCells = null;
    /** keep track of first iteration **/
    private boolean first = true;
    /** number of normal cell growth iterations **/
    private int cellGrowthCount = 0;
    /** number of metanephric cell growth iterations **/
    private int metanephric_attract_moves = 0;
    /** radius of attractive cells **/
    private double attractiveRadius;
    /** use periodic cell for metanephric cells **/
    private static final boolean METANEPHRIC_CELL_PERIODIC = true;
    /** turn on some extra print's **/
    boolean debug = false;

    public CellSimulation() {}

    public CellSimulation(Cell cell) {
        int i;
        setIter(6);
        setStepLength(1.);
        setBranchStepLength(BRANCH_STEP_LENGTH);
        setMaxAngle(maxAngle);
        setMinAngle(minAngle);
        setSpreadAngle(120);
        setCellSkip(5);
        AllCells = new Vector[CELL_TYPES];

        for (i = 0; i < CELL_TYPES; ++i)
            AllCells[i] = new Vector(0);

        random = new Random(getRandomSeed());
        setFraction(1.);
        cell.setCellNumber(1);
        growCell(cell);
        attractiveRadius = (0.9 * iter) / getCellSkip();
        upperLimit = attractiveRadius * 1.1;
        lowerLimit = 0.0;
        leftLimit = (-(upperLimit - lowerLimit) * 1.6) / 2;
        rightLimit = -leftLimit;

        // System.out.println(" Left " + leftLimit + " Right " + rightLimit +
        //  " Lower " + lowerLimit + " Upper " + upperLimit);
    }

    public CellSimulation(Cell[] cells) {
        int i;
        setIter(5);
        setStepLength(1.);
        setBranchStepLength(BRANCH_STEP_LENGTH);
        setMaxAngle(60);
        setMinAngle(30);
        setSpreadAngle(120);
        setCellSkip(5);
        AllCells = new Vector[CELL_TYPES];

        for (i = 0; i < CELL_TYPES; ++i)
            AllCells[i] = new Vector(0);

        random = new Random(getRandomSeed());
        setMaxBranch(3);
        growCells(cells);
        attractiveRadius = (0.9 * iter) / getCellSkip();
        upperLimit = attractiveRadius * 1.1;
        lowerLimit = 0.0;
        leftLimit = (-(upperLimit - lowerLimit) * 1.4) / 2;
        rightLimit = -leftLimit;

        // System.out.println(" Left " + leftLimit + " Right " + rightLimit +
        //  " Lower " + lowerLimit + " Upper " + upperLimit);
    }

    public CellSimulation(int inIter, double ang, Cell cell) {
        int i;
        setIter(inIter);
        setMaxAngle(60);
        setMinAngle(30);
        setStepLength(1.);
        setBranchStepLength(BRANCH_STEP_LENGTH);
        AllCells = new Vector[CELL_TYPES];

        for (i = 0; i < CELL_TYPES; ++i)
            AllCells[i] = new Vector(0);

        random = new Random(getRandomSeed());
        setFraction(1.);
        setMaxBranch(3);
        setMaxIntermediateBranch(2);
        setSpreadAngle(120);
        setCellSkip(5);
        growCell(cell);
        attractiveRadius = (0.9 * iter) / getCellSkip();
        upperLimit = attractiveRadius * 1.1;
        lowerLimit = 0.0;
        leftLimit = (-(upperLimit - lowerLimit) * 1.4) / 2;
        rightLimit = -leftLimit;

        // System.out.println(" Left " + leftLimit + " Right " + rightLimit +
        //  " Lower " + lowerLimit + " Upper " + upperLimit);
    }

    public CellSimulation(int inIter, double ang, Cell[] cells) {
        int i;
        setIter(inIter);
        setStepLength(1.);
        setBranchStepLength(BRANCH_STEP_LENGTH);
        setMinAngle(30);
        setMaxAngle(60);
        setMaxBranch(3);
        AllCells = new Vector[CELL_TYPES];

        for (i = 0; i < CELL_TYPES; ++i)
            AllCells[i] = new Vector(0);

        random = new Random(getRandomSeed());
        growCells(cells);
        attractiveRadius = (0.9 * iter) / getCellSkip();
        upperLimit = attractiveRadius * 1.1;
        lowerLimit = 0.0;
        leftLimit = (-(upperLimit - lowerLimit) * 1.4) / 2;
        rightLimit = -leftLimit;

        // System.out.println(" Left " + leftLimit + " Right " + rightLimit +
        //  " Lower " + lowerLimit + " Upper " + upperLimit);
    }

    public CellSimulation(int inIter, double minang, double maxang, Cell cell,
        long seed, double length, double fraction, int maxB, int intermediate) {
        int i;
        setIter(inIter);
        setStepLength(length);
        setBranchStepLength(BRANCH_STEP_LENGTH);
        setMinAngle(minang);
        setMaxAngle(maxang);
        setFraction(fraction);
        setMaxBranch(maxB);
        setMaxIntermediateBranch(intermediate);
        AllCells = new Vector[CELL_TYPES];

        for (i = 0; i < CELL_TYPES; ++i)
            AllCells[i] = new Vector(0);

        setRandomSeed(seed);
        random = new Random(getRandomSeed());
        setSpreadAngle(120);
        setCellSkip(5);
        growCell(cell);
        attractiveRadius = (0.9 * iter) / getCellSkip();
        upperLimit = attractiveRadius * 1.1;
        lowerLimit = 0.0;
        leftLimit = (-(upperLimit - lowerLimit) * 1.4) / 2;
        rightLimit = -leftLimit;

        // System.out.println(" Left " + leftLimit + " Right " + rightLimit +
        //  " Lower " + lowerLimit + " Upper " + upperLimit);
    }

    /** The constructor currently called by DrawSimulationApplet2D **/
    public CellSimulation(int inIter, double minang, double maxang, Cell cell,
        long seed, double length, double fraction, int maxB, int intermediate,
        double spread, int skip) {
        int i;
        setIter(inIter);
        setStepLength(length);
        setBranchStepLength(BRANCH_STEP_LENGTH);

        // System.out.println("RANDOM ANGLE MIN IN " + minang);
        // System.out.println("RANDOM ANGLE MAX IN " + maxang);
        setMinAngle(minang);
        setMaxAngle(maxang);
        setFraction(fraction);
        setMaxBranch(maxB);
        setMaxIntermediateBranch(intermediate);
        setSpreadAngle(spread);
        setCellSkip(skip);
        AllCells = new Vector[CELL_TYPES];

        for (i = 0; i < CELL_TYPES; ++i)
            AllCells[i] = new Vector(0);

        setRandomSeed(seed);
        random = new Random(getRandomSeed());
        growCell(cell);
        attractiveRadius = (0.9 * iter) / getCellSkip();
        upperLimit = attractiveRadius * 1.1;
        lowerLimit = 0.0;
        leftLimit = (-(upperLimit - lowerLimit) * 1.4) / 2;
        rightLimit = -leftLimit;

        // System.out.println(" Left " + leftLimit + " Right " + rightLimit +
        //  " Lower " + lowerLimit + " Upper " + upperLimit);
    }

    public CellSimulation(int inIter, double minang, double maxang, Cell cell,
        Cell[] cells, long seed, double length, double fraction, int maxB,
        int intermediate) {
        int i;
        setIter(inIter);
        setStepLength(length);
        setBranchStepLength(BRANCH_STEP_LENGTH);
        setMinAngle(minang);
        setMaxAngle(maxang);
        setFraction(fraction);
        setMaxBranch(maxB);
        setMaxIntermediateBranch(intermediate);
        AllCells = new Vector[CELL_TYPES];

        for (i = 0; i < CELL_TYPES; ++i)
            AllCells[i] = new Vector(0);

        setRandomSeed(seed);
        random = new Random(getRandomSeed());
        setSpreadAngle(120);
        setCellSkip(4);
        growCell(cell);
        growCells(cells);
        attractiveRadius = (0.9 * iter) / getCellSkip();
        upperLimit = attractiveRadius * 1.1;
        lowerLimit = 0.0;
        leftLimit = (-(upperLimit - lowerLimit) * 1.4) / 2;
        rightLimit = -leftLimit;

        // System.out.println(" Left " + leftLimit + " Right " + rightLimit +
        //  " Lower " + lowerLimit + " Upper " + upperLimit);
    }

    public CellSimulation(int inIter, double minang, double maxang,
        Cell[] cells, long seed, double length, double fraction) {
        int i;
        setIter(inIter);
        setStepLength(length);
        setBranchStepLength(BRANCH_STEP_LENGTH);
        setFraction(fraction);
        setMinAngle(minang);
        setMaxAngle(maxang);
        setSpreadAngle(120);
        setCellSkip(4);
        AllCells = new Vector[CELL_TYPES];

        for (i = 0; i < CELL_TYPES; ++i)
            AllCells[i] = new Vector(0);

        setRandomSeed(seed);
        random = new Random(getRandomSeed());
        growCells(cells);
        attractiveRadius = (0.9 * iter) / getCellSkip();
        upperLimit = attractiveRadius * 1.1;
        lowerLimit = 0.0;
        leftLimit = (-(upperLimit - lowerLimit) * 1.4) / 2;
        rightLimit = -leftLimit;

        // System.out.println(" Left " + leftLimit + " Right " + rightLimit +
        //  " Lower " + lowerLimit + " Upper " + upperLimit);
    }

    /** set the angle between the two main branches **/
    public void setSpreadAngle(double a) {
        spreadAngle = a;
    }

    /** get the angle between the two main branches **/
    public double getSpreadAngle() {
        return (spreadAngle);
    }

    /** set how many steps are between normal cell updates **/
    public void setCellSkip(int a) {
        cellSkip = a;
    }

    /** get how many steps are between normal cell updates **/
    public int getCellSkip() {
        return (cellSkip);
    }

    /** set the maximum random angle **/
    public void setMaxAngle(double ang) {
        maxAngle = ang;
    }

    /** set the minimum random angle **/
    public void setMinAngle(double ang) {
        minAngle = ang;
    }

    /** get the minimum random angle **/
    public double getMinAngle() {
        return (minAngle);
    }

    /** get the maximum random angle **/
    public double getMaxAngle() {
        return (maxAngle);
    }

    /** return a random angle for a cell growing off the main branch, alternating sign **/
    public double getMainRandomAngle() {
        if (angleMainSign > 0) {
            angleMainSign = -1;
        } else {
            angleMainSign = 1;
        }

        return (random.nextDouble() * 30. * angleMainSign);
    }
    /** return a random angle with alternating sign: minAngle + (maxAngle - minAngle)*random*sign **/
    public double getRandomAngle() {
        double angleDiff = getMaxAngle() - getMinAngle();

        if (angleSign > 0) {
            angleSign = -1;
        } else {
            angleSign = 1;
        }

        // System.out.println("ATTRACT RAN ANGLE SIGN " + angleSign);
        return ((getMinAngle() + (angleDiff * random.nextDouble())) * angleSign);
    }

    /** set the maximum number of cells that can grow from a cell **/
    public void setMaxBranch(int it) {
        maxBranch = it;
    }

    /** get the maximum number of cells that can grow from a cell **/
    public int getMaxBranch() {
        return (maxBranch);
    }

    /** set the total number of iterations **/
    public void setIter(int it) {
        iter = it;
    }

    /** get the total number of iterations **/
    public int getIter() {
        return (iter);
    }

    /** get the maximum distance between a cell and its child **/
    public double getStepLength() {
        return (stepLength);
    }

    /** set the maximum distance between a cell and its child **/
    public void setStepLength(double s) {
        stepLength = s;
    }

    /** get the maximum distance between a cell not on a main branch and its child **/
    public double getBranchStepLength() {
        return (branchStepLength);
    }

    /** set the maximum distance between a cell not on a main branch and its child **/
    public void setBranchStepLength(double s) {
        branchStepLength = s;
    }

    /** return the random seed **/
    public long getRandomSeed() {
        return (randomSeed);
    }

    /** set the random seed **/
    public void setRandomSeed(long seed) {
        randomSeed = seed;
    }

    /** get the value that multiplies the distance between parent and child cells **/
    public double getFraction() {
        return (fraction);
    }

    /** set the value that multiplies the distance between parent and child cells **/
    public void setFraction(double frac) {
        fraction = frac;
    }

    /** print toString() method from Cell class **/
    private void printSimulationStep() {
        int i;
        int j;
        Cell cell;

        for (j = 0; j < getNormalCellTotal(); ++j) {
            System.out.println((Cell) AllCells[Types.NORMAL.ordinal()].elementAt(j));
        }
    }

    /** add a cell to the appropriate cell vector **/
    private void growCell(Cell cell) {
        System.out.println("ADD CELL NO " + cell.getCellNumber());
        AllCells[cell.getType().ordinal()].addElement(cell);
        return;
    }

    /** add an array of cells to the appropriate cell vector **/
    private void growCells(Cell[] cells) {
        int i;
        for (i = 0; i < cells.length; ++i)
            AllCells[cells[0].getType().ordinal()].addElement(cells[i]);

        return;
    }

    /** calculate coordinates for a new normal cell (newCell) based on the position of the cell it "grew"
        from (oldCell), attractions and random motion  **/

    private void placeNewCell(Cell oldCell, Cell newCell, int icount) {
        double dx;
        double dy;
        double dz;
        double dx2;
        double dy2;
        double dz2;
        double x = 0.;
        double y = 0.;
        double theta = 0;
        double tempAngle;
        double scale;
        double ranAngle;
        double alignAngle;
        double maxDist;
        double xMin = 0.;
        double yToXMin = 0.;
        double dist;
        double m1;
        double m2;
        double dotprod;
        double xAttr;
        double yAttr;
        double dist1;
        double dist2;
        Boolean didTransform = new Boolean(false);
        boolean tooCloseAngle;
        int noAttrCells;
        Vector distVec;
        int i;
        int j;
        int jattract = -1;
        Cell oldOldCell;
        Cell cellM = null;
        Cell tempCell;
        double totalAngle = 0;
        DistNo tempDistNo;
        dz = 0.;
        distVec = new Vector(0);

        // System.out.println("\nOLDCELL " + oldCell);
        // System.out.println("OLDCELL Type " + oldCell.getSubType());
        // System.out.println("INITIAL NEW CELL " + newCell);
        // System.out.println("ITER COUNT  " + cellGrowthCount);


        /* first normal cell */
        if (oldCell.getLinkCellDown() == null) {
            if (first == false) {
                System.out.println("RETURN oldCell.getLinkCellDown() == null");
                return;
            }

            if (oldCell.getSubType() == SubTypes.INTERMEDIATE) {
                newCell.setCellCount(newCell.getCellCount() - 1);
                System.out.println("RETURN Cell.getSubType() == SubTypes.INTERMEDIATE");

                return;
            }

            newCell.setCoords(Transform.translate(oldCell.getCoords(), 0.,
                1. * getStepLength(), 0.));
            first = false;
            /* all other normal cells */
        } else {
            if (cellGrowthCount <= 1) {
                System.out.println("RETURN Cell.getSubType() == SubTypes.INTERMEDIATE");
                return;
            }

            if (getNumCellsToMain(oldCell) > MAX_NUM_CELLS_BRANCH) {
                System.out.println("RETURN getNumCellsToMain(oldCell)");
                return;
            }

            // System.out.println("OLD CELL SUB TYPE " + oldCell.getSubType());

            /* oldOldCell is the cell that oldCell grew from */
            oldOldCell = oldCell.getLinkCellDown();

            /* distance between oldCell and oldOldCell */
            scale = scaleFactor(oldCell, oldOldCell);

            /* oldCell is on one of the "main" branches, use the current length .
                The initial orientation of all new cells is x=0, y=getStepLength */
            if ((oldCell.getSubType() == SubTypes.MAIN_R) ||
                (oldCell.getSubType() == SubTypes.MAIN_L) ||
                (oldCell.getSubType() == SubTypes.MAIN_C) ||
                (cellGrowthCount <= 2)) {
                newCell.setCoords(Transform.translate(ZERO_COORDS, 0.,
                    getStepLength(), 0.));
            } else if (oldCell.getSubType() == SubTypes.MAIN) {
                newCell.setCoords(Transform.translate(ZERO_COORDS, 0.,
                    getStepLength(), 0.));
            } else {
                /* if newCell is a cell of subtype LAST, then make it 25 % the length of other normal cells */
                if (newCell.getSubType() == SubTypes.LAST) {
                    newCell.setCoords(Transform.translate(ZERO_COORDS, 0.,
                        getBranchStepLength() * 0.25, 0.));
                } else {
                    /* if normal cell iteration count is >= 7 cut the distance from oldCell to newCell by 50% */
                    if (cellGrowthCount >= 7) {
                        newCell.setCoords(Transform.translate(ZERO_COORDS, 0.,
                            getBranchStepLength() * 0.5, 0.));
                    } else {
                        newCell.setCoords(Transform.translate(ZERO_COORDS, 0.,
                            getBranchStepLength(), 0.));
                    }
                }
            }

            /* vector between new and old cell */
            dx = ((oldCell.getCoordX() - oldOldCell.getCoordX()) / scale) * getStepLength();
            dy = ((oldCell.getCoordY() - oldOldCell.getCoordY()) / scale) * getStepLength();
            dz = ((oldCell.getCoordZ() - oldOldCell.getCoordZ()) / scale) * getStepLength();

            /* angle relative to Y axis of vector defined by oldCell and oldoldCell */
            theta = getCalculatedAngle(dx, dy);

            if ((oldCell.getSubType() == SubTypes.MAIN_R) ||
                (oldCell.getSubType() == SubTypes.MAIN_L) ||
                (oldCell.getSubType() == SubTypes.MAIN_C) ||
                (oldCell.getSubType() == SubTypes.MAIN)) {
                /* see the getMainRandomAngle and getRandomAngle methods for a description */
                ranAngle = getMainRandomAngle();
            } else {
                ranAngle = getRandomAngle();

                // System.out.println("RANDOM ANGLE " + ranAngle);
            }

            /* set the cell SubType */
            if (cellGrowthCount == 2) {
                /* for cells # 2 and #3, create two MAIN branches (MAIN_R and MAIN_L) */
                switch (icount) {
                case 0:
                    ranAngle = -getSpreadAngle() / 2.;
                    System.out.print("NEW CELL NUMBER " + newCell.getCellNumber() + " CELL TYPE " + newCell.getType() + " CELL SUBTYPE " + newCell.getSubType());
                    newCell.setSubType(SubTypes.MAIN_L);
                    System.out.println(" CHANGE TO CELL SUBTYPE " + newCell.getSubType());

                    break;

                case 1:
                    ranAngle = getSpreadAngle() / 2.;
                    System.out.print("NEW CELL NUMBER " + newCell.getCellNumber() + " CELL TYPE " + newCell.getType() + " CELL SUBTYPE " + newCell.getSubType());
                    newCell.setSubType(SubTypes.MAIN_R);
                    System.out.println(" CHANGE TO CELL SUBTYPE " + newCell.getSubType());

                    break;

                case 2:
                    ranAngle = 0.;
                    System.out.print("NEW CELL NUMBER " + newCell.getCellNumber() + " CELL TYPE " + newCell.getType() + " CELL SUBTYPE " + newCell.getSubType());
                    newCell.setSubType(SubTypes.MAIN_C);
                    System.out.println(" CHANGE TO CELL SUBTYPE " + newCell.getSubType());

                    break;
                }
            } else if ((oldCell.getSubType() == SubTypes.MAIN_R) ||
                (oldCell.getSubType() == SubTypes.MAIN_L) ||
                (oldCell.getSubType() == SubTypes.MAIN_C)) {
                // System.out.println("MAIN SWITCH");
                if (oldCell.getSubType() == SubTypes.MAIN_R) {
                    System.out.print("NEW CELL NUMBER " + newCell.getCellNumber() + " CELL TYPE " + newCell.getType() + " CELL SUBTYPE " + newCell.getSubType());
                    newCell.setSubType(SubTypes.MAIN_R);
                    System.out.println(" CHANGE TO CELL SUBTYPE " + newCell.getSubType());
                } else if (oldCell.getSubType() == SubTypes.MAIN_L) {
                    System.out.print("NEW CELL NUMBER " + newCell.getCellNumber() + " CELL TYPE " + newCell.getType() + " CELL SUBTYPE " + newCell.getSubType());
                    newCell.setSubType(SubTypes.MAIN_L);
                    System.out.println(" CHANGE TO CELL SUBTYPE " + newCell.getSubType());
                } else if (oldCell.getSubType() == SubTypes.MAIN_C) {
                    System.out.print("NEW CELL NUMBER " + newCell.getCellNumber() + " CELL TYPE " + newCell.getType() + " CELL SUBTYPE " + newCell.getSubType());
                    newCell.setSubType(SubTypes.MAIN_C);
                    System.out.println(" CHANGE TO CELL SUBTYPE " + newCell.getSubType());
                }

                System.out.print("OLD CELL NUMBER " + oldCell.getCellNumber() + " CELL TYPE " + oldCell.getType() + " CELL SUBTYPE " + oldCell.getSubType());
                oldCell.setSubType(SubTypes.MAIN);
                System.out.println(" CHANGE TO CELL SUBTYPE " + oldCell.getSubType());
            } else if (oldCell.getSubType() != SubTypes.MAIN && oldCell.getSubType() != SubTypes.MAIN_L && oldCell.getSubType() != SubTypes.MAIN_R && oldCell.getSubType() != SubTypes.MAIN_C) {
                if ((oldCell.getGrowthCount() >= getMaxIntermediateBranch()) &&
                    (cellGrowthCount >= 2)) {
                    System.out.print("OLD CELL NUMBER " + oldCell.getCellNumber() + " CELL TYPE " + oldCell.getType() + " CELL SUBTYPE " + oldCell.getSubType());
                    oldCell.setSubType(SubTypes.NORMAL);
                    System.out.println(" CHANGE TO CELL SUBTYPE " + oldCell.getSubType());
                } else if (oldCell.getSubType() != SubTypes.INTERMEDIATE) {
                    System.out.print("OLD CELL NUMBER " + oldCell.getCellNumber() + " CELL TYPE " + oldCell.getType() + " CELL SUBTYPE " + oldCell.getSubType());
                    oldCell.setSubType(SubTypes.INTERMEDIATE);
                    System.out.println(" CHANGE TO CELL SUBTYPE " + oldCell.getSubType());
                }
            }

            /* increment count of cells grown from oldCell */
            oldCell.setGrowthCount(oldCell.getGrowthCount() + 1);

            // if(getAttractiveCellTotal() > 0 && oldCell.getSubType() == SubTypes.MAIN)
            jattract = -1;

            /* direct growth towards attractive cells */

            if ((getAttractiveCellTotal() > 0) &&
                (newCell.getSubType() != SubTypes.LAST)) {
                x = oldCell.getCoordX();
                y = oldCell.getCoordY();

                /* distance table  from attactive cell i to normal cell */
                for (i = 0; i < getAttractiveCellTotal(); ++i) {
                    cellM = getAttractiveCell(i);
                    xAttr = cellM.getCoordX();
                    yAttr = cellM.getCoordY();
                    dx2 = x - xAttr;
                    dy2 = y - yAttr;
                    distVec.addElement(new DistNo(i,
                        Math.sqrt((dx2 * dx2) + (dy2 * dy2))));
                }

                sortThem(distVec);
                /* closest attractive cell */
                tempDistNo = (DistNo) distVec.elementAt(0);
                jattract = tempDistNo.getI();
                xMin = getAttractiveCell(jattract).getCoordX();
                yToXMin = getAttractiveCell(jattract).getCoordY();
                dx = xMin - x;
                dy = yToXMin - y;
                alignAngle = getCalculatedAngle(dx, dy);
                alignAngle /= DTR;

                /* rotate towards attractive cell */

                newCell.setCoords(Transform.rotateZ(alignAngle * DTR,
                    newCell.getCoords()));
                totalAngle += alignAngle;

                // System.out.println("ROTATE ANGLE ALIGN ANGLE " + alignAngle);
                // System.out.println("ATTRACTIVE OLD RAN ANGLE " + ranAngle);
                // System.out.println("ATTRACTIVE OLD ANGLE TOTAL (-180 - +180) " +
                //     totalAngle);
                /* add random angle */
                tempAngle = totalAngle + ranAngle;

                if (tempAngle < 0) {
                    tempAngle += 360.;
                }

                if (ranAngle > 180.) {
                    ranAngle = ranAngle - 360.;
                }

                // System.out.println(
                //   "ATTRACTIVE NEW RAN ANGLE AFTER (-180 - +180) " + ranAngle);
                // System.out.println("ATTRACTIVE NEW ANGLE TOTAL  " + totalAngle);
            } else if (newCell.getSubType() == SubTypes.LAST) {
                dx = ((oldCell.getCoordX() - oldOldCell.getCoordX()) / scale) * getStepLength();
                dy = ((oldCell.getCoordY() - oldOldCell.getCoordY()) / scale) * getStepLength();
                dz = ((oldCell.getCoordZ() - oldOldCell.getCoordZ()) / scale) * getStepLength();

                /* rotate 90 deg */

                theta = getCalculatedAngle(dx, dy);
                ranAngle = perpAngleSign * 90.;
                perpAngleSign *= -1;
                totalAngle += ((theta / DTR) + ranAngle);
                newCell.setCoords(Transform.rotateZ(theta, newCell.getCoords()));
            } else {
                //                  System.out.println("NORMAL ALIGN ANGLE " + theta/DTR);
                //                 System.out.println("NORMAL RANDOM ANGLE " + ranAngle);

                /* rotate in line with old cell and old old cell */
                newCell.setCoords(Transform.rotateZ(theta, newCell.getCoords()));
                totalAngle += (theta / DTR);

                // System.out.println("ROTATE ANGLE THETA " + (theta / DTR));
            }

            totalAngle += ranAngle;

            /*  now rotate by random angle */

            newCell.setCoords(Transform.rotateZ(ranAngle * DTR,
                newCell.getCoords()));

            /* transform "tail" of new vector to the coordinates of the old cell */
            newCell.setCoords(Transform.translate(newCell.getCoords(),
                oldCell.getCoords()));
        }

        /* dont add new cell if its beyond arc defined by attractive cells */

        if (jattract >= 0) {
            dx = getAttractiveCell(jattract).getCoordX();
            dy = getAttractiveCell(jattract).getCoordY();
            dist1 = Math.sqrt((dx * dx) + (dy * dy));
            dx = newCell.getCoordX();
            dy = newCell.getCoordY();
            dist2 = Math.sqrt((dx * dx) + (dy * dy));

            if (dist2 > dist1) {
                System.out.println("CELL " + newCell.getCellNumber() + " OUT OF BOUNDS");
                return;
            }
        }

        /* add new cell */

        growCell(newCell);

        if (totalAngle < 0) {
            totalAngle = 360. - totalAngle;
        }

        // System.out.println("TOTAL ANGLE " + totalAngle);
        newCell.setAngle(totalAngle);
    }

    /** move metanephric cells based on attraction to LAST cells and random motion **/
    private void moveMetanenephricCells() {
        int i;
        int j;
        int lastNum;
        double m;
        double b;
        double x1;
        double y1;
        double x2;
        double y2;
        double x3;
        double y3;
        double x4;
        double y4;
        double xmin = 100000.;
        double ymin = 100000.;
        double r;
        double theta;
        double dist = 100000.;
        double dx = 0;
        double dy = 0;
        double dz = 0.;
        double dxp;
        double dyp;
        double dzp;
        double distTemp;
        double alignAngle;
        double ranAngle;
        double[] coords = new double[3];
        double[] coordsdown = new double[3];
        double[] refCoords = {
            0., ELLIPSE_MAJOR / 2, 0.
        };
        double[] rCoords1 = new double[3];
        double[] rCoords2 = new double[3];
        double scale;
        boolean mAttract = false;
        boolean bound = false;
        Cell mCell;
        Cell tempCell = null;
        Cell tempCell2;
        Cell tempCell3 = null;
        Cell newmCell;
        Cell tempMinCell = null;
        Boolean didTransform = new Boolean(false);
        int mCellTotal;

        mCellTotal = getMetanephricCellTotal();

        if (mCellTotal == 0) {
            return;
        }

        lastNum = getLastCellTotal();

        if ((lastNum != 0) &&
            (metanephric_attract_moves <= MAX_METANEPHRIC_ATTRACT_MOVES)) {
            ++metanephric_attract_moves;
        }

        /* loop over metanephric cells */

        for (i = 0; i < mCellTotal; ++i) {
            mCell = getMetanephricCell(i);
            x1 = mCell.getCoordX();
            y1 = mCell.getCoordY();
            bound = false;

            /*  only consider attraction between normal cells of subtype LAST or bound metanephric and unbound umetanephric cells */

            if (mCell.getSubType() != SubTypes.LAST) {
                dist = 100000.;

                if (lastNum != 0) {
                    for (j = 0; j < lastNum; ++j) {
                        tempCell = getLastCell(j);
                        mAttract = false;

                        /* maximum number of bound cells on this LAST cell, don't dock it */
                        if (tempCell.getNumberOfAttractCells() >= MAX_ATTRACT) {
                            continue;
                            /* some metanephric cells are bound to this LAST cell */
                        } else if (tempCell.getNumberOfAttractCells() != 0) {
                            /* LAST cell */
                            tempCell3 = tempCell;
                            /* last metanephric cell bound to this LAST cell */
                            tempCell = tempCell.getLastAttractCell();
                            mAttract = true;
                        }

                        x2 = tempCell.getCoordX();
                        y2 = tempCell.getCoordY();



                        dx = x2 - x1;
                        dy = y2 - y1;
                        dxp = coordDiffX(dx, METANEPHRIC_CELL_PERIODIC,
                            didTransform);



                        dyp = coordDiffY(dy, METANEPHRIC_CELL_PERIODIC,
                            didTransform);

                        distTemp = Math.sqrt((dxp * dxp) + (dyp * dyp));

                        /*  if distance between metanephric cell and LAST cell or a bound metanephric cell */
                        /*  is small enough, dock it */

                        if (distTemp < DOCKING_DISTANCE) {
                            mCell.setSubType(SubTypes.LAST);

                            /* bound to a LAST cell */
                            if (tempCell.getType() == Types.NORMAL) {
                                tempCell.addAttractCells(mCell);
                                mCell.setBoundCell(tempCell);
                                /* bound to another metaneprhic cell  */
                            } else if (tempCell.getType() == Types.METANEPHRIC) {
                                tempCell3.addAttractCells(mCell);
                                mCell.setBoundCell(tempCell3);

                                if (debug)
                                    System.out.println(mCell.getDock(0) + " " +
                                        mCell.getDock(1));
                                if (debug)
                                    System.out.println(tempCell.getDock(0) + " " +
                                        tempCell.getDock(1));
                            } else {
                                System.err.println("Error wrong type of cell");
                                System.exit(-1);
                            }
                            mCell.setDock(tempCell.getCoordX(), tempCell.getCoordX(), 0);

                            if (tempCell.getType() == Types.NORMAL)
                                System.out.println("MCELL " + AllCells[Types.METANEPHRIC.ordinal()].indexOf(mCell) + " DOCKED TO NORMAL CELL " + tempCell.getCellNumber());
                            else
                                System.out.println("MCELL " + AllCells[Types.METANEPHRIC.ordinal()].indexOf(mCell) + " DOCKED TO MCELL " + AllCells[Types.METANEPHRIC.ordinal()].indexOf(tempCell));

                            /* when a metanephric cell docks, create a new one */

                            x4 = (iter * .7 * (-0.5 + random.nextDouble())) / getCellSkip();
                            y4 = (iter * .7 * random.nextDouble()) / getCellSkip();
                            newmCell = new Cell(x4, getFraction() + y4, 0.,
                                Types.METANEPHRIC, SubTypes.NORMAL);
                            newmCell.setAddedType(AddedType.ADDED);
                            AllCells[Types.METANEPHRIC.ordinal()].addElement(newmCell);

                            if (tempCell.getType() == Types.NORMAL) {
                                if (debug)
                                    System.out.println("MCELL # " +
                                        AllCells[Types.METANEPHRIC.ordinal()].indexOf(
                                            mCell) + " bound to Normal cell " +
                                        tempCell);
                            } else {
                                if (debug)
                                    System.out.println("MCELL # " +
                                        AllCells[Types.METANEPHRIC.ordinal()].indexOf(
                                            mCell) + " bound to Normal cell " +
                                        tempCell3 + " next to MCELL " +
                                        AllCells[Types.METANEPHRIC.ordinal()].indexOf(
                                            tempCell));
                            }

                            if (debug)
                                System.out.println("MCELL DOCK " +
                                    mCell.getDock(0) + " " + mCell.getDock(1) +
                                    " DOCKED AT " + tempCell.getDock(0) + " " +
                                    tempCell.getDock(1));

                            bound = true;

                            break;
                        }

                        if (distTemp < dist) {
                            dist = distTemp;
                            xmin = x2;
                            ymin = y2;
                            tempMinCell = tempCell;
                        }
                    }

                    /* if not bound move metanephric cell towards nearest LAST cell */

                    if ((dist < 100000.) &&
                        (metanephric_attract_moves < MAX_METANEPHRIC_ATTRACT_MOVES) && !bound) {

                        rCoords1[0] = 0;
                        rCoords1[1] = 0;
                        rCoords2[0] = 0;
                        rCoords2[1] = 0;
                        if (mAttract == true) {
                            rCoords1 = Transform.rotateZ(-tempMinCell.getMangle() * DTR, refCoords);
                            rCoords2 = Transform.rotateZ(mCell.getMangle() * DTR, refCoords);

                        }
                        dx = xmin - rCoords1[0] - (x1 + rCoords2[0]);
                        dy = ymin - rCoords1[1] - (y1 + rCoords2[1]);

                        /* take into account peridic boundary conditions */

                        dxp = coordDiffX(dx, METANEPHRIC_CELL_PERIODIC,
                            didTransform);

                        dyp = coordDiffY(dy, METANEPHRIC_CELL_PERIODIC,
                            didTransform);

                        coords[0] = 0.;
                        coords[1] = .1;
                        coords[2] = 0.;
                        alignAngle = getCalculatedAngle(dxp, dyp);
                        alignAngle /= DTR;

                        /* angle towards LAST cell */

                        coords = Transform.rotateZ(alignAngle * DTR, coords);
                        ranAngle = 0;
                        //                      ranAngle = getRandomAngle();

                        /* perturb by a random angle */

                        coords = Transform.rotateZ(ranAngle * DTR, coords);
                        mCell.setMangle(alignAngle + ranAngle);
                        mCell.setCoords(Transform.translate(mCell.getCoords(),
                            coords));
                        mCell.setCoords(periodicCoords(mCell.getCoords(),
                            didTransform, mCell.getCellNumber()));

                        if (mAttract) {
                            mCell.setTestCell(tempMinCell);
                            System.out.println("\nCOORD TEMP_MIN_CELL " + AllCells[Types.METANEPHRIC.ordinal()].indexOf(tempMinCell));
                            System.out.println("COORD ATTRACTING " + AllCells[Types.METANEPHRIC.ordinal()].indexOf(mCell));
                            System.out.println("COORDS ANGLE " + mCell.getMangle());
                            System.out.println("ATTRACT TOWARDS DOCKED CELL " + AllCells[Types.METANEPHRIC.ordinal()].indexOf(tempMinCell));

                            System.out.println("ROTATE MCELL " + AllCells[Types.METANEPHRIC.ordinal()].indexOf(mCell) + " TOWARDS DOCKED MCELL " + AllCells[Types.METANEPHRIC.ordinal()].indexOf(tempMinCell));
                        } else
                            System.out.println("ROTATE MCELL " + AllCells[Types.METANEPHRIC.ordinal()].indexOf(mCell) + " TOWARDS LAST CELL " + tempMinCell.getCellNumber());
                        if (didTransform.tf) {
                            if (mCell.getPeriodicType() == PeriodicType.NORMAL) {
                                mCell.setPeriodicType(PeriodicType.CROSSED);
                            } else if (mCell.getPeriodicType() == PeriodicType.CROSSED) {
                                mCell.setPeriodicType(PeriodicType.NORMAL);
                            }
                        }
                    }
                    /* no LAST cells, random movement only */
                } else if (metanephric_attract_moves <= MAX_METANEPHRIC_ATTRACT_MOVES) {
                    // System.out.println("ATTRACT_MOVES #2 ");
                    for (i = 0; i < mCellTotal; ++i) {
                        debug = false;
                        mCell = getMetanephricCell(i);

                        if (mCell.getSubType() != SubTypes.LAST) {
                            r = random.nextDouble();
                            theta = 360 * DTR * random.nextDouble();

                            if (debug) {
                                System.out.println("PERIODIC COORDS Initial " +
                                    mCell.getCoords()[0] + " " +
                                    mCell.getCoords()[1]);
                            }

                            mCell.setCoords(Transform.translate(
                                mCell.getCoords(), r * Math.cos(theta),
                                r * Math.sin(theta), 0.));
                            mCell.setCoords(periodicCoords(mCell.getCoords(),
                                didTransform, mCell.getCellNumber()));

                            if (didTransform.tf) {
                                if (mCell.getPeriodicType() == PeriodicType.NORMAL) {
                                    mCell.setPeriodicType(PeriodicType.CROSSED);
                                } else if (mCell.getPeriodicType() == PeriodicType.CROSSED) {
                                    mCell.setPeriodicType(PeriodicType.NORMAL);
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    /** run the whole simulation (currently not called by driver program) **/
    public void runSimulation() {
        int i;
        int j;
        int initSize;

        for (i = 0; i < iter; ++i) {
            // System.out.println("Iter " + i);
            updateSimulation(Render.PRINT, i);
        }
    }

    /** update the simulation one step at a time  **/
    public void updateSimulation(Render render, int currentIter) {
        int j;
        int initSize;
        initSize = getNormalCellTotal();

        if (currentIter == 0) {
            cellGrowthCount = 0;
        }

        if (currentIter == START_METANEPHRIC_CELLS) {
            placeMetanephricCells();
            placeAttractiveCells();
        }

        if (((currentIter % getCellSkip()) == 0) && (currentIter < getIter())) {
            ++cellGrowthCount;

            /* loop over all normal cells: grow new ones */
            for (j = 0; j < initSize; ++j)
                currentAlgorithm((Cell) AllCells[Types.NORMAL.ordinal()].elementAt(j));

            setStepLength(getStepLength() * getFraction());
            setBranchStepLength(getBranchStepLength() * getFraction());
            if ((cellGrowthCount * getCellSkip()) == iter) {
                updateLastCellList();
            }
        }

        System.out.println("CURRENTITER " + currentIter);

        /* move metanephric cells */
        if (currentIter > START_METANEPHRIC_CELLS) {
            if (debug)
                System.out.println("IN MOVEMETANEPHRIC CELLS " + ++updateCount);
            moveMetanenephricCells();
        }

        if (render == Render.PRINT) {
            printSimulationStep();
        }


        // System.out.println();
    }

    /**
      algorithm for growing normal cells
     **/
    private void currentAlgorithm(Cell cell) {
        int number;
        int direction;
        int i;
        int j;
        System.out.println(" ALGORITHM ITER " + cellGrowthCount + " CELL NUMBER " + cell.getCellNumber());

        switch (cell.getSubType()) {
        case NORMAL:
            break;

        case MAIN_R:
        case MAIN_L:
        case MAIN_C:
        case MAIN:
            /* randomly pick one or 2 cells to grow out of MAIN cell */
            number = (int)(random.nextDouble() * getMaxBranch()) + 1;

            if (cellGrowthCount == 2) {
                number = 2;
            }

            if ((cellGrowthCount > 2) && cell.getCellNumber() <= 3) {
                return;
            }

            if (cell.getLinkCellDown() == null) {
                number = 1;
            }

            for (i = 0; i < number; ++i) {
                // System.out.println("MAIN CELL " + cell.getSubType());
                if (cell.getGrowthCount() < getMaxBranch()) {

                    /* grow a new cell */

                    placeNewCell(cell, new Cell(cell, SubTypes.END, cell), i);

                    // System.out.println("SWITCH TO CELL TYPE " + cell.getSubType());
                    if ((cell.getSubType() == SubTypes.NORMAL) ||
                        (cell.getSubType() == SubTypes.INTERMEDIATE)) {
                        break;
                    }
                } else {
                    break;
                }
            }

            break;

        case END:
        case INTERMEDIATE:

            /* randomly pick one or 2 cells to grow out of END or INTERMEDIATE cell */

            number = (int)(random.nextDouble() * getMaxBranch()) + 1;

            if (cellGrowthCount == 2) {
                number = 2;
            }

            if ((cellGrowthCount > 2) && cell.getCellNumber() <= 3) {
                return;
            }

            if (cell.getLinkCellDown() == null) {
                number = 1;
            }

            if (debug)
                System.out.println("CELLGROWTHCOUNT*SKIP " +
                    (cellGrowthCount * getCellSkip()) + " ITER " + (iter - 1));

            if (((cellGrowthCount * getCellSkip()) >= iter) &&
                (cell.getSubType() == SubTypes.END)) {
                for (i = 0; i < 2; ++i) {
                    if (cell.getGrowthCount() < getMaxIntermediateBranch()) {
                        placeNewCell(cell, new Cell(cell, SubTypes.LAST, cell),
                            i);
                    } else {
                        break;
                    }
                }
            } else {
                for (i = 0; i < number; ++i) {
                    if (cell.getGrowthCount() < getMaxIntermediateBranch()) {
                        placeNewCell(cell, new Cell(cell, SubTypes.END, cell), i);
                    } else {
                        break;
                    }
                }
            }

            break;
        }
    }

    /**
     normalized vector between the coordinates of 2 cells
     **/
    private double scaleFactor(Cell c1, Cell c2) {
        return (Math.sqrt(Math.pow((c2.getCoordX() - c1.getCoordX()), 2) +
            Math.pow((c2.getCoordY() - c1.getCoordY()), 2) +
            Math.pow((c2.getCoordZ() - c1.getCoordZ()), 2)));
    }

    /**
       return the number of NORMAL cells
     **/
    public int getNormalCellTotal() {
        return (AllCells[Types.NORMAL.ordinal()].size());
    }

    /**
        return the index'th NORMAL cell 
     **/
    public Cell getNormalCell(int index) {
        return ((Cell) AllCells[Types.NORMAL.ordinal()].elementAt(index));
    }

    /**
        return the index'th metanephric cell 
     **/
    public Cell getMetanephricCell(int index) {
        return ((Cell) AllCells[Types.METANEPHRIC.ordinal()].elementAt(index));
    }

    /**
        return the index'th attractive cell 
     **/
    public Cell getAttractiveCell(int index) {
        return ((Cell) AllCells[Types.ATTRACTIVE.ordinal()].elementAt(index));
    }

    /** redo last cell list **/
    public void updateLastCellList() {
        int i;
        Cell cellTemp;
        lastCells = new Vector();

        for (i = 0; i < AllCells[Types.NORMAL.ordinal()].size(); ++i) {
            cellTemp = (Cell) AllCells[Types.NORMAL.ordinal()].elementAt(i);

            if (cellTemp.getSubType() == SubTypes.LAST) {
                lastCells.addElement(cellTemp);
            }
        }
    }

    /** return number of LAST cells */
    public int getLastCellTotal() {
        int i;

        if (lastCells != null) {
            return (lastCells.size());
        } else {
            return (0);
        }
    }

    /**
        return the index'th LAST cell 
     **/
    public Cell getLastCell(int index) {
        if (lastCells != null) {
            return ((Cell) lastCells.elementAt(index));
        } else {
            return ((Cell) null);
        }
    }

    /**
       return the number of metanephric cells
     **/
    public int getMetanephricCellTotal() {
        return (AllCells[Types.METANEPHRIC.ordinal()].size());
    }

    /**
       return the number of attractive cells
     **/
    public int getAttractiveCellTotal() {
        return (AllCells[Types.ATTRACTIVE.ordinal()].size());
    }

    /**
           set the maximum number of cells that can grow out of an intermediate cell
     **/
    public void setMaxIntermediateBranch(int branch) {
        maxIntermediateBranch = branch;
    }

    /**
       return the maximum number of cells that can grow out of an intermediate cell
     **/
    public int getMaxIntermediateBranch() {
        return (maxIntermediateBranch);
    }

    /**
       initialize metanephric cell positions randomly
     **/
    private void placeMetanephricCells() {
        int j;
        double x;
        double y;

        for (j = 0; j < NUM_METANEPHRIC_CELLS; ++j) {
            x = (iter * .7 * (-0.5 + random.nextDouble())) / getCellSkip();
            y = (iter * .7 * random.nextDouble()) / getCellSkip();
            AllCells[Types.METANEPHRIC.ordinal()].addElement(new Cell(x,
                getFraction() + y, 0., Types.METANEPHRIC, SubTypes.NORMAL));
        }
    }

    /**
       place attractive cells in an arc about the origin
     **/
    private void placeAttractiveCells() {
        double angle;
        int j;
        double x;
        double y;

        for (j = 0, angle = 30; j < NUM_ATTRACTIVE_CELLS;
            ++j, angle += (120 / NUM_ATTRACTIVE_CELLS)) {
            x = attractiveRadius * Math.cos(angle * DTR);
            y = attractiveRadius * Math.sin(angle * DTR);
            AllCells[Types.ATTRACTIVE.ordinal()].addElement(new Cell(x, y, 0.,
                Types.ATTRACTIVE, SubTypes.NORMAL));

        }
    }

    /**
       return the angle for a coordinate relative the the y axis
    **/
    public static double getCalculatedAngle(double dx, double dy) {
        double theta = 0.;

        System.out.println("DX " + dx + " DY " +dy);
        if ((dx >= 0) && (dy >= 0)) {
            if (dy != 0) {
                theta = Math.atan(dx / dy);
            } else {
                theta = Math.PI / 2.;
            }
        } else if ((dx >= 0) && (dy <= 0)) {
            if (dx != 0) {
                theta = Math.atan(dy / dx);
                theta = (90 * DTR) - theta;
            } else {
                System.out.println("GOT TO HERE");
                theta = Math.PI / 2.;
            }
        } else if ((dx < 0) && (dy <= 0)) {
            if (dx != 0) {
                theta = Math.atan(dy / dx);
                theta = (-90 * DTR) - theta;
            } else {
                theta = Math.PI / 2.;
            }
        } else if ((dx < 0) && (dy >= 0)) {
            if (dy != 0) {
                theta = Math.atan(dx / dy);
            } else {
                theta = Math.PI / 2.;
            }
        }

        return (theta);
    }

    /**  return the number of cells between a cell and one of the main branches **/

    private int getNumCellsToMain(Cell cell) {
        Vector trace;
        Cell cellTemp;
        int count = 0;
        int i;
        trace = cell.traceCellDown();

        if ((cell.getSubType() == SubTypes.MAIN_R) ||
            (cell.getSubType() == SubTypes.MAIN_L) ||
            (cell.getSubType() == SubTypes.MAIN_C) ||
            (cell.getSubType() == SubTypes.MAIN)) {
            return (0);
        }

        for (i = 0; i < trace.size(); ++i) {
            cellTemp = (Cell) trace.elementAt(i);

            if ((cellTemp.getSubType() == SubTypes.MAIN_R) ||
                (cellTemp.getSubType() == SubTypes.MAIN_L) ||
                (cell.getSubType() == SubTypes.MAIN_C) ||
                (cellTemp.getSubType() == SubTypes.MAIN)) {
                return (count);
            } else {
                ++count;
            }
        }

        return (count);
    }

    /** sort distances in increasing order between one normal cell and one attractive cell **/

    private void sortThem(Vector distVec) {
        DistNo temp1;
        DistNo temp2;
        int i;
        int j;

        for (i = 0; i < (distVec.size() - 1); ++i) {
            for (j = i + 1; j < distVec.size(); ++j) {
                temp1 = (DistNo) distVec.elementAt(i);
                temp2 = (DistNo) distVec.elementAt(j);

                if (temp2.getDist() < temp1.getDist()) {
                    distVec.remove(j);
                    distVec.insertElementAt(temp1, j);
                    distVec.remove(i);
                    distVec.insertElementAt(temp2, i);
                }
            }
        }
    }

    /**  return difference of x coordinates transformed for peridic boundary conditions **/
    public static double coordDiffX(double x1, double x2, boolean periodic,
        Boolean didTransform) {
        return (coordDiffX((x2 - x1), periodic, didTransform));
    }

    /**  return difference of x coordinates transformed for peridic boundary conditions overloaded helper function for above **/
    public static double coordDiffX(double dx, boolean periodic,
        Boolean didTransform) {
        didTransform.tf = false;
        boolean debug = false;

        if (!periodic) {
            return (dx);
        }

        double horizontal = CellSimulation.rightLimit - CellSimulation.leftLimit;
        if (debug) {
            System.out.println("RIGHT " + CellSimulation.rightLimit);
            System.out.println("LEFT " + CellSimulation.leftLimit);
            System.out.println("DX " + dx + " HORIZONTAL " + horizontal);
        }

        if (dx >= (horizontal / 2)) {
            didTransform.tf = true;
            return (dx - horizontal);
        } else if (dx < (-horizontal / 2)) {
            didTransform.tf = true;

            return (dx + horizontal);
        } else {
            return (dx);
        }
    }

    /**  return difference of y coordinates transformed for peridic boundary conditions **/
    public static double coordDiffY(double y1, double y2, boolean periodic,
        Boolean didTransform) {
        return (coordDiffY((y2 - y1), periodic, didTransform));
    }

    /**  return difference of y coordinates transformed for peridic boundary conditions overloaded helper function for above **/
    public static double coordDiffY(double dy, boolean periodic,
        Boolean didTransform) {
        didTransform.tf = false;

        if (!periodic) {
            return (dy);
        }

        double vertical = CellSimulation.upperLimit -
            CellSimulation.lowerLimit;

        if (dy >= (vertical / 2)) {
            didTransform.tf = true;

            return (dy - vertical);
        } else if (dy <= (-vertical / 2)) {
            didTransform.tf = true;
            return (dy + vertical);
        } else {
            return (dy);
        }
    }

    /**  return difference of z coordinates transformed for peridic boundary conditions **/
    public static double coordDiffZ(double z1, double z2, boolean periodic,
        Boolean didTransform) {
        return (coordDiffY((z2 - z1), periodic, didTransform));
    }

    /**  return difference of z coordinates transformed for peridic boundary conditions overloaded helper function for above **/
    public static double coordDiffZ(double dz, boolean periodic,
        Boolean didTransform) {
        didTransform.tf = false;

        if (!periodic) {
            return (dz);
        }

        if (dz != 0.) {
            System.err.println("Error, no PBC for Z coord yet");
            System.exit(0);

            return (dz);
        } else {
            return (dz);
        }
    }


    /** transform coordinates for periodic boundary conditions **/

    public static double[] periodicCoords(double[] coords, Boolean didTransform, int cellNumber) {
        boolean debug = false;
        didTransform.tf = false;

        double[] newCoords = new double[3];

        for (int i = 0; i < 3; ++i)
            newCoords[i] = coords[i];

        // if (cellNumber == 18) {
        //   debug = true;
        // }

        if (debug) {
            System.out.println("PERIODIC COORDS BEFORE " + coords[0] + " " +
                coords[1] + " " + coords[2]);
            System.out.println("PERIODIC COORDS Left " + leftLimit);
            System.out.println("PERIODIC COORDS Right " + rightLimit);
            System.out.println("PERIODIC COORDS Upper " + upperLimit);
            System.out.println("PERIODIC COORDS Lower " + lowerLimit);
        }

        if (newCoords[0] >= rightLimit) {
            newCoords[0] -= (rightLimit - leftLimit);
            didTransform.tf = true;
        } else if (newCoords[0] < leftLimit) {
            newCoords[0] += (rightLimit - leftLimit);
            didTransform.tf = true;
        }

        if (debug) {
            System.out.println("PERIODIC COORDS Y " + newCoords[1] + " " +
                upperLimit);
        }

        if (newCoords[1] >= upperLimit) {
            if (debug) {
                System.out.println("PERIODIC COORDS IN HERE");
            }

            newCoords[1] -= (upperLimit - lowerLimit);
            didTransform.tf = true;
        } else if (newCoords[1] < lowerLimit) {
            newCoords[1] += (upperLimit - lowerLimit);
            didTransform.tf = true;
        }

        if (debug) {
            System.out.println("PERIODIC COORDS AFTER " + newCoords[0] + " " +
                newCoords[1] + " " + newCoords[2]);
        }

        return (newCoords);
    }

    /** class that holds distances between cells **/
    private class DistNo {
        int i;
        double dist;

        private DistNo(int no, double d) {
            i = no;
            dist = d;
        }

        private void setDist(double d) {
            dist = d;
        }

        private double getDist() {
            return (dist);
        }

        private void setI(int no) {
            i = no;
        }

        private int getI() {
            return (i);
        }

        public String toString() {
            return (i + " " + dist);
        }
    }

    /** store a boolean variable **/
    public class Boolean {
        public boolean tf;

        public Boolean(boolean newTf) {
            tf = newTf;
        }
    }
}
