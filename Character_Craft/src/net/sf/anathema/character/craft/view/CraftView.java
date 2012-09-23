package net.sf.anathema.character.craft.view;

import net.disy.commons.swing.layout.grid.GridAlignment;
import net.disy.commons.swing.layout.grid.GridDialogLayout;
import net.disy.commons.swing.layout.grid.GridDialogLayoutData;
import net.disy.commons.swing.layout.grid.GridDialogLayoutDataFactory;
import net.sf.anathema.character.library.intvalue.IRemovableTraitView;
import net.sf.anathema.character.library.removableentry.view.AbstractRemovableEntryView;
import net.sf.anathema.character.library.selection.IRemovableStringEntriesView;
import net.sf.anathema.character.library.selection.IStringSelectionView;
import net.sf.anathema.character.library.selection.StringSelectionView;
import net.sf.anathema.character.library.trait.IModifiableCapTrait;
import net.sf.anathema.character.library.trait.view.RearButtonTraitViewWrapper;
import net.sf.anathema.character.library.trait.view.SimpleTraitView;
import net.sf.anathema.framework.value.IntegerViewFactory;
import net.sf.anathema.lib.gui.IView;

import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.JPanel;

public class CraftView extends AbstractRemovableEntryView<IRemovableTraitView<SimpleTraitView>> implements
    IView,
    IRemovableStringEntriesView<SimpleTraitView> {
  private final IntegerViewFactory factory;
  private final JPanel mainPanel = new JPanel(new GridDialogLayout(1, false));
  private final JPanel entryPanel = new JPanel(new GridDialogLayout(2, false));
  private final int traitMaximum;

  public CraftView(IntegerViewFactory factory, int maximum) {
    this.factory = factory;
    this.traitMaximum = maximum;
  }

  @Override
  public JComponent getComponent() {
    GridDialogLayoutData data = GridDialogLayoutDataFactory.createHorizontalFillNoGrab();
    data.setVerticalAlignment(GridAlignment.BEGINNING);
    mainPanel.add(entryPanel, data);
    return mainPanel;
  }

  @Override
  public IStringSelectionView addSelectionView(String labelText, Icon addIcon) {
    return new StringSelectionView(mainPanel, labelText, addIcon);
  }

  @Override
  public IRemovableTraitView<SimpleTraitView> addEntryView(Icon removeIcon, IModifiableCapTrait trait, String string) {
    SimpleTraitView view = new SimpleTraitView(factory, string, 0, traitMaximum, trait, GridAlignment.FILL);
    RearButtonTraitViewWrapper<SimpleTraitView> traitView = new RearButtonTraitViewWrapper<>(
        view,
        removeIcon);
    traitView.addComponents(entryPanel);
    return traitView;
  }
}