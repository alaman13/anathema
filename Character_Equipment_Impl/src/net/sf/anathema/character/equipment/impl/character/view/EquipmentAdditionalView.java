package net.sf.anathema.character.equipment.impl.character.view;

import java.awt.Dimension;

import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import net.disy.commons.swing.layout.grid.GridDialogLayout;
import net.disy.commons.swing.layout.grid.GridDialogLayoutData;
import net.sf.anathema.character.equipment.character.view.IEquipmentAdditionalView;
import net.sf.anathema.character.equipment.character.view.IEquipmentObjectView;
import net.sf.anathema.character.equipment.character.view.IMagicalMaterialView;
import net.sf.anathema.character.library.taskpane.ITaskPaneGroupViewFactory;
import net.sf.anathema.character.library.taskpane.TaskPaneView;
import net.sf.anathema.lib.gui.selection.IListObjectSelectionView;
import net.sf.anathema.lib.gui.selection.ListObjectSelectionView;

public class EquipmentAdditionalView implements IEquipmentAdditionalView {

  private final ListObjectSelectionView<String> equipmentPickList = new ListObjectSelectionView<String>();
  private final JPanel panel = new JPanel(new GridDialogLayout(3, false));
  private final JPanel buttonPanel = new JPanel(new GridDialogLayout(1, false));
  private final JButton selectButton = new JButton();
  private final JButton refreshButton = new JButton();
  private final TaskPaneView<EquipmentObjectView> taskPaneView = new TaskPaneView<EquipmentObjectView>(
      new ITaskPaneGroupViewFactory<EquipmentObjectView>() {
        @Override
        public EquipmentObjectView createView() {
          return new EquipmentObjectView();
        }
      });
  private final MagicMaterialView magicMaterialView = new MagicMaterialView();

  public EquipmentAdditionalView() {
    JScrollPane itemScrollpane = new JScrollPane(equipmentPickList.getComponent());
    itemScrollpane.setPreferredSize(new Dimension(150, 250));
    buttonPanel.add(selectButton);
    buttonPanel.add(refreshButton);
    taskPaneView.getComponent().setPreferredSize(new Dimension(150, 250));

    JPanel selectionPanel = new JPanel(new GridDialogLayout(1, false));
    selectionPanel.add(itemScrollpane, GridDialogLayoutData.FILL_BOTH);
    selectionPanel.add(magicMaterialView.getComponent(), GridDialogLayoutData.FILL_HORIZONTAL);

    panel.add(selectionPanel, GridDialogLayoutData.FILL_BOTH);
    panel.add(buttonPanel, GridDialogLayoutData.CENTER);
    panel.add(taskPaneView.getComponent(), GridDialogLayoutData.FILL_BOTH);
  }

  @Override
  public JComponent getComponent() {
    return panel;
  }

  @Override
  public IEquipmentObjectView addEquipmentObjectView() {
    return taskPaneView.addEquipmentObjectView();
  }

  @Override
  public void removeEquipmentObjectView(IEquipmentObjectView objectView) {
    taskPaneView.removeEquipmentObjectView((EquipmentObjectView) objectView);
  }

  @Override
  public void setSelectButtonAction(Action action) {
    selectButton.setAction(action);
  }

  @Override
  public void setRefreshButtonAction(Action action) {
    refreshButton.setAction(action);
  }

  @Override
  public void revalidateEquipmentViews() {
    taskPaneView.revalidateView();
  }

  @Override
  public IListObjectSelectionView<String> getEquipmentTemplatePickList() {
    return equipmentPickList;
  }

  @Override
  public IMagicalMaterialView getMagicMaterialView() {
    return magicMaterialView;
  }
}