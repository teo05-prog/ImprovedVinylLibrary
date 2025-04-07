package view.manage;

import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import model.*;
import view.ViewHandler;
import viewmodel.ManageVM;

public class ManageController
{
  @FXML private Label titleLabel;
  @FXML private Label artistLabel;
  @FXML private Label yearLabel;
  @FXML private Label stateLabel;

  @FXML private TextField titleField;
  @FXML private TextField artistField;
  @FXML private TextField yearField;
  @FXML private TextField stateField;

  @FXML private Button borrowButton;
  @FXML private Button reserveButton;
  @FXML private Button returnButton;
  @FXML private Button removeButton;
  @FXML private Button cancelButton;

  private ViewHandler viewHandler;
  private Scene scene;
  private Vinyl selectedVinyl;
  private ManageVM manageVM;

  public void init(ViewHandler viewHandler, Scene scene, ManageVM manageVM)
  {
    this.viewHandler = viewHandler;
    this.scene = scene;
    this.manageVM = manageVM;
  }

  public Scene getScene()
  {
    return scene;
  }

  public void setSelectedVinyl(Vinyl vinyl)
  {
    this.selectedVinyl = vinyl;
    updateUI();
  }

  private void updateUI()
  {
    if (selectedVinyl != null)
    {
      titleField.setText(selectedVinyl.getTitle());
      artistField.setText(selectedVinyl.getArtist());
      yearField.setText(String.valueOf(selectedVinyl.getYear()));
      stateField.setText(selectedVinyl.getState().toString());
    }
  }

  @FXML private void onReturnToFrontViewClick()
  {
    viewHandler.openFrontView();
  }

  @FXML private void onBorrowButtonClick()
  {
    System.out.println(
        "Borrow button clicked for: " + selectedVinyl.getTitle());
    if (selectedVinyl != null)
    {
      selectedVinyl.getState().toBorrow(selectedVinyl);
      manageVM.borrowVinyl(selectedVinyl);
      updateUI();
    }
  }

  @FXML private void onReserveButtonClick()
  {
    System.out.println(
        "Reserve button clicked for: " + selectedVinyl.getTitle());
    if (selectedVinyl != null)
    {
      selectedVinyl.getState().toReserve(selectedVinyl);
      manageVM.reserveVinyl(selectedVinyl);
      updateUI();
    }
  }

  @FXML private void onReturnButtonClick()
  {
    System.out.println(
        "Return button clicked for: " + selectedVinyl.getTitle());
    if (selectedVinyl != null)
    {
      selectedVinyl.getState().toReturn(selectedVinyl);
      manageVM.returnVinyl(selectedVinyl);
      updateUI();
    }
  }

  @FXML private void onRemoveButtonClick()
  {
    System.out.println(
        "Remove button clicked for: " + selectedVinyl.getTitle());
    if (selectedVinyl != null)
    {
      selectedVinyl.getState().toRemove(selectedVinyl);
      manageVM.removeVinyl(selectedVinyl);
      updateUI();
    }
  }
}