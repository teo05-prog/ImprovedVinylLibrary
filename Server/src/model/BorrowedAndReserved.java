package model;

public class BorrowedAndReserved extends State
{
  @Override public String getStateName()
  {
    return "BorrowedAndReserved";
  }

  @Override public void toReturn(Vinyl vinyl)
  {
    vinyl.setState(new Reserved());
  }

  @Override public String status()
  {
    return "This vinyl is borrowed and reserved.";
  }
}
