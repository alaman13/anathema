package net.sf.anathema.character.equipment.impl.module;

import net.sf.anathema.framework.IAnathemaModel;
import net.sf.anathema.framework.database.StartDatabaseAction;
import net.sf.anathema.framework.presenter.toolbar.IAnathemaTool;
import net.sf.anathema.framework.view.toolbar.IAnathemaToolbar;
import net.sf.anathema.initialization.Tool;
import net.sf.anathema.initialization.reflections.Weight;
import net.sf.anathema.lib.resources.IResources;

@Tool
@Weight(weight = 20)
public class StartEquipmentDatabaseTool implements IAnathemaTool {

  @Override
  public void add(IResources resources, IAnathemaModel model, IAnathemaToolbar toolbar) {
    toolbar.addSeparator();
    EquipmentDatabaseActionProperties properties = new EquipmentDatabaseActionProperties(resources, model);
    toolbar.addTools(StartDatabaseAction.createToolAction(resources, model, properties));
  }
}
