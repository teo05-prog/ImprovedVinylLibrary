import javafx.application.Application;
import javafx.stage.Stage;
import model.Model;
import model.ServerModelManager;
import view.LogViewHandler;
import viewmodel.VMFactory;

public class StartServerApplication extends Application
{
  private static ServerModelManager serverModelManager;

  public static void setModelManager(ServerModelManager manager)
  {
    serverModelManager = manager;
  }

  @Override public void start(Stage primaryStage)
  {
    Model model = (serverModelManager != null) ? serverModelManager : new ServerModelManager();
    VMFactory viewModelFactory = new VMFactory(model);
    LogViewHandler view = new LogViewHandler(viewModelFactory);

    view.start(primaryStage);
  }

  @Override public void stop() throws Exception
  {
    if (serverModelManager != null)
    {
      System.out.println("Shutting down server...");
      serverModelManager.shutdown();
    }
    super.stop();
  }
}
