package model;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.ArrayList;

public class Log
{
  private ArrayList<LogLine> logs;
  private static Log log;
  private static Object lock = new Object();
  private static ServerSocketManager serverSocketManager;
  private PropertyChangeSupport support;

  private Log()
  {
    this.logs = new ArrayList<>();
    this.support = new PropertyChangeSupport(this);
  }

  public static void setServerSocketManager(ServerSocketManager manager)
  {
    serverSocketManager = manager;
  }

  public static Log getInstance()
  {
    if (log == null)
    {
      synchronized (lock)
      {
        if (log == null)
        {
          log = new Log();
        }
      }
    }
    return log;
  }

  public void addListener(PropertyChangeListener listener)
  {
    support.addPropertyChangeListener(listener);
  }

  public void removeListener(PropertyChangeListener listener)
  {
    support.removePropertyChangeListener(listener);
  }

  public void addLog(String text)
  {
    LogLine temp = new LogLine(text);
    logs.add(temp);

    support.firePropertyChange("newLogEntry", null, temp);

    if (serverSocketManager != null && !text.startsWith("BROADCAST_LOG:"))
    {
      serverSocketManager.broadcastLog(temp);
    }
  }

  public ArrayList<LogLine> getAll()
  {
    synchronized (logs)
    {
      return new ArrayList<>(logs);
    }
  }

  public LogLine getLatest()
  {
    synchronized (logs)
    {
      if (logs.isEmpty())
      {
        return null;
      }
      return logs.get(logs.size() - 1);
    }
  }

  @Override public String toString()
  {
    return "Log{" + "logs = " + logs + '}';
  }
}