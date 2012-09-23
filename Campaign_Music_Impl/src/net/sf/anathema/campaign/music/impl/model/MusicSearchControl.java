package net.sf.anathema.campaign.music.impl.model;

import com.db4o.query.Candidate;
import net.sf.anathema.campaign.music.impl.persistence.MusicDatabasePersister;
import net.sf.anathema.campaign.music.impl.persistence.search.AbstractArrayFieldSearchParameter;
import net.sf.anathema.campaign.music.impl.persistence.search.IExtendedSearchParameter;
import net.sf.anathema.campaign.music.impl.persistence.search.StringFieldSearchParameter;
import net.sf.anathema.campaign.music.model.track.IMp3Track;
import net.sf.anathema.campaign.music.model.util.IMusicCategorizationModel;
import net.sf.anathema.campaign.music.presenter.IMusicEvent;
import net.sf.anathema.campaign.music.presenter.IMusicMood;
import net.sf.anathema.campaign.music.presenter.IMusicSearchControl;
import net.sf.anathema.campaign.music.presenter.IMusicTheme;
import net.sf.anathema.campaign.music.presenter.ISearchParameter;
import net.sf.anathema.lib.control.IChangeListener;
import org.jmock.example.announcer.Announcer;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class MusicSearchControl implements IMusicSearchControl {

  private final MusicDatabasePersister persister;
  private final Announcer<IChangeListener> searchControl = Announcer.to(IChangeListener.class);
  private final IMusicCategorizationModel musicCategorizationModel;
  private IMp3Track[] searchResult;

  public MusicSearchControl(MusicDatabasePersister persister) {
    this.persister = persister;
    this.musicCategorizationModel = new DbMusicCategorizationModel(persister);
  }

  @Override
  public IMp3Track[] getTracks(String libraryName) {
    return persister.getTracksFromLibrary(libraryName);
  }

  @Override
  public ISearchParameter[] getSearchParameters() {
    return new ISearchParameter[] { new SearchParameter("Music.Labels.TrackDetails.GivenName", "givenName"), //$NON-NLS-1$//$NON-NLS-2$
        new SearchParameter("Music.Labels.TrackDetails.TrackTitle", "title"), //$NON-NLS-1$//$NON-NLS-2$
        new SearchParameter("Music.Labels.TrackDetails.AlbumTitle", "album"), //$NON-NLS-1$//$NON-NLS-2$
        new SearchParameter("Music.Labels.TrackDetails.Artist", "artist"), //$NON-NLS-1$//$NON-NLS-2$
        new SearchParameter("Music.Labels.TrackDetails.TrackNumber", "track") }; //$NON-NLS-1$//$NON-NLS-2$
  }

  @Override
  public IMp3Track[] getSearchResult() {
    return searchResult == null ? new IMp3Track[0] : searchResult;
  }

  @Override
  public void executeSearch(Map<ISearchParameter, String> constraintsByParameter) {
    List<IExtendedSearchParameter> parameterList = new ArrayList<>();
    for (Map.Entry<ISearchParameter, String> keyValue : constraintsByParameter.entrySet()) {
      ISearchParameter parameter = keyValue.getKey();
      String value = keyValue.getValue();
      parameterList.add(new StringFieldSearchParameter(parameter.getFieldName(), value));
    }
    IMusicEvent[] selectedMoods = musicCategorizationModel.getEventsModel().getSelectedValues();
    if (selectedMoods.length > 0) {
      parameterList.add(new AbstractArrayFieldSearchParameter<IMusicEvent>(selectedMoods) {
        @Override
        protected IMusicEvent[] getCandidateValues(Candidate candidate) {
          return ((IMp3Track) candidate.getObject()).getEvents();
        }
      });
    }
    IMusicTheme[] selectedThemes = musicCategorizationModel.getThemesModel().getSelectedValues();
    if (selectedThemes.length > 0) {
      parameterList.add(new AbstractArrayFieldSearchParameter<IMusicTheme>(selectedThemes) {
        @Override
        protected IMusicTheme[] getCandidateValues(Candidate candidate) {
          return ((IMp3Track) candidate.getObject()).getThemes();
        }
      });
    }
    IMusicMood[] selectedFeelings = musicCategorizationModel.getMoodsModel().getSelectedValues();
    if (selectedFeelings.length > 0) {
      parameterList.add(new AbstractArrayFieldSearchParameter<IMusicMood>(selectedFeelings) {
        @Override
        protected IMusicMood[] getCandidateValues(Candidate candidate) {
          return ((IMp3Track) candidate.getObject()).getMoods();
        }
      });
    }
    this.searchResult = persister.executeSearch(parameterList.toArray(new IExtendedSearchParameter[parameterList.size()]));
    searchControl.announce().changeOccurred();
  }

  @Override
  public void addSearchResultChangedListener(IChangeListener listener) {
    searchControl.addListener(listener);
  }

  @Override
  public IMusicCategorizationModel getMusicCategorizationModel() {
    return musicCategorizationModel;
  }
}