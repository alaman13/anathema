package net.sf.anathema.campaign.music.presenter.library.control;

import net.sf.anathema.campaign.music.model.libary.ILibrary;
import net.sf.anathema.campaign.music.model.libary.ILibraryControl;
import net.sf.anathema.campaign.music.view.library.ILibraryControlView;
import net.sf.anathema.framework.presenter.resources.BasicUi;
import net.sf.anathema.lib.gui.action.SmartAction;
import net.sf.anathema.lib.resources.IResources;

import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.Component;

public class DeleteLibraryAction extends SmartAction {

  private final ILibraryControlView controlView;
  private final ILibraryControl libraryModel;

  public DeleteLibraryAction(IResources resources, ILibraryControlView controlView, ILibraryControl model) {
    super(new BasicUi(resources).getRemoveIcon());
    this.controlView = controlView;
    this.libraryModel = model;
    setToolTipText(resources.getString("Music.Actions.DeleteLibrary.Tooltip")); //$NON-NLS-1$
    setEnabled(false);
    addEnabledListener();
  }

  private void addEnabledListener() {
    controlView.addLibraryListSelectionListener(new ListSelectionListener() {
      @Override
      public void valueChanged(ListSelectionEvent e) {
        setEnabled(controlView.getSelectedLibrary() != null);
      }
    });
  }

  @Override
  protected void execute(Component parentComponent) {
    libraryModel.removeLibrary((ILibrary) controlView.getSelectedLibrary());
  }

}
