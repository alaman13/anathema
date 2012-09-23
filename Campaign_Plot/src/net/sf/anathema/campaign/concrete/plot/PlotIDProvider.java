package net.sf.anathema.campaign.concrete.plot;

import java.util.HashMap;
import java.util.Map;

import net.sf.anathema.campaign.model.plot.IPlotTimeUnit;

public class PlotIDProvider {

  private final Map<IPlotTimeUnit, Integer> timeUnitCount = new HashMap<>();

  public PlotIDProvider(IPlotTimeUnit rootUnit) {
    IPlotTimeUnit unit = rootUnit;
    registerTimeUnit(unit);
    while (unit.hasSuccessor()) {
      unit = unit.getSuccessor();
      registerTimeUnit(unit);
    }
  }

  private void registerTimeUnit(IPlotTimeUnit timeUnit) {
    if (!timeUnitCount.containsKey(timeUnit)) {
      registerTimeUnit(timeUnit, 0);
    }
  }

  private void registerTimeUnit(IPlotTimeUnit timeUnit, int initialCount) {
    timeUnitCount.put(timeUnit, initialCount);
  }

  public void setIDNumberUsed(IPlotTimeUnit timeUnit, int idNumber) {
    Integer integer = timeUnitCount.get(timeUnit);
    registerTimeUnit(timeUnit, Math.max(idNumber + 1, integer));
  }

  public Integer getIDNumber(IPlotTimeUnit timeUnit) {
    Integer integer = timeUnitCount.get(timeUnit);
    timeUnitCount.put(timeUnit, integer + 1);
    return integer;
  }
}