package net.sf.anathema.lib.workflow.container.factory;

import net.disy.commons.swing.layout.grid.GridDialogLayoutData;
import net.disy.commons.swing.layout.grid.IDialogComponent;
import net.sf.anathema.lib.gui.container.TitledPanel;
import net.sf.anathema.lib.gui.gridlayout.DefaultGridDialogPanel;
import net.sf.anathema.lib.gui.gridlayout.IGridDialogPanel;
import net.sf.anathema.lib.gui.widgets.IntegerSpinner;
import net.sf.anathema.lib.workflow.textualdescription.ITextView;
import net.sf.anathema.lib.workflow.textualdescription.view.AreaTextView;
import net.sf.anathema.lib.workflow.textualdescription.view.LabelTextView;
import net.sf.anathema.lib.workflow.textualdescription.view.LineTextView;

import javax.swing.JLabel;
import javax.swing.JPanel;

public class StandardPanelBuilder {

  private final IGridDialogPanel dialogPanel = new DefaultGridDialogPanel();

  public void addDialogComponent(IDialogComponent component) {
    dialogPanel.add(component);
  }

  public ITextView addLineTextView(String labelName, int columnCount) {
    return addLabelledTextView(labelName, new LineTextView(columnCount));
  }

  public ITextView addAreaTextView(String labelName, int rowCount, int columnCount) {
    return addLabelledTextView(labelName, new AreaTextView(rowCount, columnCount));
  }

  private ITextView addLabelledTextView(String labelText, ITextView textView) {
    final LabelTextView labelTextView = new LabelTextView(labelText, textView);
    addDialogComponent(new IDialogComponent() {
      @Override
      public int getColumnCount() {
        return 2;
      }

      @Override
      public void fillInto(JPanel panel, int columnCount) {
        labelTextView.addToStandardPanel(panel, columnCount - 1);
      }
    });
    return textView;
  }

  public JPanel getTitledContent(String title) {
    return new TitledPanel(title, dialogPanel.getComponent());
  }

  public JPanel getUntitledContent() {
    return dialogPanel.getComponent();
  }

  public IntegerSpinner addIntegerSpinner(final String labelString, int startValue) {
    final IntegerSpinner spinner = new IntegerSpinner(startValue);
    addDialogComponent(new IDialogComponent() {
      @Override
      public int getColumnCount() {
        return 2;
      }

      @Override
      public void fillInto(JPanel panel, int columnCount) {
        panel.add(new JLabel(labelString));
        panel.add(spinner.getComponent(), GridDialogLayoutData.FILL_HORIZONTAL);
      }
    });
    return spinner;
  }
}