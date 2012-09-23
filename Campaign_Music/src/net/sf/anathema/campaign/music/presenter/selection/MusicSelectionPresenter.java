package net.sf.anathema.campaign.music.presenter.selection;

import java.util.ArrayList;
import java.util.List;

import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import net.sf.anathema.campaign.music.model.selection.IMusicSelection;
import net.sf.anathema.campaign.music.model.selection.IMusicSelectionModel;
import net.sf.anathema.campaign.music.model.track.IMp3Track;
import net.sf.anathema.campaign.music.view.selection.IMusicSelectionView;
import net.sf.anathema.lib.control.IChangeListener;
import net.sf.anathema.lib.gui.Presenter;
import net.sf.anathema.lib.gui.list.actionview.IActionAddableListView;
import net.sf.anathema.lib.gui.list.actionview.IMultiSelectionActionAddableListView;
import net.sf.anathema.lib.resources.IResources;

public class MusicSelectionPresenter implements Presenter {

  private final IMusicSelectionView selectionView;
  private final IMusicSelectionModel selectionModel;
  private final IResources resources;

  public MusicSelectionPresenter(
      IResources resources,
      IMusicSelectionView selectionView,
      IMusicSelectionModel selectionModel) {
    this.resources = resources;
    this.selectionView = selectionView;
    this.selectionModel = selectionModel;
  }

  @Override
  public void initPresentation() {
    selectionModel.addCurrentSelectionChangeListener(new IChangeListener() {
      @Override
      public void changeOccurred() {
        updateTrackList();
      }
    });
    selectionModel.getTrackDetailModel().addChangeDetailListener(new IChangeListener() {
      @Override
      public void changeOccurred() {
        refreshTrackList();
      }
    });
    selectionModel.addSelectionsChangeListener(new IChangeListener() {
      @Override
      public void changeOccurred() {
        updateSelectionList();
      }
    });
    updateTrackList();
    updateSelectionList();
    final IMultiSelectionActionAddableListView<IMp3Track> trackListView = selectionView.getTrackListView();
    IActionAddableListView<IMusicSelection> selectionListView = selectionView.getSelectionListView();
    selectionListView.addAction(new NewSelectionAction(resources, selectionModel));
    selectionListView.addAction(new DeleteSelectionAction(resources, selectionListView, selectionModel));
    selectionListView.addAction(new PersistReplaceSelectionAction(selectionListView, selectionModel, resources));
    selectionListView.addAction(new PersistAppendSelectionAction(selectionListView, selectionModel, resources));
    selectionListView.addAction(new RetrieveSelectionAction(resources, selectionListView, selectionModel));
    trackListView.addAction(new ClearSelectionAction(resources, selectionModel));
    trackListView.addAction(new DeleteSelectionTracksAction(resources, trackListView, selectionModel));
    trackListView.addAction(new ExportSelectionTracksAction(resources, selectionModel));
    trackListView.addListSelectionListener(new ListSelectionListener() {
      @Override
      public void valueChanged(ListSelectionEvent e) {
        List<IMp3Track> trackList = new ArrayList<>();
        for (IMp3Track trackObject : trackListView.getSelectedItems()) {
          trackList.add(trackObject);
        }
        selectionModel.setMarkedTracks(trackList.toArray(new IMp3Track[trackList.size()]));
      }
    });
  }

  private void refreshTrackList() {
    selectionView.getTrackListView().refreshView();
  }

  private void updateTrackList() {
    selectionView.getTrackListView().setObjects(selectionModel.getCurrentSelection().getContent());
  }

  private void updateSelectionList() {
    selectionView.getSelectionListView().setObjects(selectionModel.getPersistedSelections());
  }
}
