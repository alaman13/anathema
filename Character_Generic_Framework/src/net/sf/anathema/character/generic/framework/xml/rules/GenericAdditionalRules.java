package net.sf.anathema.character.generic.framework.xml.rules;

import com.google.common.base.Functions;
import net.sf.anathema.character.generic.additionalrules.IAdditionalEssencePool;
import net.sf.anathema.character.generic.additionalrules.IAdditionalMagicLearnPool;
import net.sf.anathema.character.generic.additionalrules.ITraitCostModifier;
import net.sf.anathema.character.generic.backgrounds.IBackgroundTemplate;
import net.sf.anathema.character.generic.impl.additional.DefaultTraitCostModifier;
import net.sf.anathema.character.generic.impl.additional.NullAdditionalRules;
import net.sf.anathema.character.generic.traits.ITraitType;
import net.sf.anathema.lib.exception.UnreachableCodeReachedException;
import net.sf.anathema.lib.lang.clone.ICloneable;

import java.util.HashMap;
import java.util.Map;

public class GenericAdditionalRules extends NullAdditionalRules implements ICloneable<GenericAdditionalRules> {

  private boolean revisedIntimacies = false;
  private boolean willpowerVirtueBased = true;
  private String[] compulsiveCharmIds = new String[0];
  private String[] rejectedBackgroundIds = new String[0];
  private IAdditionalEssencePool[] essencePools = new IAdditionalEssencePool[0];
  private IAdditionalMagicLearnPool[] magicPools = new IAdditionalMagicLearnPool[0];
  private Map<String, ITraitCostModifier> backgroundCostModifiers;

  public GenericAdditionalRules() {
    backgroundCostModifiers = createBackgroundCostModifierMap();
  }

  public void setCompulsiveCharmIds(String[] compulsiveCharmIds) {
    this.compulsiveCharmIds = compulsiveCharmIds;
  }

  public void addCompulsiveCharmIds(String[] compulsiveCharmIds) {
    this.compulsiveCharmIds = net.sf.anathema.lib.lang.ArrayUtilities.concat(String.class, this.compulsiveCharmIds, compulsiveCharmIds);
  }

  @Override
  public String[] getCompulsiveCharmIDs() {
    return compulsiveCharmIds;
  }

  public void setAdditionalEssencePools(IAdditionalEssencePool[] pools) {
    this.essencePools = pools;
  }

  public void addAdditionalEssencePools(IAdditionalEssencePool[] pools) {
    this.essencePools = net.sf.anathema.lib.lang.ArrayUtilities.concat(IAdditionalEssencePool.class, this.essencePools, pools);
  }

  @Override
  public IAdditionalEssencePool[] getAdditionalEssencePools() {
    return essencePools;
  }

  @Override
  public IAdditionalMagicLearnPool[] getAdditionalMagicLearnPools() {
    return magicPools;
  }

  public void setMagicPools(IAdditionalMagicLearnPool[] magicPools) {
    this.magicPools = magicPools;
  }

  public void addMagicPools(IAdditionalMagicLearnPool[] magicPools) {
    this.magicPools = net.sf.anathema.lib.lang.ArrayUtilities.concat(IAdditionalMagicLearnPool.class, this.magicPools, magicPools);
  }

  @Override
  public boolean isRejected(IBackgroundTemplate backgroundTemplate) {
    return net.sf.anathema.lib.lang.ArrayUtilities.containsValue(rejectedBackgroundIds, backgroundTemplate.getId());
  }

  public void setRejectedBackgrounds(String[] backgroundIds) {
    this.rejectedBackgroundIds = backgroundIds;
  }

  public void addRejectedBackgrounds(String[] backgroundIds) {
    this.rejectedBackgroundIds = net.sf.anathema.lib.lang.ArrayUtilities.concat(String.class, this.rejectedBackgroundIds, rejectedBackgroundIds);
  }

  @Override
  public boolean isRevisedIntimacies() {
    return revisedIntimacies;
  }

  public void setRevisedIntimacies(boolean revisedIntimacies) {
    this.revisedIntimacies = revisedIntimacies;
  }

  @Override
  public boolean isWillpowerVirtueBased() {
    return willpowerVirtueBased;
  }

  public void setWillpowerVirtueBased(boolean willpowerVirtueBased) {
    this.willpowerVirtueBased = willpowerVirtueBased;
  }

  @Override
  public ITraitCostModifier getCostModifier(ITraitType type) {
    final ITraitCostModifier[] modifier = new ITraitCostModifier[1];
    type.accept(new TraitTypeAdapter() {
      @Override
      public void visitBackground(IBackgroundTemplate template) {
        modifier[0] = Functions.forMap(backgroundCostModifiers, new DefaultTraitCostModifier()).apply(template.getId());
      }
    });
    return modifier[0];
  }

  public void addBackgroundCostModifier(String backgroundId, ITraitCostModifier modifier) {
    backgroundCostModifiers.put(backgroundId, modifier);
  }

  @Override
  public GenericAdditionalRules clone() {
    try {
      GenericAdditionalRules clonedRules;
      clonedRules = (GenericAdditionalRules) super.clone();
      clonedRules.backgroundCostModifiers = createBackgroundCostModifierMap();
      clonedRules.backgroundCostModifiers.putAll(backgroundCostModifiers);
      return clonedRules;
    } catch (CloneNotSupportedException e) {
      throw new UnreachableCodeReachedException();
    }
  }

  private Map<String, ITraitCostModifier> createBackgroundCostModifierMap() {
    return new HashMap<>();
  }
}