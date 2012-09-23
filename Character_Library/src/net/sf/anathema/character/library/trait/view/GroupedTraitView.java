package net.sf.anathema.character.library.trait.view;

import net.sf.anathema.character.library.intvalue.IIconToggleButtonProperties;
import net.sf.anathema.character.library.intvalue.IToggleButtonTraitView;
import net.sf.anathema.character.library.trait.IModifiableCapTrait;
import net.sf.anathema.framework.value.IntegerViewFactory;
import net.sf.anathema.lib.gui.layout.GroupedColumnPanel;

import javax.swing.JComponent;

public class GroupedTraitView {

  private final GroupedColumnPanel panel;

  public GroupedTraitView(JComponent parent, int columnCount) {
    panel = new GroupedColumnPanel(parent, columnCount);
  }

  public IToggleButtonTraitView<SimpleTraitView> addTraitView(
      String labelText,
      int value,
      int maxValue,
      IModifiableCapTrait trait,
      boolean selected,
      IIconToggleButtonProperties properties,
      IntegerViewFactory factory) {
    SimpleTraitView view = new SimpleTraitView(factory, labelText, value, maxValue, trait);
    FrontToggleButtonTraitViewWrapper<SimpleTraitView> traitView = new FrontToggleButtonTraitViewWrapper<>(
        view,
        properties,
        selected);
    traitView.addComponents(panel.getCurrentColumn());
    return traitView;
  }

  public void startNewGroup(String groupLabel) {
    panel.startNewGroup(groupLabel);
  }
}