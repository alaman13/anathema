package net.sf.anathema.character.library.trait;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.sf.anathema.character.generic.template.points.IFavorableTraitCreationPoints;
import net.sf.anathema.character.library.trait.favorable.IFavorableTrait;
import net.sf.anathema.character.library.trait.subtrait.ISubTrait;
import net.sf.anathema.character.library.trait.visitor.IAggregatedTrait;
import net.sf.anathema.character.library.trait.visitor.IDefaultTrait;
import net.sf.anathema.character.library.trait.visitor.ITraitVisitor;

public abstract class AbstractFavorableTraitCostCalculator implements IFavorableTraitCostCalculator {

  protected final IFavorableTraitCreationPoints points;
  private final Map<IFavorableTrait, FavorableTraitCost[]> costsByTrait = new HashMap<>();
  private final IAdditionalTraitBonusPointManagement additionalPools;
  private final int freeTraitMax;
  private final IFavorableTrait[] traits;
  private int favoredPicksSpent = 0;
  private int favoredDotSum = 0;
  private int generalDotSum = 0;
  private int extraFavoredDotSum = 0;
  private int extraGenericDotSum = 0;

  public AbstractFavorableTraitCostCalculator(
      IAdditionalTraitBonusPointManagement additionalPools,
      IFavorableTraitCreationPoints points,
      int freeTraitMax,
      IFavorableTrait[] traits) {
    this.additionalPools = additionalPools;
    this.points = points;
    this.freeTraitMax = freeTraitMax;
    this.traits = traits;
  }

  @Override
  public void calculateCosts() {
    clear();
    countFavoredTraits();
    Set<IFavorableDefaultTrait> sortedTraits = sortTraitsByStatus();
    for (IFavorableDefaultTrait trait : sortedTraits) {
      int costFactor = getCostFactor(trait);
      FavorableTraitCost[] allCosts;
      if (trait.getFavorization().isCasteOrFavored()) {
        allCosts = handleFavoredTrait(trait, costFactor);
      }
      else {
        allCosts = handleGeneralTrait(trait, costFactor);
      }
      for (FavorableTraitCost cost : allCosts) {
        additionalPools.spendOn(trait, cost.getBonusCost());
      }
      costsByTrait.put(trait, allCosts);
    }
  }

  protected void countFavoredTraits() {
    for (IFavorableTrait trait : traits) {
      if (trait.getFavorization().isFavored()) {
        increaseFavoredPicksSpent();
      }
    }
  }

  protected void clear() {
    favoredPicksSpent = 0;
    favoredDotSum = 0;
    extraFavoredDotSum = 0;
    extraGenericDotSum = 0;
    generalDotSum = 0;
    costsByTrait.clear();
  }
  
  @Override
  public int getBonusPointsSpent() {
    int bonusPointSum = 0;
    for (FavorableTraitCost[] allCosts : costsByTrait.values()) {
      for (FavorableTraitCost cost : allCosts) {
        bonusPointSum += cost.getBonusCost();
      }
    }
    return bonusPointSum;
  }

  protected abstract int getCostFactor(IFavorableDefaultTrait trait);

  @Override
  public FavorableTraitCost[] getCosts(IFavorableTrait trait) {
    return costsByTrait.get(trait);
  }

  private int getDefaultDotCount() {
    return points.getDefaultDotCount();
  }

  private int getFavoredDotCount() {
    return points.getFavoredDotCount();
  }
  
  protected int getExtraFavoredDotCount()
  {
	return points.getExtraFavoredDotCount();
  }
  
  protected int getExtraGenericDotCount()
  {
	return points.getExtraGenericDotCount();
  }

  @Override
  public int getFavoredPicksSpent() {
    return favoredPicksSpent;
  }
  
  @Override
  public int getExtraFavoredDotsSpent()
  {
	  return extraFavoredDotSum;
  }
  
  @Override
  public int getExtraGenericDotsSpent()
  {
	  return extraGenericDotSum;
  }

  @Override
  public int getFreePointsSpent(boolean favored) {
    return favored ? favoredDotSum : generalDotSum;
  }

  protected IFavorableTrait[] getTraits() {
    return traits;
  }
  
  private FavorableTraitCost handleFavoredSingleTrait(IDefaultTrait trait, int bonusPointCostFactor) {
	int freeTraitMax = Math.max(this.freeTraitMax, trait.getAbsoluteMinValue());
    int freePointsToAdd = Math.min(trait.getCalculationValue(), freeTraitMax) - trait.getCalculationMinValue();
    int favoredDotsSpent = 0;
    int generalDotsSpent = 0;
    int bonusPointsSpent = 0;
    if (getFreePointsSpent(true) < getFavoredDotCount()) {
      int remainingFavoredPoints = getFavoredDotCount() - getFreePointsSpent(true);
      favoredDotsSpent = Math.min(remainingFavoredPoints, freePointsToAdd);
      increaseFavoredDotSum(favoredDotsSpent);
      freePointsToAdd -= favoredDotsSpent;
    }
    if (freePointsToAdd > 0)
    {
      if (getFreePointsSpent(false) < getDefaultDotCount()) {
        int remainingGeneralPoints = getDefaultDotCount() - getFreePointsSpent(false);
        generalDotsSpent = Math.min(remainingGeneralPoints, freePointsToAdd);
        increaseGeneralDotSum(generalDotsSpent);
        freePointsToAdd -= generalDotsSpent;
      }
    }
    if (freePointsToAdd > 0)
    {
      if (getFreePointsSpent(false) < getDefaultDotCount()) {
        int remainingGeneralPoints = getDefaultDotCount() - getFreePointsSpent(false);
        generalDotsSpent = Math.min(remainingGeneralPoints, freePointsToAdd);
        increaseGeneralDotSum(generalDotsSpent);
        freePointsToAdd -= generalDotsSpent;
      }
    }
    if (freePointsToAdd > 0) {
      bonusPointsSpent += freePointsToAdd * bonusPointCostFactor;
    }
    bonusPointsSpent += Math.max(trait.getCalculationValue() - freeTraitMax, 0) * bonusPointCostFactor;
    return new FavorableTraitCost(bonusPointsSpent, generalDotsSpent, favoredDotsSpent);
  }

  private FavorableTraitCost[] handleFavoredTrait(IFavorableTrait trait, final int bonusPointCostFactor) {
    final List<FavorableTraitCost> allCosts = new ArrayList<>();
    trait.accept(new ITraitVisitor() {

      @Override
      public void visitAggregatedTrait(IAggregatedTrait visitedTrait) {
        for (ISubTrait subTrait : visitedTrait.getSubTraits().getSubTraits()) {
          allCosts.add(handleFavoredSingleTrait(subTrait, bonusPointCostFactor));
        }
      }

      @Override
      public void visitDefaultTrait(IDefaultTrait visitedTrait) {
        allCosts.add(handleFavoredSingleTrait(visitedTrait, bonusPointCostFactor));
      }
    });
    return allCosts.toArray(new FavorableTraitCost[allCosts.size()]);
  }

  private FavorableTraitCost handleGeneralSingleTrait(IDefaultTrait trait, int bonusPointCostFactor) {
	int freeTraitMax = Math.max(this.freeTraitMax, trait.getAbsoluteMinValue());
	int freePointsToAdd = Math.min(trait.getCalculationValue(), freeTraitMax) - trait.getCalculationMinValue();
    int generalDotsSpent = 0;
    int bonusPointsSpent = 0;
    if (getFreePointsSpent(false) < getDefaultDotCount()) {
      int remainingGeneralPoints = getDefaultDotCount() - getFreePointsSpent(false);
      generalDotsSpent = Math.min(remainingGeneralPoints, freePointsToAdd);
      
      increaseGeneralDotSum(generalDotsSpent);
      freePointsToAdd -= generalDotsSpent;
    }
    if (freePointsToAdd > 0) {
      bonusPointsSpent += freePointsToAdd * bonusPointCostFactor;
    }
    bonusPointsSpent += Math.max(trait.getCalculationValue() - freeTraitMax, 0) * bonusPointCostFactor;
    return new FavorableTraitCost(bonusPointsSpent, generalDotsSpent, 0);
  }

  private FavorableTraitCost[] handleGeneralTrait(ITrait trait, final int bonusPointCostFactor) {
    final List<FavorableTraitCost> allCosts = new ArrayList<>();
    trait.accept(new ITraitVisitor() {

      @Override
      public void visitAggregatedTrait(IAggregatedTrait visitedTrait) {
        for (ISubTrait subTrait : visitedTrait.getSubTraits().getSubTraits()) {
          allCosts.add(handleGeneralSingleTrait(subTrait, bonusPointCostFactor));
        }
      }

      @Override
      public void visitDefaultTrait(IDefaultTrait visitedTrait) {
        allCosts.add(handleGeneralSingleTrait(visitedTrait, bonusPointCostFactor));
      }
    });
    return allCosts.toArray(new FavorableTraitCost[allCosts.size()]);
  }
  
  protected void increaseExtraFavoredDotSum(int extraDotsSpent)
  {
	  extraFavoredDotSum += extraDotsSpent;
  }
  
  protected void increaseExtraGenericDotSum(int extraDotsSpent)
  {
	  extraGenericDotSum += extraDotsSpent;
  }

  private void increaseFavoredDotSum(int favoredDotsSpent) {
    favoredDotSum += favoredDotsSpent;
  }

  private void increaseFavoredPicksSpent() {
    favoredPicksSpent++;
  }

  private void increaseGeneralDotSum(int generalDotsSpent) {
	  if (generalDotsSpent == 0)
		  return;
    generalDotSum += generalDotsSpent;
  }

  private Set<IFavorableDefaultTrait> sortTraitsByStatus() {
    Set<IFavorableDefaultTrait> orderedTraits = new LinkedHashSet<>();
    for (IFavorableTrait trait : traits) {
      if (!trait.getFavorization().isCasteOrFavored()) {
        addAllTraits(orderedTraits, trait);
      }
    }
    for (IFavorableTrait trait : traits) {
      if (!orderedTraits.contains(trait)) {
        addAllTraits(orderedTraits, trait);
      }
    }
    return orderedTraits;
  }

  private void addAllTraits(final Set<IFavorableDefaultTrait> orderedTraits, IFavorableTrait trait) {
    trait.accept(new ITraitVisitor() {
      @Override
      public void visitAggregatedTrait(IAggregatedTrait visitedTrait) {
        for (ISubTrait subtrait : visitedTrait.getSubTraits().getSubTraits()) {
          orderedTraits.add((IFavorableDefaultTrait) subtrait);
        }
      }

      @Override
      public void visitDefaultTrait(IDefaultTrait visitedTrait) {
        orderedTraits.add((IFavorableDefaultTrait) visitedTrait);
      }
    });
  }
}