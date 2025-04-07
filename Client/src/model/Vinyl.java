package model;

import java.io.Serializable;

public class Vinyl implements Serializable
{
  private String title;
  private String artist;
  private String year;
  private State state;
  private boolean markedForRemoval;

  public Vinyl(String title, String artist, String year)
  {
    this.title = title;
    this.artist = artist;
    this.year = year;
    this.state = new Available();
    this.markedForRemoval = false;
    setState(new Available());
  }

  public String getTitle()
  {
    return title;
  }

  public String getArtist()
  {
    return artist;
  }

  public String getYear()
  {
    return year;
  }

  public State getState()
  {
    return state;
  }

  public boolean isMarkedForRemoval()
  {
    return markedForRemoval;
  }

  public void markForRemoval()
  {
    this.markedForRemoval = true;
  }

  public void setState(State state)
  {
    if (!markedForRemoval)
    {
      this.state = state;
    }
    else
    {
      System.out.println("Cannot change state: vinyl is removed");
    }
  }

  public synchronized void borrowVinyl()
  {
    state.toBorrow(this);
  }

  public synchronized void reserveVinyl()
  {
    String oldState = state.getStateName();
    state.toReserve(this);
  }

  public synchronized void returnVinyl()
  {
    String oldState = state.getStateName();
    state.toReturn(this);
  }

  public synchronized void removeVinyl()
  {
    String oldState = state.getStateName();
    state.toRemove(this);
  }

  @Override public String toString()
  {
    return title + ", " + artist + ", " + year + ", " + state;
  }
}
