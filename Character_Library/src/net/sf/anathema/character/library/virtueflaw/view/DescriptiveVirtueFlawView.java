package net.sf.anathema.character.library.virtueflaw.view;

import net.sf.anathema.character.library.virtueflaw.presenter.IDescriptiveVirtueFlawView;
import net.sf.anathema.framework.value.IntegerViewFactory;
import net.sf.anathema.lib.workflow.textualdescription.ITextView;
import net.sf.anathema.lib.workflow.textualdescription.view.AreaTextView;

import java.util.ArrayList;
import java.util.List;

public class DescriptiveVirtueFlawView extends VirtueFlawView implements IDescriptiveVirtueFlawView {
  private final List<ITextView> textViews = new ArrayList<>();
  
  public DescriptiveVirtueFlawView(IntegerViewFactory factory)
  {
	  super(factory);
  }

  @Override
  public ITextView addTextView(String labelText, int columns, int rows) {
    ITextView textView = new AreaTextView(rows, columns);
    textViews.add(textView);
    fillIntoVirtueFlawPanel(labelText, textView);
    return textView;
  }

  @Override
  public void setEnabled(boolean enabled) {
    super.setEnabled(enabled);
    for (ITextView textView : textViews) {
      textView.setEnabled(enabled);
    }
  }
}