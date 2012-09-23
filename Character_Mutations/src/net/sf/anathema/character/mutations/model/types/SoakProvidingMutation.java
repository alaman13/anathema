package net.sf.anathema.character.mutations.model.types;

import net.sf.anathema.character.generic.health.HealthType;
import net.sf.anathema.character.generic.health.IHealthTypeVisitor;
import net.sf.anathema.character.library.quality.model.QualityPrerequisite;
import net.sf.anathema.character.library.quality.presenter.IQuality;
import net.sf.anathema.character.library.quality.presenter.IQualityPredicate;
import net.sf.anathema.character.mutations.model.IMutationVisitor;
import net.sf.anathema.character.mutations.model.Mutation;

import java.util.ArrayList;
import java.util.List;

public class SoakProvidingMutation extends Mutation {

  private final int bonus;
  private boolean replacesPrerequisites;

  public SoakProvidingMutation(String id, int bonus, boolean replacesPrerequisites) {
    super(id);
    this.replacesPrerequisites = replacesPrerequisites;
    this.bonus = bonus;
  }

  public int getBonus() {
    return bonus;
  }

  @Override
  public void accept(IMutationVisitor visitor) {
    visitor.acceptSoakProvidingMutation(this);
  }

  public void adjustActiveMutationList(List<SoakProvidingMutation> giftList) {
    List<SoakProvidingMutation> cloneList = new ArrayList<>(giftList);
    if (replacesPrerequisites) {
      for (SoakProvidingMutation mutation : cloneList) {
        if (isPrerequisite(mutation)) {
          giftList.remove(mutation);
        }
      }
    }
    for (SoakProvidingMutation mutation : giftList) {
      if (mutation.replacesPrerequisites && mutation.isPrerequisite(this)) {
        return;
      }
    }
    giftList.add(this);
  }

  private boolean isPrerequisite(SoakProvidingMutation mutation) {
    List<IQualityPredicate> prerequisiteList = getPrerequisiteList();
    for (IQualityPredicate predicate : prerequisiteList) {
      if (predicate instanceof QualityPrerequisite) {
        IQuality[] prerequisiteQualities = ((QualityPrerequisite) predicate).getPrerequisiteQualities();
        return net.sf.anathema.lib.lang.ArrayUtilities.containsValue(prerequisiteQualities, mutation);
      }
    }
    return false;
  }

  public float getSoakStaminaModifier(HealthType type) {
    final float[] modifier = new float[1];
    type.accept(new IHealthTypeVisitor() {
      @Override
      public void visitBashing(HealthType bashing) {
        modifier[0] = 1;
      }

      @Override
      public void visitLethal(HealthType lethal) {
        modifier[0] = 1;
      }

      @Override
      public void visitAggravated(HealthType aggravated) {
        throw new UnsupportedOperationException();
      }
    });
    return modifier[0];
  }
}
