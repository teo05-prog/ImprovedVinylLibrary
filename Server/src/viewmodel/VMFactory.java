package viewmodel;

import model.Model;

public class VMFactory
{
  private LogVM logViewModel;

  public VMFactory(Model model)
  {
    logViewModel = new LogVM(model);
  }

  public LogVM getLogViewModel()
  {
    return logViewModel;
  }
}
