package net.sf.anathema.campaign.music.presenter.selection;

import net.sf.anathema.campaign.music.model.selection.IMusicSelection;
import net.sf.anathema.campaign.music.model.selection.IMusicSelectionModel;
import net.sf.anathema.campaign.music.presenter.MusicUI;
import net.sf.anathema.lib.gui.list.actionview.IActionAddableListView;
import net.sf.anathema.lib.resources.IResources;

import java.awt.Component;

public class PersistReplaceSelectionAction extends AbstractPersistSelectionAction {

  private static final long serialVersionUID = -8545986574118274085L;

  public PersistReplaceSelectionAction(
      IActionAddableListView<IMusicSelection> selectionListView,
      IMusicSelectionModel selectionModel,
      IResources resources) {
    super(new MusicUI(resources).getReplaceToLeftIcon(), resources.getString("Music.Actions.ReplaceSelection.Tooltip"), //$NON-NLS-1$
        selectionListView,
        selectionModel);
  }

  @Override
  protected void execute(Component parentComponent) {
    IMusicSelection currentSelection = getSelectionModel().getCurrentSelection();
    IMusicSelection persistSelection = getSelectionListView().getSelectedItems().get(0);
    persistSelection.clear();
    persistSelection.addTracks(currentSelection.getContent());
    getSelectionModel().persistSelection(persistSelection);
  }
}