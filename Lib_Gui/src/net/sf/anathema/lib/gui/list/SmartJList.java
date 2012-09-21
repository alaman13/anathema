package net.sf.anathema.lib.gui.list;

import javax.swing.DefaultListModel;
import javax.swing.DefaultListSelectionModel;
import javax.swing.JList;
import java.util.ArrayList;
import java.util.List;

public class SmartJList<T> extends JList<T> {

  public SmartJList() {
    setModel(new DefaultListModel<T>());
    setSelectionModel(new DefaultListSelectionModel());
  }

  @SuppressWarnings("unchecked")
  public void setObjects(T[] objects) {
    DefaultListModel<T> listModel = (DefaultListModel) getModel();
    listModel.clear();
    for (T object : objects) {
      listModel.addElement(object);
    }
  }

  public void setSelectedObjects(T... objects) {
    DefaultListModel model = (DefaultListModel) getModel();
    List<Integer> indexList = new ArrayList<>();
    for (Object object : objects) {
      indexList.add(model.indexOf(object));
    }
    DefaultListSelectionModel selectionModel = (DefaultListSelectionModel) getSelectionModel();
    selectionModel.setValueIsAdjusting(true);
    selectionModel.clearSelection();
    for (Integer index : indexList) {
      selectionModel.addSelectionInterval(index, index);
    }
    selectionModel.setValueIsAdjusting(false);
  }

  @SuppressWarnings("unchecked")
  @Override
  public T getSelectedValue() {
    return (T) super.getSelectedValue();
  }

  @Override
  public T[] getSelectedValues() {
    return net.sf.anathema.lib.lang.ArrayUtilities.transform(super.getSelectedValues(), clazz);
  }

  public void setSelectionMode(ListSelectionMode mode) {
    setSelectionMode(mode.getMode());
  }
}