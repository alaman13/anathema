package net.sf.anathema.campaign.music.presenter.util;

import javax.swing.Icon;

import net.sf.anathema.campaign.music.model.track.IMp3Track;
import net.sf.anathema.lib.gui.list.actionview.IActionAddableListView;

import java.util.List;

public abstract class AbstractTrackSelectionAction extends AbstractListViewSelectionEnabledAction<IMp3Track> {

  public AbstractTrackSelectionAction(IActionAddableListView<IMp3Track> view, Icon icon) {
    super(icon, view);
  }

  protected IMp3Track[] getSelectedTracks() {
    List<IMp3Track> tracks = getSelectedItems();
    return tracks.toArray(new IMp3Track[tracks.size()]);
  }
}