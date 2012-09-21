package net.sf.anathema.lib.gui.list.actionview;

import net.sf.anathema.lib.gui.list.SmartJList;

import javax.swing.*;
import javax.swing.event.ListSelectionListener;
import java.util.List;

public class ActionAddableListView<T> extends AbstractActionAddableListView<T> implements
    IMultiSelectionActionAddableListView<T> {

  private final SmartJList<T> list;

  public ActionAddableListView(String title) {
    super(title);
    this.list = new SmartJList<>();
  }

  @Override
  public void setObjects(T[] items) {
    list.setObjects(items);
  }

  @Override
  public void addListSelectionListener(ListSelectionListener listener) {
    list.addListSelectionListener(listener);
  }

  @Override
  public List<T> getSelectedItems() {
    return list.getSelectedValuesList();
  }

  @Override
  public int[] getSelectedIndices() {
    return list.getSelectedIndices();
  }

  @Override
  protected JComponent getDisplayComponent() {
    return list;
  }

  @Override
  protected boolean isScrollable() {
    return true;
  }

  protected SmartJList<T> getList() {
    return list;
  }

  public void setListCellRenderer(ListCellRenderer renderer) {
    list.setCellRenderer(renderer);
  }
}