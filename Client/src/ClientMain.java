import javafx.application.Application;
import model.ClientModelManager;

public class ClientMain
{
  private static ClientModelManager modelManager;

  public static void main(String[] args)
  {
    modelManager = new ClientModelManager();
    modelManager.initializeSocketManager();

    System.out.println("Client started");

    StartApplication.setModelManager(modelManager);

    Application.launch(StartApplication.class);
  }
}
