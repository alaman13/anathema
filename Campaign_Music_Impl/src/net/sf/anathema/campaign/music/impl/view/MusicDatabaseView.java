package net.sf.anathema.campaign.music.impl.view;

import net.miginfocom.layout.AC;
import net.miginfocom.layout.LC;
import net.miginfocom.swing.MigLayout;
import net.sf.anathema.campaign.music.impl.view.library.LibraryControlView;
import net.sf.anathema.campaign.music.impl.view.selection.MusicSelectionView;
import net.sf.anathema.campaign.music.presenter.ILibraryControlProperties;
import net.sf.anathema.campaign.music.presenter.IMusicPlayerProperties;
import net.sf.anathema.campaign.music.presenter.IMusicSelectionProperties;
import net.sf.anathema.campaign.music.presenter.ITrackDetailsProperties;
import net.sf.anathema.campaign.music.view.IMusicDatabaseView;
import net.sf.anathema.campaign.music.view.categorization.IMusicCategorizationProperties;
import net.sf.anathema.campaign.music.view.library.ILibraryControlView;
import net.sf.anathema.campaign.music.view.selection.IMusicSelectionView;
import net.sf.anathema.framework.view.item.AbstractItemView;
import net.sf.anathema.lib.gui.table.columsettings.ITableColumnViewSettings;

import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.JPanel;

public class MusicDatabaseView extends AbstractItemView implements IMusicDatabaseView {
  private final JPanel content = new JPanel(
          new MigLayout(new LC().wrapAfter(2).fill(), new AC().gap("5px").size("50%", 0, 1),
                  new AC().size("50%", 1, 3)));

  public MusicDatabaseView(String name, Icon icon) {
    super(name, icon);
  }

  @Override
  public JComponent getComponent() {
    return content;
  }

  @Override
  public IMusicSelectionView addMusicSelectionView(ITableColumnViewSettings columnSettings, boolean playerView,
                                                   IMusicCategorizationProperties categoryProperties,
                                                   IMusicPlayerProperties playerProperties,
                                                   IMusicSelectionProperties selectionProperties,
                                                   ITrackDetailsProperties detailsProperties) {
    MusicSelectionView selectionView = new MusicSelectionView(content);
    selectionView.initGui(columnSettings, playerView, categoryProperties, playerProperties, selectionProperties,
            detailsProperties);
    return selectionView;
  }

  @Override
  public ILibraryControlView addLibraryControlView(ITableColumnViewSettings settings,
                                                   IMusicCategorizationProperties categorizationProperties,
                                                   ILibraryControlProperties libraryProperties) {
    LibraryControlView libraryControlView = new LibraryControlView(settings, libraryProperties, content);
    libraryControlView.addLibraryView();
    libraryControlView.addSearchView(categorizationProperties);
    libraryControlView.initGui();
    return libraryControlView;
  }
}