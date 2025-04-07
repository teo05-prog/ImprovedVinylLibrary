package model;

public abstract class State
{
  public abstract String getStateName();

  public void toReturn(Vinyl vinyl)
  {
    //Do nothing
  }

  public void toReserve(Vinyl vinyl)
  {
    //Do nothing
  }

  public void toRemove(Vinyl vinyl)
  {
    //Do nothing
  }

  public void toBorrow(Vinyl vinyl)
  {
    //Do nothing
  }

  public abstract String status();

  @Override public String toString()
  {
    return getStateName();
  }
}
