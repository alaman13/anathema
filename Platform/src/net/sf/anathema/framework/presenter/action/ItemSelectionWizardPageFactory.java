package net.sf.anathema.framework.presenter.action;

import net.sf.anathema.framework.item.IItemType;
import net.sf.anathema.framework.repository.access.printname.IPrintNameFileAccess;
import net.sf.anathema.framework.view.PrintNameFile;
import net.sf.anathema.lib.control.IChangeListener;
import net.sf.anathema.lib.gui.wizard.IAnathemaWizardPage;
import net.sf.anathema.lib.registry.Registry;
import net.sf.anathema.lib.workflow.wizard.selection.IAnathemaWizardModelTemplate;
import net.sf.anathema.lib.workflow.wizard.selection.IObjectSelectionProperties;
import net.sf.anathema.lib.workflow.wizard.selection.IObjectSelectionView;
import net.sf.anathema.lib.workflow.wizard.selection.IObjectSelectionWizardModel;
import net.sf.anathema.lib.workflow.wizard.selection.IWizardFactory;
import net.sf.anathema.lib.workflow.wizard.selection.LenientLegalityProvider;
import net.sf.anathema.lib.workflow.wizard.selection.ListObjectSelectionPageView;
import net.sf.anathema.lib.workflow.wizard.selection.ObjectSelectionWizardModel;
import net.sf.anathema.lib.workflow.wizard.selection.ObjectSelectionWizardPage;

public class ItemSelectionWizardPageFactory implements IWizardFactory {

  private final IItemType type;
  private final IPrintNameFileAccess access;
  private final IObjectSelectionProperties selectionProperties;

  public ItemSelectionWizardPageFactory(
      IItemType type,
      IPrintNameFileAccess access,
      IObjectSelectionProperties selectionProperties) {
    this.type = type;
    this.access = access;
    this.selectionProperties = selectionProperties;
  }

  @Override
  public IAnathemaWizardPage createPage(final IAnathemaWizardModelTemplate template) {
    if (!(template instanceof ConfigurableFileProvider)) {
      throw new IllegalArgumentException("Bad template type."); //$NON-NLS-1$
    }
    PrintNameFile[] printNameFiles = access.collectClosedPrintNameFiles(type);
    final IObjectSelectionWizardModel<PrintNameFile> model = new ObjectSelectionWizardModel<>(
        printNameFiles,
        new LenientLegalityProvider<PrintNameFile>());
    model.addListener(new IChangeListener() {
      @Override
      public void changeOccurred() {
        ((ConfigurableFileProvider) template).setFile(model.getSelectedObject().getFile());
      }
    });
    IObjectSelectionView<PrintNameFile> view = new ListObjectSelectionPageView<>();
    Registry<PrintNameFile, IAnathemaWizardModelTemplate> modelTemplateRegistry = new Registry<>();
    return new ObjectSelectionWizardPage<>(
        new NullWizardPageRegistry(),
        modelTemplateRegistry,
        model,
        view,
        selectionProperties);
  }

  @Override
  public IAnathemaWizardModelTemplate createTemplate() {
    return new ConfigurableFileProvider();
  }

  @Override
  public boolean needsFurtherDetails() {
    return true;
  }
}