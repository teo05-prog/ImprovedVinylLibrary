package model;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

public class ServerInterface
{
  public static VinylList convertJsonToVinylList(JsonArray vinylsArray)
  {
    VinylList vinylList = new VinylList();
    for (JsonElement element : vinylsArray)
    {
      JsonObject vinylJson = element.getAsJsonObject();
      String title = vinylJson.get("title").getAsString();
      String artist = vinylJson.get("artist").getAsString();
      String stateStr = vinylJson.get("state").getAsString();
      String year = vinylJson.get("year").getAsString();

      Vinyl vinyl = new Vinyl(title, artist, year);
      State state;

      switch (stateStr)
      {
        case "AVAILABLE":
          state = new Available();
          break;
        case "REMOVED":
          state = new Removed();
          break;
        case "BORROWED":
          state = new Borrowed();
          break;
        case "BORROWED_AND_RESERVED":
          state = new BorrowedAndReserved();
          break;
        case "RESERVED":
          state = new Reserved();
          break;
        default:
          state = new Available();
      }
      vinyl.setState(state);
      vinylList.add(vinyl);
    }
    return vinylList;
  }
}