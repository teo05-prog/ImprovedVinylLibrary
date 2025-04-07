package viewmodel;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import model.Log;
import model.Model;
import model.ModelListener;
import model.Vinyl;

public class LogVM implements ModelListener
{
  private final ObservableList<String> logs;

  public LogVM(Model model)
  {
    this.logs = FXCollections.observableArrayList();
    model.addListener(this);
  }

  public ObservableList<String> getLogs()
  {
    return logs;
  }

  public void addServerLog(String formattedLog)
  {
    logs.add(formattedLog);
  }

  @Override public void vinylAdded(Vinyl vinyl)
  {
    System.out.println("vinylAdded listener called with: " + vinyl.toString());
    String message = "Vinyl added: " + vinyl;
    Log.getInstance().addLog(message);
    Platform.runLater(() -> logs.add(message));
  }

  @Override public void vinylRemoved(Vinyl vinyl)
  {
    Platform.runLater(() -> logs.add("Vinyl removed: " + vinyl.toString()));
  }

  @Override public void vinylUpdated(Vinyl vinyl)
  {
    Platform.runLater(() -> logs.add("Vinyl updated: " + vinyl.toString()));
  }

  @Override public void vinylMarkedForRemoval(Vinyl vinyl)
  {
    Platform.runLater(
        () -> logs.add("Vinyl marked for removal: " + vinyl.toString()));
  }
}