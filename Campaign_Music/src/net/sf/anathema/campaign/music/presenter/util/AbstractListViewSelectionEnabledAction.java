package net.sf.anathema.campaign.music.presenter.util;

import net.sf.anathema.lib.gui.action.SmartAction;
import net.sf.anathema.lib.gui.list.actionview.IActionAddableListView;

import javax.swing.Icon;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.util.List;

public abstract class AbstractListViewSelectionEnabledAction<V> extends SmartAction {

  private static final long serialVersionUID = 4608996635499443248L;
  private final IActionAddableListView<V> view;

  public AbstractListViewSelectionEnabledAction(Icon icon, IActionAddableListView<V> view) {
    super(icon);
    this.view = view;
    view.addListSelectionListener(new ListSelectionListener() {
      @Override
      public void valueChanged(ListSelectionEvent e) {
        updateEnabled();
      }
    });
    updateEnabled();
  }

  protected final boolean isSelectionEmpty() {
    return getSelectedItems().isEmpty();
  }

  private void updateEnabled() {
    setEnabled(isSelectionEmpty());
  }

  protected final List<V> getSelectedItems() {
    return view.getSelectedItems();
  }
}