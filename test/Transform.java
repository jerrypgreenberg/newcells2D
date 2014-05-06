import java.lang.Math.*;
public class Transform
{
       private static final double INC = 0.01;
       private static final double SIN_INC = Math.sin(INC);
       private static final double COS_INC = Math.cos(INC);
       private static final double SIN_MINC = Math.sin(-INC);
       private static final double COS_MINC = Math.cos(-INC);
       public static double[]  rotateZ(double angle,double[] coords)
       {
              int i;
              double[] returnCoords = new double[3];
              double[] oldReturnCoords = new double[3];
              for(i=0;i<3;++i)
                 oldReturnCoords[i] = coords[i];
              returnCoords[0] = Math.cos(angle)*oldReturnCoords[0] + Math.sin(angle)*oldReturnCoords[1];
              returnCoords[1] = -Math.sin(angle)*oldReturnCoords[0] + Math.cos(angle)*oldReturnCoords[1];
              return(returnCoords);
       }
       public static double[]  translate(double[] coords,double[] tcoords)
       {
              double[] returnCoords = new double[3];
              returnCoords[0] = coords[0] + tcoords[0];
              returnCoords[1] = coords[1] + tcoords[1];
              returnCoords[2] = coords[2] + tcoords[2];
              return(returnCoords);
       }
       public static double[]  translate(double[] coords,double x,double y,double z)
      {
              double returnCoords[] = new double[3];
              returnCoords[0] = coords[0] + x;
              returnCoords[1] = coords[1] + y;
              returnCoords[2] = coords[2] + z;
              return(returnCoords);
       }
}
