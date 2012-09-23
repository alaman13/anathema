package net.sf.anathema.character.impl.model.traits.creation;

import net.sf.anathema.character.generic.template.ICharacterTemplate;
import net.sf.anathema.character.generic.template.abilities.IGroupedTraitType;
import net.sf.anathema.character.generic.traits.ITraitType;
import net.sf.anathema.character.library.trait.favorable.IFavorableTrait;
import net.sf.anathema.character.library.trait.favorable.IIncrementChecker;
import net.sf.anathema.character.model.traits.ICoreTraitConfiguration;

import java.util.ArrayList;
import java.util.List;

public class FavoredIncrementChecker implements IIncrementChecker {

  private final int maxFavoredCount;
  private final ICoreTraitConfiguration traitConfiguration;
  private final ITraitType[] traitTypes;

  public static IIncrementChecker createFavoredAbilityIncrementChecker(
      ICharacterTemplate template,
      ICoreTraitConfiguration traitConfiguration) {
    int maxFavoredAbilityCount = template.getCreationPoints().getAbilityCreationPoints().getFavorableTraitCount();
    List<ITraitType> abilityTypes = new ArrayList<>();
    for (IGroupedTraitType traitType : template.getAbilityGroups()) {
      abilityTypes.add(traitType.getTraitType());
    }
    return new FavoredIncrementChecker(
        maxFavoredAbilityCount,
        abilityTypes.toArray(new ITraitType[abilityTypes.size()]),
        traitConfiguration);
  }
  
  public static IIncrementChecker createFavoredAttributeIncrementChecker(
	      ICharacterTemplate template,
	      ICoreTraitConfiguration traitConfiguration) {
	    int maxFavoredAttributeCount = template.getCreationPoints().getAttributeCreationPoints().getFavorableTraitCount();
	    List<ITraitType> attributeTypes = new ArrayList<>();
	    for (IGroupedTraitType traitType : template.getAttributeGroups()) {
	      attributeTypes.add(traitType.getTraitType());
	    }
	    return new FavoredIncrementChecker(
	        maxFavoredAttributeCount,
	        attributeTypes.toArray(new ITraitType[attributeTypes.size()]),
	        traitConfiguration);
	  }
  
  public static IIncrementChecker createFavoredYoziIncrementChecker(
	      ICharacterTemplate template,
	      ICoreTraitConfiguration traitConfiguration) {
	    int maxFavoredAttributeCount = 1;
	    List<ITraitType> yoziTypes = new ArrayList<>();
	    for (IGroupedTraitType traitType : template.getYoziGroups())
	      yoziTypes.add(traitType.getTraitType());
	    return new FavoredIncrementChecker(
	        maxFavoredAttributeCount,
	        yoziTypes.toArray(new ITraitType[yoziTypes.size()]),
	        traitConfiguration);
	  }

  public FavoredIncrementChecker(
      int maxFavoredCount,
      ITraitType[] traitTypes,
      ICoreTraitConfiguration traitConfiguration) {
    this.maxFavoredCount = maxFavoredCount;
    this.traitTypes = traitTypes;
    this.traitConfiguration = traitConfiguration;
  }

  @Override
  public boolean isValidIncrement(int increment) {
    int count = 0;
    for (IFavorableTrait trait : getAllTraits()) {
      if (trait.getFavorization().isFavored()) {
        count++;
      }
    }
    return count + increment <= maxFavoredCount;
  }

  private IFavorableTrait[] getAllTraits() {
    return traitConfiguration.getFavorableTraits(traitTypes);
  }
}