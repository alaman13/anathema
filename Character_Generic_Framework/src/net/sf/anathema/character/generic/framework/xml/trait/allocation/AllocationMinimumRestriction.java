package net.sf.anathema.character.generic.framework.xml.trait.allocation;

import net.sf.anathema.character.generic.character.ILimitationContext;
import net.sf.anathema.character.generic.framework.xml.trait.IMinimumRestriction;
import net.sf.anathema.character.generic.traits.ITraitType;
import net.sf.anathema.lib.lang.ReflectionEqualsObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AllocationMinimumRestriction extends ReflectionEqualsObject implements IMinimumRestriction
{
  private final Map<ILimitationContext,Map<ITraitType, Integer>> claimMap = new HashMap<>();
  private final List<AllocationMinimumRestriction> siblings;
  private final int dotCount;
  private int strictMinimumValue = 0;
  private final List<ITraitType> alternateTraitTypes = new ArrayList<>();
  private ILimitationContext latestContext = null;
  private ITraitType latestTrait = null;
  private boolean isFreebie;

  public AllocationMinimumRestriction(int dotCount, List<AllocationMinimumRestriction> siblings) {
    this.dotCount = dotCount;
    this.siblings = siblings;
  }

  @Override
  public boolean isFullfilledWithout(ILimitationContext context, ITraitType traitType) {
    int remainingDots = dotCount;
    latestContext = context;
    latestTrait = traitType;
    for (ITraitType type : alternateTraitTypes)
      if (type != traitType)
      {
    	  int currentDots = context.getTraitCollection().getTrait(type).getCurrentValue();
    	  int externalDots = getExternalClaims(context, type);
    	  int claimedDots = Math.max(currentDots - externalDots, 0);
    	  claimedDots = Math.min(claimedDots, remainingDots);
    	  claimDots(context, type, claimedDots);
    	  remainingDots -= claimedDots;
      }
    strictMinimumValue = remainingDots;
    return remainingDots == 0;
  }
  
  @Override
  public int getCalculationMinValue(ILimitationContext context, ITraitType traitType)
  {
	  if (!isFreebie)
		  return 0;
	  int traitDots = 0;
	  int remainingDots = dotCount;
	  for (ITraitType type : alternateTraitTypes)
      {
    	  int currentDots = context.getTraitCollection().getTrait(type).getCurrentValue();
    	  int externalDots = getExternalClaims(context, type);
    	  int claimedDots = Math.max(currentDots - externalDots, 0);
    	  claimedDots = Math.min(claimedDots, remainingDots);
    	  claimDots(context, type, claimedDots);
    	  remainingDots -= claimedDots;
    	  
    	  if (type == traitType)
    		  traitDots = claimedDots;
      }
      return traitDots + getExternalClaims(context, traitType);
  }
  
  @Override
  public void setIsFreebie(boolean value)
  {
	  isFreebie = value;
  }
  
  private void claimDots(ILimitationContext context, ITraitType type, int dots)
  {
	  Map<ITraitType, Integer> map = claimMap.get(context);
	  if (map == null)
	  {
		  map = new HashMap<>();
		  claimMap.put(context, map);
	  }
	  map.put(type, dots);
  }

  private int getExternalClaims(ILimitationContext context, ITraitType traitType)
  {
	  int claimed = 0;
	  for (AllocationMinimumRestriction sibling : siblings)
	  {
		  if (sibling == this)
			  continue;
		  try
		  {
			  Map<ITraitType, Integer> map = sibling.claimMap.get(context);
			  claimed += map.get(traitType);
		  }
		  catch (NullPointerException ignored) { }
	  }
	  return claimed;
  }

  @Override
  public void clear()
  {
	  claimMap.clear();
	  for (AllocationMinimumRestriction sibling : siblings)
		  sibling.claimMap.clear();
  }

  @Override
  public void addTraitType(ITraitType traitType) {
    alternateTraitTypes.add(traitType);
  }

  @Override
  public int getStrictMinimumValue()
  {
	  claimDots(latestContext, latestTrait, strictMinimumValue);
	  return strictMinimumValue + getExternalClaims(latestContext, latestTrait);
  }
  
  public String toString()
  {
	  return "{" + dotCount + ";" + alternateTraitTypes + "}";
  }
}