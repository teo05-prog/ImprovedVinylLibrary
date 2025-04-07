package viewmodel;

import model.Vinyl;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

public class ViewState
{
  private Vinyl selectedVinyl;
  private PropertyChangeSupport support;

  public ViewState()
  {
    support = new PropertyChangeSupport(this);
  }

  public Vinyl getSelectedVinyl()
  {
    return selectedVinyl;
  }

  public void setSelectedVinyl(Vinyl vinyl)
  {
    Vinyl oldVinyl = this.selectedVinyl;
    this.selectedVinyl = vinyl;
    support.firePropertyChange("selectedVinyl", oldVinyl, vinyl);
  }

  public void addListener(PropertyChangeListener listener)
  {
    support.addPropertyChangeListener(listener);
  }

  public void removeListener(PropertyChangeListener listener)
  {
    support.removePropertyChangeListener(listener);
  }
}
