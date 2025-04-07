package model;

import com.google.gson.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class ClientSocketManager
{
  private static final String HOST = "localhost";
  private static final int PORT = 2077;

  private Socket socket;
  private BufferedReader in;
  private PrintWriter out;

  private boolean connected = false;
  private final ClientModelManager clientModelManager;
  private final Gson gson = new Gson();

  private final List<VinylUpdateListener> updateListeners = new ArrayList<>();

  public ClientSocketManager(ClientModelManager clientModelManager)
  {
    this.clientModelManager = clientModelManager;
  }

  public boolean connect()
  {
    try
    {
      System.out.println(
          "Attempting to connect to server at " + HOST + ":" + PORT);
      socket = new Socket(HOST, PORT);
      out = new PrintWriter(socket.getOutputStream(), true);
      in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
      connected = true;

      System.out.println("Connected to server at " + HOST + ":" + PORT);

      Thread listenerThread = new Thread(this::listenForServerMessages);
      listenerThread.setDaemon(true);
      listenerThread.start();

      Thread.sleep(500);

      return true;
    }
    catch (IOException e)
    {
      System.err.println("Failed to connect to server: " + e.getMessage());
      return false;
    }
    catch (InterruptedException e)
    {
      System.err.println("Connection interrupted: " + e.getMessage());
      return false;
    }
  }

  public void disconnect()
  {
    connected = false;
    try
    {
      if (socket != null && !socket.isClosed())
      {
        socket.close();
      }
      System.out.println("Disconnected from server");
    }
    catch (IOException e)
    {
      System.err.println("Error while disconnecting: " + e.getMessage());
    }
  }

  private void listenForServerMessages()
  {
    try
    {
      String message;
      while (connected && (message = in.readLine()) != null)
      {
        System.out.println("Received from server: " + message);
        processServerMessage(message);
      }
    }
    catch (IOException e)
    {
      if (connected)
      {
        System.err.println("Connection to server lost: " + e.getMessage());
        disconnect();
      }
    }
  }

  private void processServerMessage(String jsonMessage)
  {
    try
    {
      JsonObject message = gson.fromJson(jsonMessage, JsonObject.class);
      String messageType = message.get("type").getAsString();

      System.out.println("Processing message of type: " + messageType);

      switch (messageType)
      {
        case "CONNECTION_SUCCESS":
          System.out.println(
              "Connection confirmed by server: " + message.get("message")
                  .getAsString());
          break;
        case "VINYL_LIST":
          try
          {
            if (message.has("vinyls") && !message.get("vinyls").isJsonNull())
            {
              updateVinylList(message.get("vinyls").getAsJsonArray());
            }
            else
            {
              System.err.println(
                  "Vinyl list is empty or missing in the response");
            }
          }
          catch (ClassCastException e)
          {
            System.err.println(
                "Caught ClassCastException. Using direct approach instead.");
            handleVinylListDirectly(message.get("vinyls").getAsJsonArray());
          }
          catch (Exception e)
          {
            System.err.println("Server error: " + e.getMessage());
            e.printStackTrace();
          }
          break;
        case "SUCCESS":
          System.out.println(
              "Success response: " + message.get("message").getAsString());
          notifyUpdateListeners(
              "Success: " + message.get("message").getAsString());
          break;
        case "ERROR":
          System.err.println(
              "Error response: " + message.get("message").getAsString());
          notifyUpdateListeners(
              "Error: " + message.get("message").getAsString());
          break;
        case "STATE_CHANGE":
          System.out.println("State change received: " + jsonMessage);
          String title = message.has("title") ?
              message.get("title").getAsString() :
              "unknown";
          notifyUpdateListeners("Vinyl state changed: " + title);
          break;
        default:
          System.out.println("Unhandled message type: " + messageType);
      }
    }
    catch (Exception e)
    {
      System.err.println("Error parsing server message: " + e.getMessage());
      e.printStackTrace();
    }
  }

  private void handleVinylListDirectly(JsonArray vinylsArray)
  {
    try
    {
      System.out.println(
          "Handling vinyl list directly with " + vinylsArray.size() + " items");
      VinylList existingList = clientModelManager.getVinylList();
      synchronized (existingList)
      {
        existingList.clear();

        for (JsonElement element : vinylsArray)
        {
          JsonObject vinylJson = element.getAsJsonObject();
          String title = vinylJson.get("title").getAsString();
          String artist = vinylJson.get("artist").getAsString();
          String year = vinylJson.get("year").getAsString();

          Vinyl vinyl = new Vinyl(title, artist, year);

          String stateType = vinylJson.has("stateType") ?
              vinylJson.get("stateType").getAsString() :
              "Available";

          State state;
          switch (stateType)
          {
            case "Available":
              state = new Available();
              break;
            case "Reserved":
              state = new Reserved();
              break;
            case "Borrowed":
              state = new Borrowed();
              break;
            case "BorrowedAndReserved":
              state = new BorrowedAndReserved();
              break;
            default:
              state = new Available();
              System.out.println("Unknown state type: " + stateType
                  + ", defaulting to Available");
          }

          vinyl.setState(state);

          if (vinylJson.has("markedForRemoval") && vinylJson.get(
              "markedForRemoval").getAsBoolean())
          {
            vinyl.markForRemoval();
          }

          existingList.add(vinyl);
        }
      }
      System.out.println(
          "Updated vinyl list with " + existingList.size() + " items");
      notifyUpdateListeners(
          "Vinyl list updated with " + existingList.size() + " items");
    }
    catch (Exception e)
    {
      System.err.println("Error in direct vinyl handling: " + e.getMessage());
      e.printStackTrace();
    }
  }

  private void updateVinylList(JsonArray vinylsArray)
  {
    try
    {
      VinylList newVinylList = ServerInterface.convertJsonToVinylList(
          vinylsArray);

      clientModelManager.setVinylList(newVinylList);
      System.out.println("Updated vinyl list through ServerInterface");
      notifyUpdateListeners("Vinyl list updated");
    }
    catch (Exception e)
    {
      System.err.println("Error updating vinyl list: " + e.getMessage());
      e.printStackTrace();
      notifyUpdateListeners("Error: " + e.getMessage());
    }
  }

  private void sendRequest(JsonObject request)
  {
    if (!connected)
    {
      System.err.println("Not connected to server");
      return;
    }

    String jsonRequest = gson.toJson(request);
    System.out.println("Sending to server: " + jsonRequest);
    out.println(jsonRequest);
    out.flush();
  }

  public void borrowVinyl(String title, String artist)
  {
    if (!connected)
    {
      System.err.println("Cannot borrow vinyl: Not connected to server");
      return;
    }
    else
    {
      System.out.println("Borrowing vinyl: " + title + " by " + artist);
    }

    JsonObject request = new JsonObject();
    request.addProperty("type", "BORROW_VINYL");
    request.addProperty("title", title);
    request.addProperty("artist", artist);
    sendRequest(request);
  }

  public void reserveVinyl(String title, String artist)
  {
    if (!connected)
    {
      System.err.println("Cannot reserve vinyl: Not connected to server");
      return;
    }
    else
    {
      System.out.println("Reserving vinyl: " + title + " by " + artist);
    }

    JsonObject request = new JsonObject();
    request.addProperty("type", "RESERVE_VINYL");
    request.addProperty("title", title);
    request.addProperty("artist", artist);
    sendRequest(request);
  }

  public void returnVinyl(String title, String artist)
  {
    if (!connected)
    {
      System.err.println("Cannot return vinyl: Not connected to server");
      return;
    }
    else
    {
      System.out.println("Returning vinyl: " + title + " by " + artist);
    }

    JsonObject request = new JsonObject();
    request.addProperty("type", "RETURN_VINYL");
    request.addProperty("title", title);
    request.addProperty("artist", artist);
    sendRequest(request);
  }

  public void requestAllVinyls()
  {
    JsonObject request = new JsonObject();
    request.addProperty("type", "GET_ALL_VINYLS");
    sendRequest(request);
  }

  public interface VinylUpdateListener
  {
    void onVinylUpdate(String message);
  }

  // Add a listener
  public void addUpdateListener(VinylUpdateListener listener)
  {
    updateListeners.add(listener);
  }

  // Remove a listener
  public void removeUpdateListener(VinylUpdateListener listener)
  {
    updateListeners.remove(listener);
  }

  // Notify all listeners
  private void notifyUpdateListeners(String message)
  {
    System.out.println("Notifying listeners: " + message);
    for (VinylUpdateListener listener : updateListeners)
    {
      listener.onVinylUpdate(message);
    }
  }
}