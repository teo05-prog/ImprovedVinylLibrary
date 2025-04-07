package model;

public class Reserved extends State
{
  @Override public String getStateName()
  {
    return "Reserved";
  }

  @Override public void toBorrow(Vinyl vinyl)
  {
    vinyl.setState(new BorrowedAndReserved());
  }

  @Override public void toReturn(Vinyl vinyl)
  {
    vinyl.setState(new Available());
  }

  @Override public String status()
  {
    return "This vinyl is reserved.";
  }
}
