import javafx.application.Application;
import javafx.stage.Stage;
import model.Model;
import model.ClientModelManager;
import model.VinylList;
import view.ViewHandler;
import viewmodel.FrontVM;
import viewmodel.ManageVM;
import viewmodel.ViewState;

import java.io.IOException;

public class StartApplication extends Application
{
  private static ClientModelManager modelManager;
  private VinylList vinylList;
  private ViewState viewState;
  private FrontVM frontVM;
  private ManageVM manageVM;

  public static void setModelManager(ClientModelManager manager)
  {
    modelManager = manager;
  }

  public void start(Stage primaryStage) throws IOException
  {
    Model model = (modelManager != null) ?
        modelManager :
        new ClientModelManager();
    vinylList = new VinylList();
    viewState = new ViewState();

    frontVM = new FrontVM(model, viewState);

    ViewHandler viewHandler = new ViewHandler(primaryStage, vinylList, frontVM,
        manageVM, model, viewState);
    viewHandler.start();
  }

  @Override public void stop() throws Exception
  {
    if (modelManager != null)
    {
      System.out.println("Shutting down client...");
      modelManager.shutdown();
    }
    super.stop();
  }
}