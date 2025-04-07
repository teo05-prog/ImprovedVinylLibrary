package model;

public class Borrowed extends State
{
  @Override public String getStateName()
  {
    return "Borrowed";
  }

  @Override public void toReserve(Vinyl vinyl)
  {
    vinyl.setState(new BorrowedAndReserved());
  }

  @Override public void toReturn(Vinyl vinyl)
  {
    vinyl.setState(new Available());
  }

  @Override public String status()
  {
    return "This vinyl is borrowed.";
  }
}
