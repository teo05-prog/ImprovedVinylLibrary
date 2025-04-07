package model;

import java.util.ArrayList;
import java.util.List;

public class ClientModelManager implements Model
{
  private VinylList vinylList;
  private List<ModelListener> listeners;
  private ClientSocketManager clientSocketManager;

  public void initializeSocketManager()
  {
    clientSocketManager = new ClientSocketManager(this);

    clientSocketManager.addUpdateListener(
        message -> System.out.println("Socket update: " + message));

    if (clientSocketManager.connect())
    {
      clientSocketManager.requestAllVinyls();
    }
  }

  public ClientModelManager()
  {
    this.vinylList = new VinylList();
    this.listeners = new ArrayList<>();
  }

  public ClientSocketManager getClientSocketManager()
  {
    return clientSocketManager;
  }

  @Override public void addListener(ModelListener listener)
  {
    listeners.add(listener);
  }

  @Override public void removeListener(ModelListener listener)
  {
    listeners.remove(listener);
  }

  private void notifyListenersVinylAdded(Vinyl vinyl)
  {
    for (ModelListener listener : listeners)
    {
      listener.vinylAdded(vinyl);
    }
  }

  private void notifyListenersVinylRemoved(Vinyl vinyl)
  {
    for (ModelListener listener : listeners)
    {
      listener.vinylRemoved(vinyl);
    }
  }

  private void notifyListenersVinylUpdated(Vinyl vinyl)
  {
    for (ModelListener listener : listeners)
    {
      listener.vinylUpdated(vinyl);
    }
  }

  private void notifyListenersVinylMarkedForRemoval(Vinyl vinyl)
  {
    for (ModelListener listener : listeners)
    {
      listener.vinylMarkedForRemoval(vinyl);
    }
  }

  @Override public void addVinyl(String title, String artist, String year)
  {
    Vinyl vinyl = new Vinyl(title, artist, year);
    synchronized (vinylList)
    {
      vinylList.add(vinyl);
    }
    notifyListenersVinylAdded(vinyl);
  }

  @Override public void removeVinyl(Vinyl vinyl)
  {
    synchronized (vinylList)
    {
      if (vinyl.getState() instanceof Available)
      {
        vinylList.remove(vinyl);
        notifyListenersVinylRemoved(vinyl);
      }
      else
      {
        vinyl.markForRemoval();
        notifyListenersVinylMarkedForRemoval(vinyl);
      }
    }
  }

  public void setVinylList(VinylList newVinylList)
  {
    ArrayList<Vinyl> newList = newVinylList.getVinyls();

    synchronized (vinylList)
    {
      vinylList.clear();
      vinylList.addAll(newList);
    }

    for (Vinyl vinyl : newList)
    {
      notifyListenersVinylAdded(vinyl);
    }
  }

  @Override public VinylList getVinylList()
  {
    synchronized (vinylList)
    {
      return vinylList;
    }
  }

  @Override public void reserve(Vinyl vinyl) throws IllegalStateException
  {
    synchronized (vinyl)
    {
      vinyl.reserveVinyl();
      notifyListenersVinylUpdated(vinyl);

      clientSocketManager.reserveVinyl(vinyl.getTitle(), vinyl.getArtist());
    }
  }

  @Override public void borrow(Vinyl vinyl) throws IllegalStateException
  {
    synchronized (vinyl)
    {
      vinyl.borrowVinyl();
      notifyListenersVinylUpdated(vinyl);

      clientSocketManager.borrowVinyl(vinyl.getTitle(), vinyl.getArtist());
    }
  }

  @Override public void returnVinyl(Vinyl vinyl) throws IllegalStateException
  {
    synchronized (vinyl)
    {
      vinyl.returnVinyl();
      clientSocketManager.returnVinyl(vinyl.getTitle(), vinyl.getArtist());
      if (vinyl.isMarkedForRemoval() && (vinyl.getState() instanceof Available))
      {
        vinylList.remove(vinyl);
        notifyListenersVinylRemoved(vinyl);
      }
      else
      {
        notifyListenersVinylUpdated(vinyl);
      }
    }
  }

  @Override public Vinyl getVinylByIndex(int index)
  {
    synchronized (vinylList)
    {
      if (index >= 0 && index < vinylList.size())
      {
        return vinylList.get(index);
      }
      return null;
    }
  }

  public Vinyl getVinylByTitle(String title)
  {
    for (int i = 0; i < vinylList.size(); i++)
    {
      if (vinylList.get(i).getTitle().equals(title))
        return vinylList.get(i);
    }
    return null;
  }

  public void shutdown()
  {
    if (clientSocketManager != null)
    {
      clientSocketManager.disconnect();
    }
  }

  public Vinyl findVinyl(String title, String artist)
  {
    synchronized (vinylList)
    {
      for (Vinyl vinyl : vinylList.getVinyls())
      {
        if (vinyl.getTitle().equals(title) && vinyl.getArtist().equals(artist))
        {
          return vinyl;
        }
      }
    }
    return null;
  }

  public void handleVinylStateUpdate(String title, String artist,
      String stateType)
  {
    synchronized (vinylList)
    {
      Vinyl vinyl = findVinyl(title, artist);
      if (vinyl != null)
      {
        try
        {
          switch (stateType)
          {
            case "BORROW":
              vinyl.borrowVinyl();
              break;
            case "RESERVE":
              vinyl.reserveVinyl();
              break;
            case "RETURN":
              vinyl.returnVinyl();
              break;
          }
          notifyListenersVinylUpdated(vinyl);
        }
        catch (IllegalStateException e)
        {
          System.err.println("Cannot update vinyl state: " + e.getMessage());
        }
      }
    }
  }
}
