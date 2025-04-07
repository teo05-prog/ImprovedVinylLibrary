package viewmodel;

import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

import java.beans.PropertyChangeSupport;
import java.beans.PropertyChangeListener;

import model.*;

public class ManageVM implements ModelListener
{
  private Model model;
  private ViewState viewState;
  private VinylList vinylList;
  private StringProperty errorLabel;
  private PropertyChangeSupport support;

  public static final String VINYL_LIST_PROPERTY = "vinylList";

  public ManageVM(Model model, ViewState viewState)
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
    VinylList oldList = vinylList;
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

  public void reserveVinyl(Vinyl vinyl)
  {
    try
    {
      model.reserve(vinyl);
      errorLabel.set("");
    }
    catch (IllegalStateException e)
    {
      errorLabel.set("Error: " + e.getMessage());
    }
  }

  public void borrowVinyl(Vinyl vinyl)
  {
    try
    {
      model.borrow(vinyl);
      errorLabel.set("");
    }
    catch (IllegalStateException e)
    {
      errorLabel.set("Error: " + e.getMessage());
    }
  }

  public void returnVinyl(Vinyl vinyl)
  {
    try
    {
      model.returnVinyl(vinyl);
      errorLabel.set("");
    }
    catch (IllegalStateException e)
    {
      errorLabel.set("Error: " + e.getMessage());
    }
  }

  public void removeVinyl(Vinyl vinyl)
  {
    try
    {
      model.removeVinyl(vinyl);
      errorLabel.set("");
    }
    catch (IllegalStateException e)
    {
      errorLabel.set("Error: " + e.getMessage());
    }
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