import java.util.*;
/** types of normal cells **/
enum SubTypes {
    END,
    INTERMEDIATE,
    MAIN_R,
    MAIN_L,
    MAIN_C,
    MAIN,
    NORMAL,
    LAST;
}
/** flags for cells crossing periodic boundary **/
enum PeriodicType {
    NORMAL,
    CROSSED;
}
/** flags for metanephric cells  **/
enum AddedType {
    NORMAL,
    ADDED;
}
/** Cell class describing a single cell **/
public class Cell {
    /** maximum number of cells **/
    private static final int MAX_BRANCH = 1000;
    /** Types enum variable **/
    private Types type;
    /** SubTypes enum variable **/
    private SubTypes subType;
    /** 3d coord array **/
    private double coords[] = new double[3];
    /** cell that the current cell has "grown" from **/
    private Cell linkCellDown = null;
   /** cells grown after this one that are linked to it  (currently not used) **/
    private Cell[] linkCellUp = new Cell[MAX_BRANCH];
    /** number assigned to this cell **/
    private int cellNumber;
    /** number of links to child cells **/
    private int upLinks = 0;
    /** number of cells that have "grown" from this cell   **/
    private int growthCount = 0;
    /** total number of cells **/
    static int cellCount = 0;
    /** cell this cell is bound to (if this is a metanephric cell **/
    private Cell boundCell = null;
    private int nBoundCell = 0;
    private Cell normalBoundCell = null;
    /** metaneprhic cells bound to this cell **/   
    private Vector attractCells = new Vector(0);
    /** angles between cell and child cells (only 1 element used )**/
    private Vector angles = new Vector(0);
    /** PeriodicType enum variable **/
    private PeriodicType pType;
    /** AddedType enum variable **/
    private AddedType aType;
    /**  docked metanephric cell coordinates **/
    private double[] dock = {
        0., 0., 0.
    };
    private double mAngle = 0;
    Cell testCell=null;

    public Cell() {
        setCoords(0., 0., 0.);
        setType(Types.NORMAL);
        setSubType(SubTypes.NORMAL);
        setCellCount(getCellCount() + 1);
        setCellNumber(getCellCount());
    }
    public Cell(double coor[]) {
        setCoords(coor);
        setSubType(SubTypes.NORMAL);
        setType(Types.NORMAL);
        setCellCount(getCellCount() + 1);
        setCellNumber(getCellCount());
        setPeriodicType(PeriodicType.NORMAL);
        setAddedType(AddedType.NORMAL);
        // System.out.println("CELL NUMBER " + getCellNumber() + " CELL TYPE " + getType() + " CELL SUBTYPE " + getSubType());
    }
    /** this constructor is used **/
    public Cell(double x, double y, double z, SubTypes stype) {
        setCoords(x, y, z);
        setType(Types.NORMAL);
        setSubType(stype);
        setCellCount(getCellCount() + 1);
        setCellNumber(getCellCount());
        setPeriodicType(PeriodicType.NORMAL);
        setAddedType(AddedType.NORMAL);
        // System.out.println("CELL NUMBER " + getCellNumber() + " CELL TYPE " + getType() + " CELL SUBTYPE " + getSubType());
    }
    public Cell(double coor[], Cell down, Cell[] up) {
        setCoords(coor);
        setLinkCellUp(up);
        setLinkCellDown(down);
        setSubType(SubTypes.NORMAL);
        setType(Types.NORMAL);
        setCellCount(getCellCount() + 1);
        setCellNumber(getCellCount());
        setPeriodicType(PeriodicType.NORMAL);
        setAddedType(AddedType.NORMAL);
        // System.out.println("CELL NUMBER " + getCellNumber() + " CELL TYPE " + getType() + " CELL SUBTYPE " + getSubType());
    }
    public Cell(double coor[], SubTypes stype, Cell down, Cell[] up) {
        setCoords(coor);
        setLinkCellUp(up);
        setLinkCellDown(down);
        setSubType(stype);
        setType(Types.NORMAL);
        setCellCount(getCellCount() + 1);
        setCellNumber(getCellCount());
        setPeriodicType(PeriodicType.NORMAL);
        setAddedType(AddedType.NORMAL);
        // System.out.println("CELL NUMBER " + getCellNumber() + " CELL TYPE " + getType() + " CELL SUBTYPE " + getSubType());
    }
    /** this constuctor is used **/
    public Cell(double x, double y, double z, Types type, SubTypes stype) {
        setCoords(x, y, z);
        setType(type);
        setSubType(stype);
        setCellCount(getCellCount() + 1);
        setCellNumber(getCellCount());
        setPeriodicType(PeriodicType.NORMAL);
        // System.out.println("CELL NUMBER " + getCellNumber() + " CELL TYPE " + getType() + " CELL SUBTYPE " + getSubType());
    }
    public Cell(double coor[], Cell down, Cell[] up, Types type, SubTypes stype) {
        setCoords(coor);
        setLinkCellUp(up);
        setLinkCellDown(down);
        setType(type);
        setSubType(stype);
        setCellCount(getCellCount() + 1);
        setCellNumber(getCellCount());
        setPeriodicType(PeriodicType.NORMAL);
        setAddedType(AddedType.NORMAL);
        // System.out.println("CELL NUMBER " + getCellNumber() + " CELL TYPE " + getType() + " CELL SUBTYPE " + getSubType());
    }
    public Cell(Cell cell, SubTypes stype) {
        if (cell == null) {
            System.err.println("error, no Cell object");
            System.exit(0);
        }
        setCoords(cell.getCoords());
        setType(cell.getType());
        setSubType(stype);
        setCellCount(getCellCount() + 1);
        setCellNumber(getCellCount());
        // System.out.println("CELL NUMBER " + getCellNumber() + " CELL TYPE " + getType() + " CELL SUBTYPE " + getSubType());
    }
    /** this constructor is used **/
    public Cell(Cell cell, SubTypes stype, Cell down) {
        if (cell == null) {
            System.err.println("error, no Cell object");
            System.exit(0);
        }
        setCoords(cell.getCoords());
        setLinkCellDown(down);
        setType(cell.getType());
        setSubType(stype);
        setCellCount(getCellCount() + 1);
        setCellNumber(getCellCount());
        // System.out.println("CELL NUMBER " + getCellNumber() + " CELL TYPE " + getType() + " CELL SUBTYPE " + getSubType());
    }
    /** set cell coordinates given an array **/
    public void setCoords(double[] coor) {
        int i;
        for (i = 0; i < 3; ++i)
            coords[i] = coor[i];
    }
    /** return cell coordinates as an array **/
    public double[] getCoords() {
        return (coords);
    }
    /** copy coordinates to an array **/
    public double[] copyCoords() {
        double[] copyCoords = new double[3];
        copyCoords[0] = getCoordX();
        copyCoords[1] = getCoordY();
        copyCoords[2] = getCoordZ();
        return (copyCoords);
    }
    
    /** set cell coordinates given x,y,z **/
    public void setCoords(double coorX, double coorY, double coorZ) {
        coords[0] = coorX;
        coords[1] = coorY;
        coords[2] = coorZ;
    }
    /** return the x coordinate **/
    public double getCoordX() {
        return (coords[0]);
    }
    /** return the y coordinate **/
    public double getCoordY() {
        return (coords[1]);
    }
    /** return the z coordinate **/
    public double getCoordZ() {
        return (coords[2]);
    }
    /** set the x coordinate **/
    public void setCoordX(double x) {
        coords[0] = x;
    }
    /** set the y coordinate **/
    public void setCoordY(double y) {
        coords[1] = y;
    }
    /** set the z coordinate **/
    public void setCoordZ(double z) {
        coords[2] = z;
    }
    /** set up the array of cells that are children of this cell **/
    public void setLinkCellUp(Cell[] up) {
        linkCellUp = up;
    }
    /** add a cell to the list of cells that are children of this cell **/
    public void setLinkCellUp(Cell up) {
        try {
            if (upLinks < MAX_BRANCH)
                linkCellUp[upLinks++] = up;
            else
                throw new BranchException();
        } catch (BranchException e) {
            System.out.println(e.getMessage());
            System.exit(-1);
        }
    }
    /** set the cell that is the parent of this cell **/
    public void setLinkCellDown(Cell down) {
        linkCellDown = down;
    }
    /** return the parent cell of this cell **/
    public Cell getLinkCellDown() {
        return (linkCellDown);
    }
    /** return the list of children of this cell **/
    public Cell[] getLinkCellUp() {
        return (linkCellUp);
    }
    /** set the main type of this cell **/
    public void setType(Types ttype) {
        type = ttype;
    }
    /** return the  main type of this cell **/
    public Types getType() {
        return (type);
    }
    /** set the number of children of this cell **/
    public void setUpLinks(int inLink) {
        upLinks = inLink;
    }
    /** get the number of children of this cell **/
    public int getUpLinks() {
        return (upLinks);
    }
    /** set the sub type of this cell **/
    public void setSubType(SubTypes tsubType) {
        subType = tsubType;
    }
    /** get the sub type of this cell **/
    public SubTypes getSubType() {
        return (subType);
    }
    /** set the number of this cell **/
    public static void setCellCount(int count) {
        cellCount = count;
    }
    /** get the cell number for this cell **/
    public static int getCellCount() {
        return (cellCount);
    }
    /** set how many cells have grown from this cell **/
    public void setGrowthCount(int growth) {
        growthCount = growth;
    }
    /** get how many cells have grown from this cell **/
    public int getGrowthCount() {
        return (growthCount);
    }
    /** return a list of cells that trace the lineage of this cell **/
    public Vector traceCellDown() {
        Cell currentCell;
        Vector cellList = new Vector(0);
        currentCell = this;
        cellList.addElement(this);
        while (currentCell.linkCellDown != null) {
            cellList.addElement(currentCell.linkCellDown);
            currentCell = currentCell.linkCellDown;
        }
        return (cellList);
    }
    /** return the number of cells that are the ancestors of this cell  **/
    public int traceCellDownNum() {
        Cell currentCell;
        int cCount = 1;
        currentCell = this;
        while (currentCell.linkCellDown != null) {
            currentCell = currentCell.linkCellDown;
            ++cCount;
        }
        return (cCount);
    }
    /** set the cell number of this cell **/
    public void setCellNumber(int cNumber) {
        cellNumber = cNumber;
    }
    /** get the cell number of this cell **/
    public int getCellNumber() {
        return (cellNumber);
    }
    /** return instance variables **/
    public String toString() {
        String returnString;
        returnString = "Cell # " + getCellNumber() + " " + getType() + " " + getSubType() + " Coords " + getCoordX() + " " + getCoordY() + " " + getCoordZ();
        if (getLinkCellDown() != null)
            returnString += " CellDown " + getLinkCellDown().getCellNumber();
        Cell[] cells = getLinkCellUp();
        for (int i = 0; i < cells.length; ++i) {
            if (cells[i] != null)
                returnString += " CellUp " + cells[i].getCellNumber();
        }
        returnString += " Cell Branch " + getGrowthCount();
        return (returnString);
    }
    /** set an angle for a child cell **/
    public void setAngle(double a)
    {
         angles.addElement(new Double(a));
    } 
    /** get ith angle for a child cell **/
    public double getAngle(int i)
    {
         Double tempDouble = (Double) angles.elementAt(i);
         return(tempDouble.doubleValue());
    } 
    /** return the number of angles in the angle Vector **/
    public int getAngleSize()
    {
         return(angles.size());
    }
    /** add a cell to the attractive cell list **/
    public void addAttractCells(Cell addCell) {
        attractCells.addElement(addCell);
    }
    /** return the last attractive cell in the list **/
    public Cell getLastAttractCell() {
        if (getNumberOfAttractCells() != 0)
            return ((Cell) attractCells.get(attractCells.size() - 1));
        else
            return ((Cell) null);
    }
    /** get the ith attractive cell **/
    public Cell getAttractCell(int i) {
        return ((Cell) attractCells.get(i));
    }
    /** return the number of attractive cells **/
    public int getNumberOfAttractCells() {
        return (attractCells.size());
    }
    /** bind this cell **/
    public void setBoundCell(Cell bCell) {
        boundCell = bCell;
        ++nBoundCell;
    }
    /** return the cell this cell is bound to **/
    public Cell getBoundCell() {
        return (boundCell);
    }
    public void setNormalBoundCell(Cell bCell) {
        normalBoundCell = bCell;
    }
    /** return the cell this cell is bound to **/
    public Cell getNormalBoundCell() {
        return (normalBoundCell);
    }
    /**  set the periodic type of this cell **/
    public void setPeriodicType(PeriodicType newpType) {
        pType = newpType;
    }
    /**  return the periodic type of this cell **/
    public PeriodicType getPeriodicType() {
        return (pType);
    }
    /**  set the added type of this cell **/
    public void setAddedType(AddedType newaType) {
        aType = newaType;
    }
    /**  return the added type of this cell **/
    public AddedType getAddedType() {
        return (aType);
    }
    /** set the docked coordinates  given a coordinate set **/
    public void setDock(double x, double y, double z) {
        dock[0] = x;
        dock[1] = y;
        dock[2] = z;
    }
    /** set the docked coordinates  given an array **/
    public void setDock(double[] xyz) {
        dock[0] = xyz[0];
        dock[1] = xyz[1];
        dock[2] = xyz[2];
    }
    /** return the docked coordinates **/
    public double[] getDock() {
        return (dock);
    }
    /** get a dock coordinate **/
    public double getDock(int i) {
        if (i <= 2 && i >= 0)
            return (dock[i]);
        else {
            System.err.println("Bad dock reference");
            System.exit(-1);
            return (-1.);
        }
    }
   public double getMangle()
   {
       return(mAngle);
   }
   public void setMangle(double inAngle)
   {
        mAngle = inAngle;
   }
   Cell getTestCell()
   {
      return(testCell);
   }
   void setTestCell(Cell tCell)
   {
      testCell = tCell;
   }
}
