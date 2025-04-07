import javafx.application.Application;
import model.ServerModelManager;

public class ServerMain
{
  private static ServerModelManager modelManager;

  public static void main(String[] args)
  {
    modelManager = new ServerModelManager();
    modelManager.initializeSocketManager();

    System.out.println("Server started");

    StartServerApplication.setModelManager(modelManager);
    Application.launch(StartServerApplication.class);
  }
}
