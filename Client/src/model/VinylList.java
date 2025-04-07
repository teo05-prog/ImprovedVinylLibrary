package model;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class VinylList implements Serializable
{
  private ArrayList<Vinyl> vinyls;
  private PropertyChangeSupport support;

  public VinylList()
  {
    vinyls = new ArrayList<Vinyl>();
    support = new PropertyChangeSupport(this);
  }

  public int size()
  {
    return vinyls.size();
  }

  public synchronized Vinyl get(int index)
  {
    if (index < vinyls.size())
    {
      return vinyls.get(index);
    }
    else
    {
      return null;
    }
  }

  public ArrayList<Vinyl> getVinyls()
  {
    return vinyls;
  }

  public void setVinyls(ArrayList<Vinyl> vinyls)
  {
    this.vinyls = vinyls;
  }

  public synchronized void remove(Vinyl vinyl)
  {
    vinyls.remove(vinyl);
    support.firePropertyChange("vinylRemoved", vinyl, null);
  }

  public synchronized void borrowVinyl(Vinyl vinyl)
  {
    if (vinyl != null)
    {
      vinyl.borrowVinyl();
      support.firePropertyChange("vinylBorrowed", null, vinyl);
    }
  }

  public synchronized void reserveVinyl(Vinyl vinyl)
  {
    if (vinyl != null)
    {
      vinyl.reserveVinyl();
      support.firePropertyChange("vinylReserved", null, vinyl);
    }
  }

  public synchronized void returnVinyl(Vinyl vinyl)
  {
    if (vinyl != null)
    {
      vinyl.returnVinyl();
      support.firePropertyChange("vinylReturned", null, vinyl);
    }
  }

  public String toString()
  {
    String result = "";
    for (Vinyl vinyl : vinyls)
    {
      result += vinyl + "\n";
    }
    return result;
  }

  public boolean equals(Object obj)
  {
    if (obj == null || getClass() != obj.getClass())
    {
      return false;
    }
    VinylList other = (VinylList) obj;
    return vinyls.equals(other.vinyls);
  }

  public void add(Vinyl vinyl)
  {
    vinyls.add(vinyl);
    support.firePropertyChange("vinylAdded", null, vinyl);
  }

  public void clear()
  {
    vinyls.clear();
  }

  public void addListener(PropertyChangeListener listener)
  {
    support.addPropertyChangeListener(listener);
  }

  public void removeListener(PropertyChangeListener listener)
  {
    support.removePropertyChangeListener(listener);
  }

  public void addAll(ArrayList<Vinyl> vinylList)
  {
    for (Vinyl vinyl : vinylList)
    {
      add(vinyl);
    }
  }
}
