package net.sf.anathema.lib.gui.wizard;

import com.google.common.base.Preconditions;
import net.sf.anathema.lib.gui.dialog.input.RequestFinishListener;
import net.sf.anathema.lib.gui.dialog.wizard.IWizardPage;
import net.sf.anathema.lib.gui.wizard.workflow.CheckInputListener;
import net.sf.anathema.lib.gui.wizard.workflow.ICondition;
import net.sf.anathema.lib.message.BasicMessage;
import net.sf.anathema.lib.message.IBasicMessage;
import org.jmock.example.announcer.Announcer;

import java.util.HashMap;
import java.util.Map;

public abstract class AbstractAnathemaWizardPage implements IAnathemaWizardPage {

  private IBasicMessage message = new BasicMessage("!!! UNSET-MESSAGE !!!");
  private final Announcer<RequestFinishListener> requestFinishListeners = Announcer.to(RequestFinishListener.class);
  private IAnathemaWizardPage previousPage = null;
  protected final Map<ICondition, IAnathemaWizardPage> followUpPagesByCondition = new HashMap<>();

  @Override
  public boolean canFlipToNextPage() {
    return getNextPage() != null;
  }

  @Override
  public final IAnathemaWizardPage getPreviousPage() {
    return previousPage;
  }

  protected final void addFollowupPage(IAnathemaWizardPage page, CheckInputListener inputListener, ICondition condition) {
    followUpPagesByCondition.put(condition, page);
    page.initPresentation(inputListener);
  }

  @Override
  public final IWizardPage getNextPage() {
    for (ICondition condition : followUpPagesByCondition.keySet()) {
      if (condition.isFulfilled()) {
        IWizardPage page = followUpPagesByCondition.get(condition);
        if (page instanceof IAnathemaWizardPage) {
          ((IAnathemaWizardPage) page).setPreviousPage(this);
        }
        return page;
      }
    }
    return null;
  }

  @Override
  public final void setPreviousPage(IAnathemaWizardPage page) {
    this.previousPage = page;
  }

  @Override
  public final void initPresentation(CheckInputListener inputListener) {
    initModelListening(inputListener);
    addFollowUpPages(inputListener);
    initPageContent();
  }

  protected abstract void initPageContent();

  protected abstract void addFollowUpPages(CheckInputListener inputListener);

  protected abstract void initModelListening(CheckInputListener inputListener);

  @Override
  public boolean canCancel() {
    return true;
  }

  @Override
  public void addRequestFinishListener(RequestFinishListener requestFinishListener) {
    requestFinishListeners.addListener(requestFinishListener);
  }

  @Override
  public void removeRequestFinishListener(RequestFinishListener requestFinishListener) {
    requestFinishListeners.removeListener(requestFinishListener);
  }

  @Override
  public void enter() {
  }

  @Override
  public void leave() {
  }

  @Override
  public String getTitle() {
    return getDescription();
  }

  @Override
  public IBasicMessage getMessage() {
    return message;
  }

  @Override
  public void setMessage(IBasicMessage message) {
    Preconditions.checkNotNull(message);
    this.message = message;
  }
}