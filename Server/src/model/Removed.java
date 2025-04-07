package model;

public class Removed extends State
{
  @Override public String getStateName()
  {
    return "Removed";
  }

  @Override public String status()
  {
    return "removed";
  }
}
