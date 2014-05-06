public class BranchException extends  Exception
{
      public BranchException()
      {
          super("Branch Error");
      }
      public BranchException(String message)
      {
          super(message);
      }
}
