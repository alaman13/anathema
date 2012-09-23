package net.sf.anathema.character.sidereal.colleges.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import net.sf.anathema.character.generic.additionaltemplate.IAdditionalModelBonusPointCalculator;
import net.sf.anathema.character.generic.template.points.IFavorableTraitCreationPoints;
import net.sf.anathema.character.generic.traits.IGenericTrait;
import net.sf.anathema.character.library.trait.IAdditionalTraitBonusPointManagement;
import net.sf.anathema.character.library.trait.IFavorableDefaultTrait;
import net.sf.anathema.character.sidereal.colleges.presenter.IAstrologicalHouse;

public class CollegeModelBonusPointCalculator implements IAdditionalModelBonusPointCalculator {

  private final CollegeBonusPointCalculator calculator;

  public CollegeModelBonusPointCalculator(
      IAstrologicalHouse[] allHouses,
      ICollegeBonusPointCosts costs,
      IFavorableTraitCreationPoints points) {
    List<IFavorableDefaultTrait> colleges = new ArrayList<>();
    for (IAstrologicalHouse house : allHouses) {
      Collections.addAll(colleges, house.getColleges());
    }
    this.calculator = new CollegeBonusPointCalculator(new IAdditionalTraitBonusPointManagement() {
      @Override
      public void spendOn(IGenericTrait trait, int bonusCost) {
        // nothing to do
      }
    }, points, colleges.toArray(new IFavorableDefaultTrait[colleges.size()]), costs);
  }

  @Override
  public void recalculate() {
    calculator.calculateCosts();
  }

  @Override
  public int getBonusPointsGranted() {
    return 0;
  }

  @Override
  public int getBonusPointCost() {
    return calculator.getBonusPointsSpent();
  }

  public int getFavoredDotsSpent() {
    return calculator.getFreePointsSpent(true);
  }

  public int getGeneralDotsSpent() {
    return calculator.getFreePointsSpent(false);
  }
}