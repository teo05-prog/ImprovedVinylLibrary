package model;

import java.util.ArrayList;
import java.util.List;

public class ServerModelManager implements Model
{
  private VinylList vinylList;
  private List<ModelListener> listeners;
  private ServerSocketManager socketManager;

  public void initializeSocketManager()
  {
    socketManager = new ServerSocketManager(this);
    socketManager.start();
  }

  public ServerModelManager()
  {
    this.listeners = new ArrayList<>();
    this.vinylList = new VinylList();
    initializeListeners();
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
        vinyl.removeVinyl();
        notifyListenersVinylMarkedForRemoval(vinyl);
      }
    }
  }

  @Override public VinylList getVinylList()
  {
    synchronized (vinylList)
    {
      VinylList copy = new VinylList();
      copy.setVinyls(new ArrayList<>(vinylList.getVinyls()));
      return copy;
    }
  }

  @Override public void reserve(Vinyl vinyl) throws IllegalStateException
  {
    synchronized (vinyl)
    {
      vinyl.reserveVinyl();
      notifyListenersVinylUpdated(vinyl);
    }
  }

  @Override public void borrow(Vinyl vinyl) throws IllegalStateException
  {
    synchronized (vinyl)
    {
      vinyl.borrowVinyl();
      notifyListenersVinylUpdated(vinyl);
    }
  }

  @Override public void returnVinyl(Vinyl vinyl) throws IllegalStateException
  {
    synchronized (vinyl)
    {
      vinyl.returnVinyl();

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

  public Vinyl findVinyl(String title, String artist)
  {
    for (int i = 0; i < vinylList.size(); i++)
    {
      if (vinylList.get(i).getTitle().equals(title) && vinylList.get(i)
          .getArtist().equals(artist))
        return vinylList.get(i);
    }
    return null;
  }

  public void changeVinylState(int index, State newState)
  {
    Vinyl vinyl = getVinylByIndex(index);
    if (vinyl != null)
    {
      vinyl.setState(newState);

      if (socketManager != null)
      {
        socketManager.notifyVinylStateChange(vinyl);
      }

      Log.getInstance()
          .addLog("Vinyl " + index + " state changed to " + newState);
    }
  }

  public void shutdown()
  {
    if (socketManager != null)
    {
      socketManager.stop();
      socketManager = null;
    }
  }

  public void initializeListeners()
  {
    vinylList.addListener(evt -> {
      String propertyName = evt.getPropertyName();
      Vinyl vinyl = (Vinyl) evt.getNewValue();

      switch (propertyName)
      {
        case "vinylAdded":
          notifyListenersVinylAdded(vinyl);
          break;
        case "vinylRemoved":
          notifyListenersVinylRemoved((Vinyl) evt.getOldValue());
          break;
        case "vinylBorrowed":
        case "vinylReserved":
        case "vinylReturned":
          notifyListenersVinylUpdated(vinyl);
          break;
      }
    });
  }
}