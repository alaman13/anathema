package net.sf.anathema.cascades.module;

import net.sf.anathema.framework.IAnathemaModel;
import net.sf.anathema.framework.presenter.toolbar.IAnathemaTool;
import net.sf.anathema.framework.view.toolbar.IAnathemaToolbar;
import net.sf.anathema.initialization.Tool;
import net.sf.anathema.initialization.reflections.Weight;
import net.sf.anathema.lib.resources.IResources;

@Tool
@Weight(weight = 5)
public class ShowCharmCascadesTool implements IAnathemaTool {

  @Override
  public void add(IResources resources, IAnathemaModel model, IAnathemaToolbar toolbar) {
    toolbar.addSeparator();
    toolbar.addTools(ShowCascadesAction.createToolAction(resources, model));
  }
}