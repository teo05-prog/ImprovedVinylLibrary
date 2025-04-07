package view.front;

import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import model.Vinyl;
import model.VinylList;
import view.ViewHandler;
import viewmodel.FrontVM;

public class FrontController
{
  @FXML private TableView<Vinyl> vinylTable;
  @FXML private TableColumn<Vinyl, String> titleColumn;
  @FXML private TableColumn<Vinyl, String> artistColumn;
  @FXML private TableColumn<Vinyl, String> yearColumn;
  @FXML private TableColumn<Vinyl, String> statusColumn;

  private ViewHandler viewHandler;
  private FrontVM frontVM;
  private Scene scene;

  @FXML private void initialize()
  {
    titleColumn.setCellValueFactory(new PropertyValueFactory<>("title"));
    artistColumn.setCellValueFactory(new PropertyValueFactory<>("artist"));
    yearColumn.setCellValueFactory(new PropertyValueFactory<>("year"));
    statusColumn.setCellValueFactory(new PropertyValueFactory<>("state"));
  }

  public void init(ViewHandler viewHandler, FrontVM frontVM, Scene scene)
  {
    this.viewHandler = viewHandler;
    this.frontVM = frontVM;
    this.scene = scene;

    VinylList vinyls = frontVM.getVinylList();
    vinylTable.getItems().addAll(vinyls.getVinyls());
  }

  @FXML private void onManageClick()
  {
    Vinyl selectedVinyl = vinylTable.getSelectionModel().getSelectedItem();
    if (selectedVinyl != null)
    {
      viewHandler.openManageView(selectedVinyl);
    }
  }

  public Scene getScene()
  {
    return scene;
  }

  public void updateVinylTable()
  {
    vinylTable.refresh();
  }
}