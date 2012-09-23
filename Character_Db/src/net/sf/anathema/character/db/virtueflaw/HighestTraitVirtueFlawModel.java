package net.sf.anathema.character.db.virtueflaw;

import net.sf.anathema.character.generic.framework.additionaltemplate.listening.VirtueChangeListener;
import net.sf.anathema.character.generic.framework.additionaltemplate.model.ICharacterModelContext;
import net.sf.anathema.character.generic.impl.traits.ValueWeightGenericTraitSorter;
import net.sf.anathema.character.generic.template.additional.IAdditionalTemplate;
import net.sf.anathema.character.generic.traits.IGenericTrait;
import net.sf.anathema.character.generic.traits.ITraitType;
import net.sf.anathema.character.generic.traits.types.VirtueType;
import net.sf.anathema.character.library.virtueflaw.model.VirtueFlawModel;

import java.util.ArrayList;
import java.util.List;

public class HighestTraitVirtueFlawModel extends VirtueFlawModel {

  private final IGenericTrait[] virtues;
  private final ValueWeightGenericTraitSorter sorter;

  public HighestTraitVirtueFlawModel(ICharacterModelContext context, IAdditionalTemplate additionalTemplate) {
    super(context, additionalTemplate);
    this.sorter = new ValueWeightGenericTraitSorter();
    VirtueType[] virtueTypes = VirtueType.values();
    this.virtues = new IGenericTrait[virtueTypes.length];
    for (int index = 0; index < virtueTypes.length; index++) {
      virtues[index] = getContext().getTraitCollection().getTrait(virtueTypes[index]);
    }
    addVirtueChangeListener(new VirtueChangeListener() {
      @Override
      public void configuredChangeOccured() {
        ITraitType[] flawVirtueTypes = getFlawVirtueTypes();
        if (!net.sf.anathema.lib.lang.ArrayUtilities.containsValue(flawVirtueTypes, getVirtueFlaw().getRoot())) {
          getVirtueFlaw().setRoot(sorter.sortDescending(virtues).get(0).getType());
        }
      }
    });
  }

  @Override
  public ITraitType[] getFlawVirtueTypes() {
    List<IGenericTrait> sortedVirtues = sorter.sortDescending(virtues);
    List<ITraitType> highestVirtueTypes = new ArrayList<>();
    int highestVirtueValue = sortedVirtues.get(0).getCurrentValue();
    for (IGenericTrait trait : sortedVirtues) {
      if (trait.getCurrentValue() == highestVirtueValue) {
        highestVirtueTypes.add(trait.getType());
      }
    }
    return highestVirtueTypes.toArray(new ITraitType[highestVirtueTypes.size()]);
  }
}
