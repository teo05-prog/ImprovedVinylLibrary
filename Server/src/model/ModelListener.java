package model;

public interface ModelListener
{
  void vinylAdded(Vinyl vinyl);
  void vinylRemoved(Vinyl vinyl);
  void vinylUpdated(Vinyl vinyl);
  void vinylMarkedForRemoval(Vinyl vinyl);
}
