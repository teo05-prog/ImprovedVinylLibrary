package model;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.Serializable;
import java.util.ArrayList;

public class VinylList implements Serializable
{
  private ArrayList<Vinyl> vinyls;
  private PropertyChangeSupport support;

  public VinylList()
  {
    vinyls = new ArrayList<>();
    support = new PropertyChangeSupport(this);
    Creator();
  }

  private void Creator()
  { // Do not add to diagram

    String[] titles = new String[] {"Thriller", "Abbey - Road",
        "Greatest Hits - 2", "Purple Rain", "Master of Puppets",
        "Curtain Call: The Hits", "Back in Black", "Hotel California",
        "Led Zeppelin IV", "The Wall"};
    String[] artists = new String[] {"Michael Jackson", "Beatles", "Queen",
        "Prince", "Metallica", "Eminem", "AC/DC", "Eagles", "Led Zeppelin",
        "Pink Floyd"};
    String[] years = new String[] {"1982", "1969", "1981", "1984", "1986",
        "2005", "2003", "1976", "1971", "1979"};

    for (int i = 0; i < titles.length; i++)
    {
      Vinyl vinyl = new Vinyl(titles[i], artists[i], years[i]);
      vinyls.add(vinyl);
    }
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
    if (vinyl != null)
    {
      vinyl.removeVinyl();
      support.firePropertyChange("vinylRemoved", vinyl, null);
    }
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
}
