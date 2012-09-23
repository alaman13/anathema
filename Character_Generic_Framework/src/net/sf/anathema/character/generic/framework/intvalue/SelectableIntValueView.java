package net.sf.anathema.character.generic.framework.intvalue;

import net.disy.commons.swing.layout.grid.GridDialogLayout;
import net.disy.commons.swing.layout.grid.GridDialogLayoutData;
import net.sf.anathema.framework.value.IIntValueDisplay;
import net.sf.anathema.framework.value.IntegerViewFactory;
import net.sf.anathema.framework.value.NullUpperBounds;
import net.sf.anathema.lib.control.IIntValueChangedListener;
import net.sf.anathema.lib.control.ObjectValueListener;
import net.sf.anathema.lib.gui.selection.ISelectionIntValueChangedListener;
import net.sf.anathema.lib.gui.widgets.ChangeableJComboBox;
import net.sf.anathema.lib.gui.widgets.IChangeableJComboBox;
import org.jmock.example.announcer.Announcer;

import javax.swing.JComponent;
import javax.swing.JPanel;

public class SelectableIntValueView<V> implements ISelectableIntValueView<V> {

  private final IChangeableJComboBox<V> objectSelectionBox = new ChangeableJComboBox<>(false);

  private final IIntValueDisplay valueDisplay;
  private final Announcer<ISelectionIntValueChangedListener> control = Announcer.to(ISelectionIntValueChangedListener.class);
  private int currentValue;

  public SelectableIntValueView(IntegerViewFactory configuration, int initial, int max) {
    this.valueDisplay = configuration.createIntValueDisplay(max, initial, new NullUpperBounds());
    objectSelectionBox.addObjectSelectionChangedListener(new ObjectValueListener<V>() {
      @Override
      public void valueChanged(V newValue) {
        fireSelectionChangedEvent();
      }
    });
    valueDisplay.addIntValueChangedListener(new IIntValueChangedListener() {
      @Override
      public void valueChanged(int newValue) {
    	int oldValue = currentValue;
    	currentValue = newValue;
    	if (oldValue != newValue) {
    		valueDisplay.setValue(newValue);
    	}
        fireSelectionChangedEvent();
      }
    });
  }

  private void fireSelectionChangedEvent() {
    control.announce().valueChanged(objectSelectionBox.getSelectedObject(), currentValue);
  }

  public JComponent getContent() {
    JPanel panel = new JPanel(new GridDialogLayout(2, false));
    addTo(panel);
    return panel;
  }

  @Override
  public void addTo(JPanel panel) {
    panel.add(objectSelectionBox.getComponent(), GridDialogLayoutData.FILL_HORIZONTAL);
    panel.add(valueDisplay.getComponent());
    panel.revalidate();
  }

  @Override
  public void setSelectableValues(V[] objects) {
    objectSelectionBox.setObjects(objects);
  }

  @Override
  public void addSelectionChangedListener(ISelectionIntValueChangedListener<V> listener) {
    control.addListener(listener);
  }

  @Override
  public void setSelectedObject(V object) {
    objectSelectionBox.setSelectedObject(object);
  }

  @Override
  public void setValue(int value) {
    valueDisplay.setValue(value);
  }
}