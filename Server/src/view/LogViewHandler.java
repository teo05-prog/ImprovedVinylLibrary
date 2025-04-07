package view;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.Region;
import javafx.stage.Stage;
import viewmodel.VMFactory;

public class LogViewHandler
{
  private VMFactory viewModelFactory;
  private Stage primaryStage;
  private Scene currentScene;
  private LogViewController logViewController;

  public LogViewHandler(VMFactory viewModelFactory)
  {
    this.viewModelFactory = viewModelFactory;
  }

  public void start(Stage primaryStage)
  {
    this.primaryStage = primaryStage;
    this.currentScene = new Scene(new Region());
    openView("logs");
  }

  public void openView(String id)
  {
    Region root = null;
    switch (id)
    {
      case "logs":
        root = loadLogViewController("LogView.fxml");
        break;
    }
    currentScene.setRoot(root);

    primaryStage.setTitle("Server Log");
    primaryStage.setScene(currentScene);
    primaryStage.show();
  }

  private Region loadLogViewController(String fxmlFile)
  {
    if (logViewController == null)
    {
      try
      {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource(fxmlFile));
        Region root = loader.load();
        logViewController = loader.getController();
        logViewController.init(this, viewModelFactory.getLogViewModel(), root);
      }
      catch (Exception e)
      {
        e.printStackTrace();
      }
    }
    else
    {
      logViewController.reset();
    }
    return logViewController.getRoot();
  }
}

