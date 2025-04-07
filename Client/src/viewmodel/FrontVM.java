package viewmodel;

import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

import java.util.ArrayList;
import java.util.List;
import java.beans.PropertyChangeSupport;
import java.beans.PropertyChangeListener;

import model.Model;
import model.ModelListener;
import model.Vinyl;
import model.VinylList;

public class FrontVM implements ModelListener
{
  private Model model;
  private ViewState viewState;
  private VinylList vinylList;
  private StringProperty errorLabel;
  private PropertyChangeSupport support;

  public static final String VINYL_LIST_PROPERTY = "vinylList";

  public FrontVM(Model model, ViewState viewState)
  {
    this.model = model;
    this.viewState = viewState;
    this.vinylList = new VinylList();
    this.errorLabel = new SimpleStringProperty("");
    this.support = new PropertyChangeSupport(this);

    refreshVinylList();

    model.addListener(this);
  }

  private void refreshVinylList()
  {
    VinylList oldList = new VinylList();
    vinylList.clear();
    vinylList.addAll(model.getVinylList().getVinyls());
    support.firePropertyChange(VINYL_LIST_PROPERTY, oldList, vinylList);
  }

  @Override public void vinylAdded(Vinyl vinyl)
  {
    Platform.runLater(this::refreshVinylList);
  }

  @Override public void vinylRemoved(Vinyl vinyl)
  {
    Platform.runLater(this::refreshVinylList);
  }

  @Override public void vinylUpdated(Vinyl vinyl)
  {
    Platform.runLater(this::refreshVinylList);
  }

  @Override public void vinylMarkedForRemoval(Vinyl vinyl)
  {
    Platform.runLater(this::refreshVinylList);
  }

  public void addPropertyChangeListener(String propertyName,
      PropertyChangeListener listener)
  {
    support.addPropertyChangeListener(propertyName, listener);
  }

  public void removePropertyChangeListener(String propertyName,
      PropertyChangeListener listener)
  {
    support.removePropertyChangeListener(propertyName, listener);
  }

  public VinylList getVinylList()
  {
    return vinylList;
  }

  public StringProperty errorLabelProperty()
  {
    return errorLabel;
  }

  public void setSelectedVinyl(Vinyl vinyl)
  {
    viewState.setSelectedVinyl(vinyl);
  }

  public Vinyl getSelectedVinyl()
  {
    return viewState.getSelectedVinyl();
  }
}