package view;

import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import javafx.scene.layout.Region;
import viewmodel.LogVM;

public class LogViewController
{
  @FXML private ListView<String> logList;
  private Region root;
  private LogVM viewModel;
  private LogViewHandler viewHandler;

  public LogViewController()
  {
  }

  public void init(LogViewHandler viewHandler, LogVM viewModel, Region root)
  {
    this.viewHandler = viewHandler;
    this.viewModel = viewModel;
    this.root = root;
    logList.setItems(viewModel.getLogs());
  }

  public Region getRoot()
  {
    return root;
  }

  public void reset()
  {
  }
}
