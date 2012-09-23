package net.sf.anathema.character.library.trait.specialties;

import net.sf.anathema.character.generic.framework.ITraitReference;
import net.sf.anathema.character.generic.framework.additionaltemplate.listening.ICharacterChangeListener;
import net.sf.anathema.character.generic.framework.additionaltemplate.model.ICharacterModelContext;
import net.sf.anathema.character.generic.traits.ITraitType;
import net.sf.anathema.character.generic.traits.groups.ITraitTypeGroup;
import net.sf.anathema.character.generic.traits.groups.TraitTypeGroup;
import net.sf.anathema.character.library.trait.ITrait;
import net.sf.anathema.character.library.trait.ITraitCollection;
import net.sf.anathema.character.library.trait.subtrait.ISubTrait;
import net.sf.anathema.character.library.trait.subtrait.ISubTraitContainer;
import net.sf.anathema.character.library.trait.subtrait.ISubTraitListener;
import net.sf.anathema.character.library.trait.visitor.IAggregatedTrait;
import net.sf.anathema.character.library.trait.visitor.IDefaultTrait;
import net.sf.anathema.character.library.trait.visitor.ITraitVisitor;
import net.sf.anathema.lib.control.IChangeListener;
import net.sf.anathema.lib.lang.StringUtilities;
import org.jmock.example.announcer.Announcer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class SpecialtiesConfiguration implements ISpecialtiesConfiguration {

  private final Map<ITraitType, ISubTraitContainer> specialtiesByType = new HashMap<>();
  private final Map<ITraitReference, ISubTraitContainer> specialtiesByTrait = new HashMap<>();
  private final Announcer<IChangeListener> control = Announcer.to(IChangeListener.class);
  private final Announcer<ITraitReferencesChangeListener> traitControl = Announcer.to(ITraitReferencesChangeListener.class);
  private final ICharacterModelContext context;
  private String currentName;
  private ITraitReference currentType;

  public SpecialtiesConfiguration(ITraitCollection traitCollection, ITraitTypeGroup[] groups,
                                  ICharacterModelContext context) {
    this.context = context;
    ITraitType[] traitTypes = TraitTypeGroup.getAllTraitTypes(groups);
    for (ITrait trait : traitCollection.getTraits(traitTypes)) {
      trait.accept(new ITraitVisitor() {
        @Override
        public void visitAggregatedTrait(IAggregatedTrait visitedTrait) {
          initializeAggregatedTrait(visitedTrait);
        }

        @Override
        public void visitDefaultTrait(IDefaultTrait visitedTrait) {
          ITraitReference reference = new DefaultTraitReference(visitedTrait);
          SpecialtiesContainer container = addSpecialtiesContainer(reference);
          specialtiesByType.put(visitedTrait.getType(), container);
        }
      });
    }
  }

  private void initializeAggregatedTrait(final IAggregatedTrait visitedTrait) {
    visitedTrait.getSubTraits().addSubTraitListener(new ISubTraitListener() {
      @Override
      public void subTraitAdded(ISubTrait subTrait) {
        ISubTraitContainer container = specialtiesByType.get(visitedTrait.getType());
        addSubTraitSpecialtiesContainer(subTrait, (AggregatedSpecialtiesContainer) container);
      }

      @Override
      public void subTraitRemoved(ISubTrait subTrait) {
        ISubTraitContainer container = specialtiesByType.get(visitedTrait.getType());
        removeSubTraitSpecialtiesContainer(subTrait, (AggregatedSpecialtiesContainer) container);
      }

      @Override
      public void subTraitValueChanged() {
        // nothing to do
      }
    });
    AggregatedSpecialtiesContainer container = new AggregatedSpecialtiesContainer();
    for (ISubTrait subTrait : visitedTrait.getSubTraits().getSubTraits()) {
      addSubTraitSpecialtiesContainer(subTrait, container);
    }
    specialtiesByType.put(visitedTrait.getType(), container);
  }

  private void removeSubTraitSpecialtiesContainer(ISubTrait subTrait, AggregatedSpecialtiesContainer container) {
    ITraitReference reference = new SubTraitReference(subTrait);
    ISubTraitContainer subContainer = specialtiesByTrait.remove(reference);
    subContainer.dispose();
    container.removeContainer(subContainer);
    traitControl.announce().referenceRemoved(new SubTraitReference(subTrait));
  }

  private void addSubTraitSpecialtiesContainer(ISubTrait subTrait, AggregatedSpecialtiesContainer container) {
    SubTraitReference reference = new SubTraitReference(subTrait);
    SpecialtiesContainer subContainer = addSpecialtiesContainer(reference);
    container.addContainer(subContainer);
    traitControl.announce().referenceAdded(reference);
  }

  private SpecialtiesContainer addSpecialtiesContainer(ITraitReference reference) {
    SpecialtiesContainer specialtiesContainer = new SpecialtiesContainer(reference, context.getTraitContext());
    specialtiesByTrait.put(reference, specialtiesContainer);
    return specialtiesContainer;
  }

  @Override
  public ISubTraitContainer getSpecialtiesContainer(ITraitReference trait) {
    return specialtiesByTrait.get(trait);
  }

  @Override
  public ISubTraitContainer getSpecialtiesContainer(ITraitType traitType) {
    return specialtiesByType.get(traitType);
  }

  @Override
  public ITraitReference[] getAllTraits() {
    Set<ITraitReference> keySet = specialtiesByTrait.keySet();
    return keySet.toArray(new ITraitReference[keySet.size()]);
  }

  @Override
  public ITraitReference[] getAllEligibleTraits() {
    List<ITraitReference> keySet = new ArrayList<>(specialtiesByTrait.keySet());
    Set<ITraitReference> toRemove = new HashSet<>();
    for (ITraitReference trait : keySet) {
      if (!getSpecialtiesContainer(trait.getTraitType()).isNewSubTraitAllowed())
        toRemove.add(trait);
    }
    keySet.removeAll(toRemove);
    return keySet.toArray(new ITraitReference[keySet.size()]);
  }

  @Override
  public void setCurrentSpecialtyName(String newSpecialtyName) {
    this.currentName = newSpecialtyName;
    control.announce().changeOccurred();
  }

  @Override
  public void setCurrentTrait(ITraitReference newValue) {
    this.currentType = newValue;
    control.announce().changeOccurred();
  }

  @Override
  public void commitSelection() {
    ISubTrait specialty = specialtiesByTrait.get(currentType).addSubTrait(currentName);
    if (specialty != null && specialty.getCurrentValue() == 0) {
      specialty.setCurrentValue(1);
    }
  }

  @Override
  public void clear() {
    currentName = null;
    currentType = null;
    control.announce().changeOccurred();
  }

  @Override
  public void addSelectionChangeListener(IChangeListener listener) {
    control.addListener(listener);
  }

  @Override
  public boolean isEntryComplete() {
    return !StringUtilities.isNullOrEmpty(currentName) && currentType != null;
  }

  @Override
  public boolean isExperienced() {
    return context.getBasicCharacterContext().isExperienced();
  }

  @Override
  public void addCharacterChangeListener(ICharacterChangeListener listener) {
    context.getCharacterListening().addChangeListener(listener);
  }

  @Override
  public void addTraitListChangeListener(ITraitReferencesChangeListener listener) {
    traitControl.addListener(listener);
  }
}