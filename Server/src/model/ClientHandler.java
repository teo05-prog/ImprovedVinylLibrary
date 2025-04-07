package model;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonArray;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class ClientHandler implements Runnable
{
  private final Socket clientSocket;
  private final ServerSocketManager serverSocketManager;
  private ServerModelManager serverModelManager;

  private BufferedReader in;
  private PrintWriter out;
  private boolean running = true;

  private final Gson gson;

  public ClientHandler(Socket socket, ServerSocketManager serverSocketManager,
      ServerModelManager serverModelManager)
  {
    this.clientSocket = socket;
    this.serverSocketManager = serverSocketManager;
    this.gson = serverSocketManager.getGson();
    this.serverModelManager = serverModelManager;
    try
    {
      this.out = new PrintWriter(clientSocket.getOutputStream(), true);
      this.in = new BufferedReader(
          new InputStreamReader(clientSocket.getInputStream()));
    }
    catch (IOException e)
    {
      Log.getInstance()
          .addLog("Error setting up client handler: " + e.getMessage());
    }
  }

  @Override public void run()
  {
    try
    {
      JsonObject welcome = new JsonObject();
      welcome.addProperty("type", "CONNECTION_SUCCESS");
      welcome.addProperty("message", "Connected to Vinyl Server");
      sendMessage(gson.toJson(welcome));

      String inputLine;
      while (running && (inputLine = in.readLine()) != null)
      {
        Log.getInstance().addLog("Received client message: " + inputLine);
        processClientMessage(inputLine);
      }
    }
    catch (IOException e)
    {
      if (running)
      {
        Log.getInstance().addLog("Error handling client: " + e.getMessage());
      }
    }
    finally
    {
      close();
      serverSocketManager.removeClient(this);
    }
  }

  public void close()
  {
    running = false;
    try
    {
      if (clientSocket != null && !clientSocket.isClosed())
      {
        clientSocket.close();
      }
    }
    catch (IOException e)
    {
      Log.getInstance()
          .addLog("Error closing client socket: " + e.getMessage());
    }
  }

  private void processClientMessage(String message)
  {
    try
    {
      JsonObject jsonMessage = gson.fromJson(message, JsonObject.class);
      String messageType = jsonMessage.get("type").getAsString();

      Log.getInstance().addLog("Processing message type: " + messageType);

      switch (messageType)
      {
        case "BORROW_VINYL":
          String borrowTitle = jsonMessage.get("title").getAsString();
          String borrowArtist = jsonMessage.has("artist") ?
              jsonMessage.get("artist").getAsString() :
              "";

          Vinyl borrowVinyl = serverSocketManager.getModelManager()
              .findVinyl(borrowTitle, borrowArtist);
          if (borrowVinyl != null)
          {
            serverModelManager.borrow(borrowVinyl);
            sendSuccessResponse("Vinyl borrowed successfully");
            serverSocketManager.notifyVinylStateChange(borrowVinyl);
          }
          else
          {
            sendErrorResponse("Vinyl not found");
          }
          break;

        case "RESERVE_VINYL":
          String reserveTitle = jsonMessage.get("title").getAsString();
          String reserveArtist = jsonMessage.has("artist") ?
              jsonMessage.get("artist").getAsString() :
              "";

          Vinyl reserveVinyl = serverSocketManager.getModelManager()
              .findVinyl(reserveTitle, reserveArtist);
          if (reserveVinyl != null)
          {
            serverModelManager.reserve(reserveVinyl);
            sendSuccessResponse("Vinyl reserved successfully");
            serverSocketManager.notifyVinylStateChange(reserveVinyl);
          }
          else
          {
            sendErrorResponse("Vinyl not found");
          }
          break;

        case "RETURN_VINYL":
          String returnTitle = jsonMessage.get("title").getAsString();
          String returnArtist = jsonMessage.has("artist") ?
              jsonMessage.get("artist").getAsString() :
              "";

          Vinyl returnVinyl = serverSocketManager.getModelManager()
              .findVinyl(returnTitle, returnArtist);
          if (returnVinyl != null)
          {
            serverModelManager.returnVinyl(returnVinyl);
            sendSuccessResponse("Vinyl returned successfully");
            serverSocketManager.notifyVinylStateChange(returnVinyl);
          }
          else
          {
            sendErrorResponse("Vinyl not found");
          }
          break;

        case "GET_ALL_VINYLS":
          sendVinylList();
          break;

        default:
          sendErrorResponse("Unknown message type: " + messageType);
      }
    }
    catch (Exception e)
    {
      Log.getInstance().addLog("Error processing message: " + e.getMessage());
      e.printStackTrace();
      sendErrorResponse("Invalid message format: " + e.getMessage());
    }
  }

  private void sendVinylList()
  {
    try
    {
      JsonObject response = new JsonObject();
      response.addProperty("type", "VINYL_LIST");

      JsonArray vinylArray = new JsonArray();
      VinylList vinylList = serverSocketManager.getModelManager()
          .getVinylList();

      for (Vinyl vinyl : vinylList.getVinyls())
      {
        JsonObject vinylJson = new JsonObject();
        vinylJson.addProperty("title", vinyl.getTitle());
        vinylJson.addProperty("artist", vinyl.getArtist());
        vinylJson.addProperty("year", vinyl.getYear());

        State state = vinyl.getState();
        vinylJson.addProperty("stateType", state.getClass().getSimpleName());
        vinylJson.addProperty("stateName", state.getStateName());
        vinylJson.addProperty("state", state.toString());

        vinylJson.addProperty("markedForRemoval", vinyl.isMarkedForRemoval());
        vinylArray.add(vinylJson);
      }

      response.add("vinyls", vinylArray);
      String jsonResponse = gson.toJson(response);
      Log.getInstance().addLog("Sending vinyl list: " + jsonResponse);
      sendMessage(jsonResponse);
    }
    catch (Exception e)
    {
      Log.getInstance().addLog("Error creating vinyl list: " + e.getMessage());
      e.printStackTrace();
      sendErrorResponse("Error creating vinyl list: " + e.getMessage());
    }
  }

  public void sendMessage(String message)
  {
    out.println(message);
    out.flush();
  }

  private void sendSuccessResponse(String message)
  {
    try
    {
      JsonObject response = new JsonObject();
      response.addProperty("type", "SUCCESS");
      response.addProperty("message", message);
      sendMessage(gson.toJson(response));
    }
    catch (Exception e)
    {
      Log.getInstance()
          .addLog("Error creating success response: " + e.getMessage());
    }
  }

  private void sendErrorResponse(String errorMessage)
  {
    try
    {
      JsonObject response = new JsonObject();
      response.addProperty("type", "ERROR");
      response.addProperty("message", errorMessage);
      sendMessage(gson.toJson(response));
    }
    catch (Exception e)
    {
      Log.getInstance()
          .addLog("Error creating error response: " + e.getMessage());
    }
  }
}