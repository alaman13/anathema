package net.sf.anathema.character.infernal.patron.presenter;

import net.sf.anathema.character.generic.framework.additionaltemplate.listening.DedicatedCharacterChangeAdapter;
import net.sf.anathema.character.generic.framework.additionaltemplate.model.ICharacterListening;
import net.sf.anathema.character.generic.framework.additionaltemplate.model.ICharacterModelContext;
import net.sf.anathema.character.generic.type.CharacterType;
import net.sf.anathema.character.library.intvalue.IIconToggleButtonProperties;
import net.sf.anathema.character.library.intvalue.IToggleButtonTraitView;
import net.sf.anathema.character.library.intvalue.IntValueDisplayFactoryPrototype;
import net.sf.anathema.character.library.overview.IOverviewCategory;
import net.sf.anathema.character.library.trait.IFavorableDefaultTrait;
import net.sf.anathema.character.library.trait.favorable.FavorableState;
import net.sf.anathema.character.library.trait.favorable.IFavorableStateChangedListener;
import net.sf.anathema.character.library.trait.favorable.IFavorableStateVisitor;
import net.sf.anathema.character.library.trait.favorable.IFavorableTrait;
import net.sf.anathema.character.library.trait.presenter.TraitPresenter;
import net.sf.anathema.character.presenter.FavorableTraitViewProperties;
import net.sf.anathema.framework.value.IntegerViewFactory;
import net.sf.anathema.lib.collection.IdentityMapping;
import net.sf.anathema.lib.control.IBooleanValueChangedListener;
import net.sf.anathema.lib.gui.Presenter;
import net.sf.anathema.lib.resources.IResources;
import net.sf.anathema.lib.workflow.labelledvalue.ILabelledAlotmentView;

public class InfernalPatronPresenter implements Presenter {

  private final IResources resources;
  private final IInfernalPatronView view;
  private final IInfernalPatronModel model;
  private final ICharacterModelContext context;
  private final ICharacterListening characterListening;
  private final IdentityMapping<IFavorableTrait, IToggleButtonTraitView< ? >> traitViewsByTrait = new IdentityMapping<>();

  public InfernalPatronPresenter(IResources resources, IInfernalPatronView view, IInfernalPatronModel model) {
    this.resources = resources;
    this.view = view;
    this.model = model;
    this.context = model.getContext();
    this.characterListening = context.getCharacterListening();
  }

  @Override
  public void initPresentation() {
    IOverviewCategory overview = view.createOverview(resources.getString("Astrology.Overview.Title")); //$NON-NLS-1$
    final ILabelledAlotmentView favoredView = overview.addAlotmentView(
        resources.getString("Infernal.Overview.FavoredYozis"), 1); //$NON-NLS-1$
    IntegerViewFactory factory = IntValueDisplayFactoryPrototype.createWithMarkerForCharacterType(resources,
            CharacterType.INFERNAL);
    view.startGroup(resources.getString("Yozis.Yozis")); //$NON-NLS-1$
    for (final IFavorableDefaultTrait yozi : model.getAllYozis()) {
        String yoziName = resources.getString(yozi.getType().getId()); //$NON-NLS-1$
        IIconToggleButtonProperties properties =
        	new FavorableTraitViewProperties(context.getPresentationProperties(),
        			context.getBasicCharacterContext(), yozi, resources);
        final IToggleButtonTraitView< ? > yoziView = view.addIntValueView(
            yoziName,
            factory,
            properties,
            yozi.getFavorization().isCasteOrFavored());
        new TraitPresenter(yozi, yoziView).initPresentation();
        yoziView.addButtonSelectedListener(new IBooleanValueChangedListener() {
            @Override
            public void valueChanged(boolean newValue) {
              yozi.getFavorization().setFavored(newValue);
            }
          });
        yozi.getFavorization().addFavorableStateChangedListener(new IFavorableStateChangedListener() {
            @Override
            public void favorableStateChanged(FavorableState state) {
              updateView(yoziView, state);
              setOverviewData(favoredView);
            }
          });
        traitViewsByTrait.put(yozi, yoziView);
        updateView(yoziView, yozi.getFavorization().getFavorableState());
      }
    view.setOverview(overview);
    setOverviewData(favoredView);
    
    characterListening.addChangeListener(new DedicatedCharacterChangeAdapter() {
        @Override
        public void experiencedChanged(boolean experienced) {
          updateButtons();
        }
      });
      updateButtons();
  }
  
  private void updateButtons() {
	    for (IFavorableDefaultTrait yozi : model.getAllYozis()) {
	      IToggleButtonTraitView< ? > view = traitViewsByTrait.get(yozi);
	      boolean disabled = context.getBasicCharacterContext().isExperienced() || yozi.getFavorization().isCaste();
	      boolean favored = yozi.getFavorization().isCasteOrFavored();
	      view.setButtonState(favored, !disabled);
	    }
	  }
  
  private void updateView(final IToggleButtonTraitView< ? > patronView, FavorableState state) {
	    state.accept(new IFavorableStateVisitor() {
	      @Override
          public void visitDefault(FavorableState visitedState) {
	        patronView.setButtonState(false, true);
	      }

	      @Override
          public void visitFavored(FavorableState visitedState) {
	        patronView.setButtonState(true, true);
	      }

	      @Override
          public void visitCaste(FavorableState visitedState) {
	        patronView.setButtonState(true, false);
	      }
	    });
	  }

  private void setOverviewData(ILabelledAlotmentView favoredView) {
    favoredView.setValue(model.getFavoredYozi() == null ? 0 : 1);
    favoredView.setAlotment(1);
  }
}
