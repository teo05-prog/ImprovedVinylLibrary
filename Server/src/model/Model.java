package model;

public interface Model
{
  void addListener(ModelListener listener);
  void removeListener(ModelListener listener);

  void addVinyl(String title, String artist, String year);
  void removeVinyl(Vinyl vinyl);
  VinylList getVinylList();

  void reserve(Vinyl vinyl) throws IllegalStateException;
  void borrow(Vinyl vinyl) throws IllegalStateException;
  void returnVinyl(Vinyl vinyl) throws IllegalStateException;

  Vinyl getVinylByIndex(int index);
}
