package model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class DateTime
{
  private LocalDateTime time;

  public DateTime()
  {
    this.time = LocalDateTime.now();
  }

  public DateTime(LocalDateTime time)
  {
    this.time = time;
  }

  public String getTimeStamp()
  {
    DateTimeFormatter dateTimeFormatter;
    dateTimeFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
    return time.format(dateTimeFormatter);
  }

  public String getSortableDate()
  {
    DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern(
        "yyyy-MM-dd");
    return time.format(dateTimeFormatter);
  }

  @Override public String toString()
  {
    return getTimeStamp();
  }
}
