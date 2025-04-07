package view;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import model.Model;
import model.Vinyl;
import view.front.FrontController;
import view.manage.ManageController;
import viewmodel.FrontVM;
import model.VinylList;
import viewmodel.ManageVM;
import viewmodel.ViewState;

import java.io.IOException;

public class ViewHandler
{
  private Stage primaryStage;
  private FrontController frontController;
  private ManageController manageController;
  private VinylList vinylList;
  private FrontVM frontVM;
  private ManageVM manageVM;
  private Model model;
  private ViewState viewState;

  public ViewHandler(Stage primaryStage, VinylList vinylList, FrontVM frontVM,
      ManageVM manageVM, Model model, ViewState viewState)
  {
    this.primaryStage = primaryStage;
    this.vinylList = vinylList;
    this.frontVM = frontVM;
    this.manageVM = manageVM;
    this.model = model;
    this.viewState = viewState;
  }

  public void start() throws IOException
  {
    loadFrontView();
    loadManageView();
    openFrontView();
  }

  private void loadFrontView() throws IOException
  {
    FXMLLoader loader = new FXMLLoader(
        getClass().getResource("/view/front/Front.fxml"));
    Scene scene = new Scene(loader.load());
    frontController = loader.getController();
    frontController.init(this, frontVM, scene);
    primaryStage.setScene(scene);
  }

  private void loadManageView()
  {
    try
    {
      FXMLLoader loader = new FXMLLoader(
          getClass().getResource("/view/manage/Manage.fxml"));
      Scene scene = new Scene(loader.load());
      manageController = loader.getController();

      ManageVM manageVM = new ManageVM(model, viewState);
      manageController.init(this, scene, manageVM);
    }
    catch (IOException e)
    {
      e.printStackTrace();
    }
  }

  public void openFrontView()
  {
    primaryStage.setTitle("Vinyl Library");
    primaryStage.setScene(frontController.getScene());
    frontController.updateVinylTable();
    primaryStage.show();
  }

  public void openManageView(Vinyl selectedVinyl)
  {
    manageController.setSelectedVinyl(selectedVinyl);
    primaryStage.setTitle("Vinyl Management");
    primaryStage.setScene(manageController.getScene());
    primaryStage.show();
  }
}