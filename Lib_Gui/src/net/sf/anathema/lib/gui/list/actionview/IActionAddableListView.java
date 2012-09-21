package net.sf.anathema.lib.gui.list.actionview;

import net.sf.anathema.lib.gui.IView;

import javax.swing.Action;
import javax.swing.event.ListSelectionListener;
import java.util.List;

public interface IActionAddableListView<T> extends IView {

  void setObjects(T[] items);

  void setListTitle(String title);

  void addListSelectionListener(ListSelectionListener listener);

  List<T> getSelectedItems();

  void addAction(Action action);

  void refreshView();
}