package net.sf.anathema.character.impl.model.advance;

import net.sf.anathema.character.generic.IBasicCharacterData;
import net.sf.anathema.character.generic.character.IGenericTraitCollection;
import net.sf.anathema.character.generic.impl.magic.MartialArtsUtilities;
import net.sf.anathema.character.generic.magic.ICharm;
import net.sf.anathema.character.generic.magic.IMagic;
import net.sf.anathema.character.generic.magic.charms.MartialArtsLevel;
import net.sf.anathema.character.generic.template.experience.ICostAnalyzer;
import net.sf.anathema.character.generic.traits.types.AbilityType;
import net.sf.anathema.character.model.ICharacter;

public class CostAnalyzer implements ICostAnalyzer {

  private final ICharacter character;
  private final IBasicCharacterData basicCharacter;
  private final IGenericTraitCollection traitCollection;

  public CostAnalyzer(IBasicCharacterData basicCharacter, IGenericTraitCollection traitCollection) {
    this.basicCharacter = basicCharacter;
    this.traitCollection = traitCollection;
    this.character = null;
  }

  public CostAnalyzer(ICharacter character, IBasicCharacterData basicCharacter, IGenericTraitCollection traitCollection) {
    this.character = character;
    this.basicCharacter = basicCharacter;
    this.traitCollection = traitCollection;
  }

  @Override
  public final boolean isOccultFavored() {
    return traitCollection.getFavorableTrait(AbilityType.Occult).isCasteOrFavored();
  }

  @Override
  public final boolean isMagicFavored(IMagic magic) {
    return magic.isFavored(basicCharacter, traitCollection);
  }

  @Override
  public MartialArtsLevel getMartialArtsLevel(ICharm charm) {
    return MartialArtsUtilities.getLevel(charm);
  }

  @Override
  public boolean swallowedLotus() {
    return character.getCharms().isLearned("Solar.SwallowingTheLotusRoot");
  }
}