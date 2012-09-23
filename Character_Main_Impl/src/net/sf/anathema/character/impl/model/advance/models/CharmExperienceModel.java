package net.sf.anathema.character.impl.model.advance.models;

import net.sf.anathema.character.generic.IBasicCharacterData;
import net.sf.anathema.character.generic.magic.ICharm;
import net.sf.anathema.character.generic.magic.charms.special.ISpecialCharmConfiguration;
import net.sf.anathema.character.impl.model.advance.IPointCostCalculator;
import net.sf.anathema.character.model.ICharacter;
import net.sf.anathema.character.model.charm.ICharmConfiguration;
import net.sf.anathema.character.model.charm.special.ISubeffectCharmConfiguration;
import net.sf.anathema.character.model.charm.special.IUpgradableCharmConfiguration;
import net.sf.anathema.character.model.traits.ICoreTraitConfiguration;

import java.util.HashSet;
import java.util.Set;

public class CharmExperienceModel extends AbstractIntegerValueModel {
  private final ICoreTraitConfiguration traitConfiguration;
  private final IPointCostCalculator calculator;
  private final ICharacter character;
  private final IBasicCharacterData basicCharacter;

  public CharmExperienceModel(ICoreTraitConfiguration traitConfiguration, IPointCostCalculator calculator, ICharacter character,
                              IBasicCharacterData basicCharacter) {
    super("Experience", "Charms");
    this.traitConfiguration = traitConfiguration;
    this.calculator = calculator;
    this.character = character;
    this.basicCharacter = basicCharacter;
  }

  @Override
  public Integer getValue() {
    return getCharmCosts();
  }

  private int getCharmCosts() {
    int experienceCosts = 0;
    ICharmConfiguration charmConfiguration = character.getCharms();
    Set<ICharm> charmsCalculated = new HashSet<>();
    for (ICharm charm : charmConfiguration.getLearnedCharms(true)) {
      int charmCosts = calculateCharmCost(charmConfiguration, charm, charmsCalculated);
      if (charmConfiguration.isAlienCharm(charm)) {
        charmCosts *= 2;
      }
      experienceCosts += charmCosts;
      charmsCalculated.add(charm);
    }
    return experienceCosts;
  }

  private int calculateCharmCost(ICharmConfiguration charmConfiguration, ICharm charm, Set<ICharm> charmsCalculated) {
    ISpecialCharmConfiguration specialCharm = charmConfiguration.getSpecialCharmConfiguration(charm);
    int charmCost = calculator
            .getCharmCosts(charm, basicCharacter, traitConfiguration, character.getCharacterTemplate().getMagicTemplate().getFavoringTraitType());
    if (specialCharm != null) {
      int timesLearnedWithExperience = specialCharm.getCurrentLearnCount() - specialCharm.getCreationLearnCount();
      int specialCharmCost = timesLearnedWithExperience * charmCost;
      if (specialCharm instanceof IUpgradableCharmConfiguration) {
        return (costsExperience(charmConfiguration, charm, charmsCalculated) ? charmCost : 0) +
               ((IUpgradableCharmConfiguration) specialCharm).getUpgradeXPCost();
      }
      if (!(specialCharm instanceof ISubeffectCharmConfiguration)) {
        return specialCharmCost;
      }
      ISubeffectCharmConfiguration subeffectCharmConfiguration = (ISubeffectCharmConfiguration) specialCharm;
      int count = Math.max(0, (subeffectCharmConfiguration.getExperienceLearnedSubeffectCount() -
                               (subeffectCharmConfiguration.getCreationLearnedSubeffectCount() == 0 ? 1 : 0)));
      int subeffectCost = (int) Math.ceil(count * subeffectCharmConfiguration.getPointCostPerEffect() * 2);
      return subeffectCost + specialCharmCost;
    }
    return costsExperience(charmConfiguration, charm, charmsCalculated) ? charmCost : 0;
  }

  private boolean costsExperience(ICharmConfiguration charmConfiguration, ICharm charm, Set<ICharm> charmsCalculated) {
    if (charmConfiguration.getGroup(charm).isLearned(charm, true)) {
      for (ICharm mergedCharm : charm.getMergedCharms()) {
        if (charmsCalculated.contains(mergedCharm) && !isSpecialCharm(charm)) {
          return false;
        }
      }
      return true;
    }
    return false;
  }

  private boolean isSpecialCharm(ICharm charm) {
    return character.getCharms().getSpecialCharmConfiguration(charm) != null;
  }
}