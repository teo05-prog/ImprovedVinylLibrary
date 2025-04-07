package model;

import java.io.*;
import java.net.*;
import java.util.*;

import com.google.gson.*;

public class ServerSocketManager
{
  private static final int PORT = 2077;
  private static final List<ClientHandler> clients = new ArrayList<>();
  private final ServerModelManager serverModelManager;
  private ServerSocket serverSocket;
  private boolean running = false;
  private final Gson gson = new Gson();

  public ServerSocketManager(ServerModelManager serverModelManager)
  {
    this.serverModelManager = serverModelManager;
  }

  public void start()
  {
    running = true;
    try
    {
      serverSocket = new ServerSocket(PORT);
      Log.getInstance().addLog("Socket server started on port " + PORT);
      new Thread(() -> {
        while (running)
        {
          try
          {
            Socket clientSocket = serverSocket.accept();
            Log.getInstance().addLog(
                "New client connected: " + clientSocket.getInetAddress()
                    .getHostAddress());
            ClientHandler clientHandler = new ClientHandler(clientSocket, this,
                serverModelManager);
            clients.add(clientHandler);
            new Thread(clientHandler).start();
          }
          catch (IOException e)
          {
            if (running)
            {
              Log.getInstance()
                  .addLog("Error accepting client: " + e.getMessage());
            }
          }
        }
      }).start();
    }
    catch (IOException e)
    {
      Log.getInstance().addLog("Server socket error: " + e.getMessage());
    }
  }

  public void stop()
  {
    running = false;
    try
    {
      if (serverSocket != null && !serverSocket.isClosed())
      {
        serverSocket.close();
      }
      List<ClientHandler> clientsCopy;
      synchronized (clients)
      {
        clientsCopy = new ArrayList<>(clients);
        clients.clear();
      }
      for (ClientHandler client : clientsCopy)
      {
        client.close();
      }
    }
    catch (IOException e)
    {
      Log.getInstance()
          .addLog("Error closing server socket: " + e.getMessage());
    }
  }

  public void broadcastMessage(String message)
  {
    List<ClientHandler> clientsCopy;
    synchronized (clients)
    {
      clientsCopy = new ArrayList<>(clients);
    }
    for (ClientHandler client : clientsCopy)
    {
      client.sendMessage(message);
    }
    Log.getInstance().addLog("BROADCAST: " + message);
  }

  public void notifyVinylStateChange(Vinyl vinyl)
  {
    try
    {
      JsonObject notification = new JsonObject();
      notification.addProperty("type", "STATE_CHANGE");
      notification.addProperty("title", vinyl.getTitle());
      notification.addProperty("artist", vinyl.getArtist());
      notification.addProperty("year", vinyl.getYear());

      State state = vinyl.getState();
      notification.addProperty("stateType", state.getClass().getSimpleName());
      notification.addProperty("stateName", state.getStateName());
      notification.addProperty("state", state.toString());

      notification.addProperty("markedForRemoval", vinyl.isMarkedForRemoval());

      notification.addProperty("timestamp", new DateTime().toString());

      broadcastMessage(gson.toJson(notification));
    }
    catch (Exception e)
    {
      Log.getInstance().addLog(
          "Error creating state change notification: " + e.getMessage());
    }
  }

  public void notifyVinylRemoved(Vinyl vinyl)
  {
    try
    {
      JsonObject notification = new JsonObject();
      notification.addProperty("type", "VINYL_REMOVED");
      notification.addProperty("title", vinyl.getTitle());
      notification.addProperty("artist", vinyl.getArtist());
      notification.addProperty("year", vinyl.getYear());
      notification.addProperty("timestamp", new DateTime().toString());

      broadcastMessage(gson.toJson(notification));
    }
    catch (Exception e)
    {
      Log.getInstance().addLog(
          "Error creating vinyl removed notification: " + e.getMessage());
    }
  }

  public void removeClient(ClientHandler clientHandler)
  {
    synchronized (clients)
    {
      clients.remove(clientHandler);
      Log.getInstance()
          .addLog("Client disconnected. Total clients: " + clients.size());
    }
  }

  public ServerModelManager getModelManager()
  {
    return serverModelManager;
  }

  public Gson getGson()
  {
    return gson;
  }

  public void broadcastLog(LogLine logLine)
  {
    JsonObject logMessage = new JsonObject();
    logMessage.addProperty("type", "LOG_MESSAGE");
    logMessage.addProperty("text", logLine.getText());
    logMessage.addProperty("timestamp", logLine.getTime().toString());
    broadcastMessage(gson.toJson(logMessage));
  }
}