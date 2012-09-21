package net.sf.anathema.lib.gui.list.actionview;

import net.sf.anathema.lib.gui.table.SmartTable;
import net.sf.anathema.lib.gui.table.columsettings.ITableColumnViewSettings;

import javax.swing.JComponent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;
import java.awt.Color;
import java.util.Collections;
import java.util.List;

public class EditableActionAddableListView<V> extends AbstractActionAddableListView<V> {

  private final SmartTable table;
  private final DefaultTableModel tableModel = new DefaultTableModel(10, 1);

  public EditableActionAddableListView(String title, ITableColumnViewSettings columnSetting) {
    super(title);
    table = new SmartTable(tableModel, new ITableColumnViewSettings[] { columnSetting });
    table.getTable().setTableHeader(null);
    table.getTable().setGridColor(new Color(0, 0, 0, 0));
  }

  @Override
  protected JComponent getDisplayComponent() {
    return table.getComponent();
  }

  @Override
  public void setObjects(V[] items) {
    for (int index = 0; index < tableModel.getRowCount(); index++) {
      tableModel.removeRow(index);
    }
    tableModel.setRowCount(0);
    for (V value : items) {
      tableModel.addRow(new Object[] { value });
    }
  }

  @Override
  public void addListSelectionListener(ListSelectionListener listener) {
    table.getTable().getSelectionModel().addListSelectionListener(listener);
  }

  @Override
  @SuppressWarnings("unchecked")
  public List<V> getSelectedItems() {
    int selectedRowIndex = table.getSelectedRowIndex();
    if (selectedRowIndex < 0) {
      return Collections.emptyList();
    }
    return Collections.singletonList((V) tableModel.getValueAt(selectedRowIndex, 0));
  }

  @Override
  protected boolean isScrollable() {
    return false;
  }
}