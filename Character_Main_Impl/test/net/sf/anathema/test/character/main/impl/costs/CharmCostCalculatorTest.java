package net.sf.anathema.test.character.main.impl.costs;

import net.sf.anathema.character.dummy.trait.DummyCoreTraitConfiguration;
import net.sf.anathema.character.generic.dummy.DummyAdditionalBonusPointManagment;
import net.sf.anathema.character.generic.dummy.DummyAdditionalSpellPointManagement;
import net.sf.anathema.character.generic.dummy.DummyGenericCharacter;
import net.sf.anathema.character.generic.dummy.magic.DummySpell;
import net.sf.anathema.character.generic.dummy.magic.DummySpellConfiguration;
import net.sf.anathema.character.generic.dummy.template.DummyCharacterTemplate;
import net.sf.anathema.character.generic.impl.template.points.DefaultBonusPointCosts;
import net.sf.anathema.character.generic.magic.ISpell;
import net.sf.anathema.character.generic.template.creation.IBonusPointCosts;
import net.sf.anathema.character.generic.traits.types.AbilityType;
import net.sf.anathema.character.impl.model.ExaltedCharacter;
import net.sf.anathema.character.impl.model.creation.bonus.magic.MagicCostCalculator;
import net.sf.anathema.character.library.trait.favorable.FavorableState;
import net.sf.anathema.character.model.ICharacter;
import net.sf.anathema.character.model.ISpellConfiguration;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class CharmCostCalculatorTest extends AbstractBonusPointTestCase {

  private MagicCostCalculator calculator;
  private ICharacter character;
  private ISpellConfiguration spells;
  private DummyCoreTraitConfiguration traitConfiguration;

  @Before
  public void setUp() throws Exception {
    spells = new DummySpellConfiguration();
    traitConfiguration = new DummyCoreTraitConfiguration();
    addAbilityAndEssence(traitConfiguration);
    IBonusPointCosts cost = new DefaultBonusPointCosts();
    DummyGenericCharacter genericCharacter = new DummyGenericCharacter(new DummyCharacterTemplate());
    character = new ExaltedCharacter(new DummyCharacterTemplate(), null);
    calculator = new MagicCostCalculator(
      genericCharacter.getTemplate().getMagicTemplate(),
      character,
      2,
      3,
      cost,
      new DummyAdditionalBonusPointManagment(),
      new DummyAdditionalSpellPointManagement());
  }

  @Test
  public void testNoSpellsLearned() {
    assertEquals(0, calculator.getGeneralCharmPicksSpent());
    assertEquals(0, calculator.getFavoredCharmPicksSpent());
    assertEquals(0, calculator.getBonusPointsSpentForCharms());
    assertEquals(0, calculator.getBonusPointsSpentForSpells());
  }

  @Test
  public void testOneSpellLearnedOccultUnfavored() {
    spells.addSpells(new ISpell[]{new DummySpell()});
    calculator.calculateMagicCosts();
    assertEquals(1, calculator.getGeneralCharmPicksSpent());
    assertEquals(0, calculator.getFavoredCharmPicksSpent());
    assertEquals(0, calculator.getBonusPointsSpentForCharms());
    assertEquals(0, calculator.getBonusPointsSpentForSpells());
  }

  @Test
  public void testOneSpellLearnedOccultFavored() {
    setOccultFavored();
    spells.addSpells(new ISpell[]{new DummySpell()});
    calculator.calculateMagicCosts();
    assertEquals(0, calculator.getGeneralCharmPicksSpent());
    assertEquals(1, calculator.getFavoredCharmPicksSpent());
    assertEquals(0, calculator.getBonusPointsSpentForCharms());
    assertEquals(0, calculator.getBonusPointsSpentForSpells());
  }

  private void setOccultFavored() {
    traitConfiguration.getFavorableTrait(AbilityType.Occult).getFavorization().setFavorableState(FavorableState.Favored);
  }

  @Test
  public void testUnfavoredSpellsOverflowToBonus() {
    DummySpell dummySpell = new DummySpell();
    spells.addSpells(new ISpell[]{dummySpell, dummySpell, dummySpell, dummySpell});
    calculator.calculateMagicCosts();
    assertEquals(3, calculator.getGeneralCharmPicksSpent());
    assertEquals(0, calculator.getFavoredCharmPicksSpent());
    assertEquals(0, calculator.getBonusPointsSpentForCharms());
    assertEquals(5, calculator.getBonusPointsSpentForSpells());
  }

  @Test
  public void testUnfavoredSpellsOverflowToBonusAndAreReset() {
    DummySpell dummySpell = new DummySpell();
    DummySpell dummySpellToRemove = new DummySpell();
    spells.addSpells(new ISpell[]{dummySpell, dummySpell, dummySpell, dummySpellToRemove});
    calculator.calculateMagicCosts();
    assertEquals(3, calculator.getGeneralCharmPicksSpent());
    assertEquals(0, calculator.getFavoredCharmPicksSpent());
    assertEquals(0, calculator.getBonusPointsSpentForCharms());
    assertEquals(5, calculator.getBonusPointsSpentForSpells());
    spells.removeSpells(new ISpell[]{dummySpellToRemove}, false);
    calculator.calculateMagicCosts();
    assertEquals(3, calculator.getGeneralCharmPicksSpent());
    assertEquals(0, calculator.getBonusPointsSpentForSpells());
  }

  @Test
  public void testFavoredSpellsOverflowToGeneralAndBonus() {
    setOccultFavored();
    DummySpell dummySpell = new DummySpell();
    spells.addSpells(new ISpell[]{dummySpell, dummySpell, dummySpell, dummySpell, dummySpell, dummySpell,});
    calculator.calculateMagicCosts();
    assertEquals(3, calculator.getGeneralCharmPicksSpent());
    assertEquals(2, calculator.getFavoredCharmPicksSpent());
    assertEquals(0, calculator.getBonusPointsSpentForCharms());
    assertEquals(4, calculator.getBonusPointsSpentForSpells());
  }
}