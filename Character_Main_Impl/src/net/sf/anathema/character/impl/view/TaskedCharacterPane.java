package net.sf.anathema.character.impl.view;

import net.sf.anathema.character.view.overview.OverviewDisplay;
import net.sf.anathema.framework.presenter.view.MultipleContentView;
import net.sf.anathema.framework.view.util.ContentProperties;
import net.sf.anathema.lib.gui.IView;
import net.sf.anathema.lib.gui.action.SmartAction;
import org.jdesktop.swingx.JXCollapsiblePane;
import org.jdesktop.swingx.JXTaskPane;
import org.jdesktop.swingx.JXTaskPaneContainer;
import org.jdesktop.swingx.JXTitledSeparator;

import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.Font;

public class TaskedCharacterPane implements CharacterPane, OverviewDisplay {

  private final JXTaskPaneContainer paneContainer = new JXTaskPaneContainer();
  private final CardLayout viewStack = new CardLayout();
  private final JPanel viewPanel = new JPanel(viewStack);
  private final JPanel content = new JPanel(new BorderLayout());
  private final JXCollapsiblePane overview = new JXCollapsiblePane(JXCollapsiblePane.Direction.RIGHT,
          new FlowLayout(FlowLayout.CENTER, 0, 0));

  public TaskedCharacterPane() {
    content.add(paneContainer, BorderLayout.WEST);
    content.add(viewPanel, BorderLayout.CENTER);
    content.add(overview, BorderLayout.EAST);
    overview.setAnimated(false);
  }

  public JXCollapsiblePane getOverview() {
    return overview;
  }

  @Override
  public MultipleContentView addMultipleContentView(String header) {
    final JXTaskPane pane = new JXTaskPane();
    pane.setTitle(header);
    paneContainer.add(pane);
    return new MultipleContentView() {
      @Override
      public void addView(IView view, final ContentProperties tabProperties) {
        final String name = tabProperties.getName();
        viewPanel.add(createContainer(view, name), name);
        pane.add(new SmartAction() {
          {
            setNameWithoutMnemonic(name);
          }

          @Override
          public void execute(Component parent) {
            viewStack.show(viewPanel, name);
          }
        });
      }

      @Override
      public JComponent getComponent() {
        return null;
      }
    };
  }

  @Override
  public void setOverview(JComponent component) {
    overview.removeAll();
    overview.add(component);
  }

  private JComponent createContainer(IView content, String name) {
    JPanel viewComponent = new JPanel(new BorderLayout());
    JXTitledSeparator title = new JXTitledSeparator(name);
    title.setBorder(new EmptyBorder(0,0,5,0));
    title.setFont(title.getFont().deriveFont(Font.BOLD));
    viewComponent.add(title, BorderLayout.NORTH);
    viewComponent.setBorder(new EmptyBorder(10, 10, 10, 10));
    viewComponent.add(content.getComponent(), BorderLayout.CENTER);
    return viewComponent;
  }

  @Override
  public JComponent getComponent() {
    return content;
  }
}
