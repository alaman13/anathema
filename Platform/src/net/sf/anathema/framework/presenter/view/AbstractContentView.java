package net.sf.anathema.framework.presenter.view;

import javax.swing.JComponent;
import javax.swing.JPanel;

public abstract class AbstractContentView<P> implements IContentView<P> {

  private final JPanel content = new JPanel();

  public final void initGui(P properties) {
    createContent(content, properties);
  }

  protected abstract void createContent(JPanel panel, P properties);

  public final JComponent getComponent() {
    return content;
  }
}