package net.sf.anathema.lib.gui.selection;

import net.disy.commons.swing.layout.grid.IGridDialogLayoutData;
import net.sf.anathema.lib.control.ObjectValueListener;
import net.sf.anathema.lib.gui.widgets.ChangeableJComboBox;
import net.sf.anathema.lib.gui.widgets.IChangeableJComboBox;

import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.ListCellRenderer;
import java.awt.Color;
import java.awt.SystemColor;

public class ObjectSelectionView<V> implements IObjectSelectionView<V> {

  private final IChangeableJComboBox<V> comboBox;
  private final JLabel label;
  private Color disabledLabelColor = SystemColor.textInactiveText;

  public JComboBox getComboBox() {
    return comboBox.getComponent();
  }

  public ObjectSelectionView(String labelString, ListCellRenderer renderer, V... objects) {
    this(labelString, renderer, false, objects);
  }

  public ObjectSelectionView(String labelString, ListCellRenderer renderer, boolean editable, V... objects) {
    this.label = new JLabel(labelString);
    this.comboBox = new ChangeableJComboBox<>(objects, editable);
    setCellRenderer(renderer);
    setSelectedObject(null);
  }

  public void setCellRenderer(ListCellRenderer renderer) {
    comboBox.setRenderer(renderer);
  }

  /** Requires two colums */
  public void addTo(JPanel panel, IGridDialogLayoutData data) {
    panel.add(label);
    panel.add(comboBox.getComponent(), data);
  }

  @Override
  public void setSelectedObject(V object) {
    comboBox.setSelectedObject(object);
  }

  @Override
  public void setObjects(V[] objects) {
    comboBox.setObjects(objects);
  }

  @Override
  public void addObjectSelectionChangedListener(ObjectValueListener<V> listener) {
    comboBox.addObjectSelectionChangedListener(listener);
  }

  @Override
  public void setEnabled(boolean enabled) {
    if (enabled) {
      label.setForeground(SystemColor.textText);
    }
    else {
      label.setForeground(disabledLabelColor);
    }
    comboBox.getComponent().setEnabled(enabled);
  }

  @Override
  public V getSelectedObject() {
    return comboBox.getSelectedObject();
  }

  @Override
  public boolean isObjectSelected() {
    return getSelectedObject() != null;
  }

  public void setDisabledLabelColor(Color color) {
    this.disabledLabelColor = color;
  }
}