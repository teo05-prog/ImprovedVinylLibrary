package model;

public class LogLine
{
  private String text;
  private DateTime time;

  public LogLine(String text)
  {
    this.text = text;
    this.time = new DateTime();
  }

  public String getText()
  {
    return text;
  }

  public DateTime getTime()
  {
    return time;
  }

  @Override public String toString()
  {
    return "text = '" + text + '\'' + ", time=" + time + '}';
  }
}
