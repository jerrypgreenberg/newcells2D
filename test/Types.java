import java.awt.*;
public enum Types
{
   NORMAL,
   METANEPHRIC,
   ATTRACTIVE;
   static int totalNORMAL = 0;
   static int totalMETANEPHRIC = 0;
   static int totalATTRACTIVE = 0;
   static void addCell(Types type)
   {
        switch(type)
        {
           case NORMAL:
               ++totalNORMAL;
               break;
           case METANEPHRIC:
               ++totalMETANEPHRIC;
               break;
           case ATTRACTIVE:
               ++totalATTRACTIVE;
               break;
           default:
               System.err.println("BAD CELL TYPE");
               System.exit(0);
        }
   }
   static void subtractCell(Types type)
   {
        switch(type)
        {
           
           case NORMAL:
               --totalNORMAL;
               break;
           case METANEPHRIC:
               --totalMETANEPHRIC;
               break;
           case ATTRACTIVE:
               --totalATTRACTIVE;
               break;
           default:
               System.err.println("BAD CELL TYPE");
               System.exit(0);
        }
   }
   static int cellTotal()
   {
       return(totalNORMAL+totalMETANEPHRIC+totalATTRACTIVE);
   }
}
