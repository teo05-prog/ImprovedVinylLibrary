package model;

public class Available extends State
{
  @Override public String getStateName()
  {
    return "Available";
  }

  @Override public String status()
  {
    return "available";
  }

  @Override public void toBorrow(Vinyl vinyl)
  {
    vinyl.setState(new Borrowed());
  }

  @Override public void toRemove(Vinyl vinyl)
  {
    vinyl.setState(new Removed());
  }

  @Override public void toReserve(Vinyl vinyl)
  {
    vinyl.setState(new Reserved());
  }
}
