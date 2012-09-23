package net.sf.anathema.character.equipment.item;

import net.sf.anathema.character.equipment.item.model.IEquipmentTemplateEditModel;
import net.sf.anathema.character.generic.equipment.weapon.IEquipmentStats;
import net.sf.anathema.framework.presenter.resources.BasicUi;
import net.sf.anathema.lib.gui.action.SmartAction;
import net.sf.anathema.lib.gui.list.actionview.IActionAddableListView;
import net.sf.anathema.lib.resources.IResources;

import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.Component;
import java.util.List;

public class RemoveStatsAction extends SmartAction {

  private final IEquipmentTemplateEditModel editModel;
  private final IActionAddableListView<IEquipmentStats> statsListView;

  public RemoveStatsAction(IResources resources, IEquipmentTemplateEditModel editModel, IActionAddableListView<IEquipmentStats> statsListView) {
    super(new BasicUi(resources).getRemoveIcon());
    this.editModel = editModel;
    this.statsListView = statsListView;
    statsListView.addListSelectionListener(new ListSelectionListener() {
      @Override
      public void valueChanged(ListSelectionEvent e) {
        updateEnabled();
      }
    });
    updateEnabled();
    setToolTipText(resources.getString("Equipment.Stats.Action.Remove.Tooltip")); //$NON-NLS-1$
  }

  private void updateEnabled() {
    setEnabled(!statsListView.getSelectedItems().isEmpty());
  }

  @Override
  protected void execute(Component parentComponent) {
    List<IEquipmentStats> selectedItems = statsListView.getSelectedItems();
    IEquipmentStats[] equipmentStats = selectedItems.toArray(new IEquipmentStats[selectedItems.size()]);
    editModel.removeStatistics(equipmentStats);
  }
}