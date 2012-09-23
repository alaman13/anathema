package net.sf.anathema.character.impl.specialties;

import net.disy.commons.swing.layout.grid.GridDialogLayout;
import net.disy.commons.swing.layout.grid.GridDialogLayoutData;
import net.disy.commons.swing.layout.grid.GridDialogLayoutDataFactory;
import net.sf.anathema.character.generic.framework.ITraitReference;
import net.sf.anathema.character.impl.view.SpecialtyView;
import net.sf.anathema.character.presenter.specialty.ISpecialtiesConfigurationView;
import net.sf.anathema.character.view.ISpecialtyView;
import net.sf.anathema.framework.presenter.view.ButtonControlledComboEditView;
import net.sf.anathema.framework.presenter.view.IButtonControlledComboEditView;
import net.sf.anathema.framework.value.IntegerViewFactory;
import net.sf.anathema.lib.gui.IView;

import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListCellRenderer;

public class SpecialtiesView implements ISpecialtiesConfigurationView, IView {

  private final IntegerViewFactory factory;
  private final JPanel mainPanel = new JPanel(new GridDialogLayout(1, false));
  private final JPanel specialtyPanel = new JPanel(new GridDialogLayout(5, false));

  public SpecialtiesView(IntegerViewFactory factory) {
    this.factory = factory;
  }

  @Override
  public ISpecialtyView addSpecialtyView(
      String abilityName,
      String specialtyName,
      Icon deleteIcon,
      int value,
      int maxValue) {
    SpecialtyView specialtyView = new SpecialtyView(factory, abilityName, deleteIcon, specialtyName, value, maxValue);
    specialtyView.addComponents(specialtyPanel);
    mainPanel.revalidate();
    return specialtyView;
  }

  @Override
  public IButtonControlledComboEditView<ITraitReference> addSpecialtySelectionView(
      String labelText,
      ListCellRenderer renderer,
      Icon addIcon) {
    ButtonControlledComboEditView<ITraitReference> objectSelectionView = new ButtonControlledComboEditView<>(
        addIcon,
        renderer);
    mainPanel.add(objectSelectionView.getComponent());
    return objectSelectionView;
  }

  @Override
  public JComponent getComponent() {
    GridDialogLayoutData data = GridDialogLayoutDataFactory.createFillNoGrab();
    data.setGrabExcessVerticalSpace(true);
    mainPanel.add(new JScrollPane(specialtyPanel), data);
    return mainPanel;
  }
}