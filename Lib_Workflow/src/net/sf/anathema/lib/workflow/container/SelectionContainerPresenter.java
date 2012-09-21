package net.sf.anathema.lib.workflow.container;

import net.sf.anathema.lib.control.IChangeListener;
import net.sf.anathema.lib.gui.Presenter;
import net.sf.anathema.lib.lang.ArrayFactory;
import net.sf.anathema.lib.lang.IArrayFactory;

import java.util.List;

public class SelectionContainerPresenter<V> implements Presenter {

  private final ISelectionContainerModel<V> model;
  private final ISelectionContainerView<V> view;
  private final IArrayFactory<V> arrayFactory;

  public SelectionContainerPresenter(
      ISelectionContainerModel<V> model,
      ISelectionContainerView<V> view,
      Class<V> componentType) {
    this.model = model;
    this.view = view;
    this.arrayFactory = new ArrayFactory<>(componentType);
  }

  @Override
  public void initPresentation() {
    view.addSelectionChangeListener(new IChangeListener() {
      @Override
      public void changeOccurred() {
        List<V> selectedValues = view.getSelectedValues();
        V[] valueArray = arrayFactory.createArray(selectedValues.size());
        selectedValues.toArray(valueArray);
        model.setSelectedValues(valueArray);
      }
    });
    model.addChangeListener(new IChangeListener() {
      @Override
      public void changeOccurred() {
        V[] values = model.getSelectedValues();
        if (values == null) {
          values = arrayFactory.createArray(0);
        }
        view.setSelectedValues(values);
      }
    });
    view.populate(model.getAllAvailableValues());

  }
}
