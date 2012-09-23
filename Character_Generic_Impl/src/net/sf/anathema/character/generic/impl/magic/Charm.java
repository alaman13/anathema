package net.sf.anathema.character.generic.impl.magic;

import com.google.common.base.Preconditions;
import net.sf.anathema.character.generic.IBasicCharacterData;
import net.sf.anathema.character.generic.character.IGenericTraitCollection;
import net.sf.anathema.character.generic.character.IMagicCollection;
import net.sf.anathema.character.generic.impl.magic.charm.prerequisite.CompositeLearnWorker;
import net.sf.anathema.character.generic.impl.magic.charm.prerequisite.ICharmLearnWorker;
import net.sf.anathema.character.generic.impl.magic.charm.prerequisite.SelectiveCharmGroup;
import net.sf.anathema.character.generic.impl.magic.persistence.prerequisite.CharmPrerequisiteList;
import net.sf.anathema.character.generic.impl.magic.persistence.prerequisite.SelectiveCharmGroupTemplate;
import net.sf.anathema.character.generic.magic.ICharm;
import net.sf.anathema.character.generic.magic.ICharmData;
import net.sf.anathema.character.generic.magic.IMagicVisitor;
import net.sf.anathema.character.generic.magic.charms.ComboRestrictions;
import net.sf.anathema.character.generic.magic.charms.ICharmAttribute;
import net.sf.anathema.character.generic.magic.charms.ICharmLearnArbitrator;
import net.sf.anathema.character.generic.magic.charms.IComboRestrictions;
import net.sf.anathema.character.generic.magic.charms.IndirectCharmRequirement;
import net.sf.anathema.character.generic.magic.charms.duration.IDuration;
import net.sf.anathema.character.generic.magic.charms.type.ICharmTypeModel;
import net.sf.anathema.character.generic.magic.general.ICostList;
import net.sf.anathema.character.generic.rules.IExaltedSourceBook;
import net.sf.anathema.character.generic.template.magic.FavoringTraitType;
import net.sf.anathema.character.generic.template.magic.IFavoringTraitTypeVisitor;
import net.sf.anathema.character.generic.traits.IFavorableGenericTrait;
import net.sf.anathema.character.generic.traits.IGenericTrait;
import net.sf.anathema.character.generic.traits.ITraitType;
import net.sf.anathema.character.generic.traits.types.AbilityType;
import net.sf.anathema.character.generic.traits.types.AttributeType;
import net.sf.anathema.character.generic.traits.types.OtherTraitType;
import net.sf.anathema.character.generic.traits.types.YoziType;
import net.sf.anathema.character.generic.type.ICharacterType;
import net.sf.anathema.lib.util.Identificate;
import net.sf.anathema.lib.util.Identified;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class Charm extends Identificate implements ICharm {

  private final CharmPrerequisiteList prerequisisteList;

  private final ICharacterType characterType;
  private final IComboRestrictions comboRules;
  private final IDuration duration;
  private final String group;
  private final boolean isGeneric;

  private final IExaltedSourceBook[] sources;
  private final ICostList temporaryCost;

  private final List<Set<ICharm>> alternatives = new ArrayList<>();
  private final List<Set<ICharm>> merges = new ArrayList<>();
  private final List<String> requiredSubeffects = new ArrayList<>();
  private final List<ICharm> parentCharms = new ArrayList<>();
  private final List<Charm> children = new ArrayList<>();
  private final SelectiveCharmGroups selectiveCharmGroups = new SelectiveCharmGroups();
  private final List<ICharmAttribute> charmAttributes = new ArrayList<>();
  private final Set<String> favoredCasteIds = new HashSet<>();

  private final ICharmTypeModel typeModel;

  public Charm(ICharacterType characterType, String id, String group, boolean isGeneric,
               CharmPrerequisiteList prerequisiteList, ICostList temporaryCost, IComboRestrictions comboRules,
               IDuration duration, ICharmTypeModel charmTypeModel, IExaltedSourceBook[] sources) {
    super(id);
    Preconditions.checkNotNull(prerequisiteList);
    Preconditions.checkNotNull(characterType);
    Preconditions.checkNotNull(id);
    Preconditions.checkNotNull(group);
    Preconditions.checkNotNull(temporaryCost);
    Preconditions.checkNotNull(comboRules);
    Preconditions.checkNotNull(duration);
    Preconditions.checkNotNull(charmTypeModel.getCharmType());
    Preconditions.checkNotNull(sources);
    this.characterType = characterType;
    this.group = group;
    this.isGeneric = isGeneric;
    this.prerequisisteList = prerequisiteList;
    this.temporaryCost = temporaryCost;
    this.comboRules = comboRules;
    this.duration = duration;
    this.typeModel = charmTypeModel;
    this.sources = sources;
    for (SelectiveCharmGroupTemplate template : prerequisiteList.getSelectiveCharmGroups()) {
      selectiveCharmGroups.add(new SelectiveCharmGroup(template));
    }
  }

  public Charm(ICharmData charmData) {
    super(charmData.getId());
    this.characterType = charmData.getCharacterType();
    this.isGeneric = charmData.isInstanceOfGenericCharm();
    this.group = charmData.getGroupId();
    this.temporaryCost = charmData.getTemporaryCost();
    this.comboRules = new ComboRestrictions();
    this.duration = charmData.getDuration();
    this.sources = charmData.getSources();
    this.prerequisisteList = new CharmPrerequisiteList(charmData.getPrerequisites(), charmData.getEssence(),
            new String[0], new SelectiveCharmGroupTemplate[0], new IndirectCharmRequirement[0]);
    parentCharms.addAll(charmData.getParentCharms());
    this.typeModel = charmData.getCharmTypeModel();
  }

  public void addCharmAttribute(ICharmAttribute attribute) {
    charmAttributes.add(attribute);
  }

  @Override
  public ICharmTypeModel getCharmTypeModel() {
    return typeModel;
  }

  @Override
  public ICharacterType getCharacterType() {
    return characterType;
  }

  @Override
  public IDuration getDuration() {
    return duration;
  }

  @Override
  public IGenericTrait getEssence() {
    return prerequisisteList.getEssence();
  }

  @Override
  public IGenericTrait[] getPrerequisites() {
    return prerequisisteList.getPrerequisites();
  }

  @Override
  public IExaltedSourceBook[] getSources() {
    return sources.length > 0 ? sources : null;
  }

  @Override
  public IExaltedSourceBook getPrimarySource() {
    return sources.length > 0 ? sources[0] : null;
  }

  @Override
  public ICostList getTemporaryCost() {
    return temporaryCost;
  }

  @Override
  public String getGroupId() {
    return group;
  }

  @Override
  public boolean isInstanceOfGenericCharm() {
    return isGeneric;
  }

  @Override
  public IComboRestrictions getComboRules() {
    return comboRules;
  }

  @Override
  public void accept(IMagicVisitor visitor) {
    visitor.visitCharm(this);
  }

  public void addAlternative(Set<ICharm> alternative) {
    alternatives.add(alternative);
  }

  @Override
  public boolean isBlockedByAlternative(IMagicCollection magicCollection) {
    for (Set<ICharm> alternative : alternatives) {
      for (ICharm charm : alternative) {
        boolean isThis = charm.getId().equals(getId());
        if (!isThis && magicCollection.isLearned(charm)) {
          return true;
        }
      }
    }
    return false;
  }

  public void addMerged(Set<ICharm> merged) {
    if (!merged.isEmpty()) {
      merges.add(merged);
      if (!hasAttribute(ICharmData.MERGED_ATTRIBUTE)) {
        addCharmAttribute(new CharmAttribute(ICharmData.MERGED_ATTRIBUTE.getId(), true));
      }
    }
  }

  @Override
  public Set<ICharm> getMergedCharms() {
    Set<ICharm> mergedCharms = new HashSet<>();
    for (Set<ICharm> merge : merges) {
      mergedCharms.addAll(merge);
    }
    return mergedCharms;
  }

  @Override
  public Set<ICharm> getParentCharms() {
    return new HashSet<>(parentCharms);
  }

  private boolean isSubeffectReference(String id) {
    return id.split("\\.").length == 4;
  }

  private boolean isGenericSubeffectReference(String id) {
    return id.split("\\.").length == 5;
  }

  public void extractParentCharms(Map<String, Charm> charmsById) {
    if (parentCharms.size() > 0) {
      return;
    }
    for (String parentId : prerequisisteList.getParentIDs()) {
      String id = parentId;

      if (isSubeffectReference(parentId)) {
        String[] split = parentId.split("\\.");
        id = split[0] + "." + split[1];
        requiredSubeffects.add(parentId);
      }
      if (isGenericSubeffectReference(parentId)) {
        String[] split = parentId.split("\\.");
        id = split[0] + "." + split[1] + "." + split[4];
        requiredSubeffects.add(parentId);
      }

      Charm parentCharm = charmsById.get(id);
      Preconditions.checkNotNull(parentCharm,
              "Parent Charm " + id + " not defined for " + getId()); //$NON-NLS-1$//$NON-NLS-2$
      parentCharms.add(parentCharm);
      parentCharm.addChild(this);
    }
    for (SelectiveCharmGroup charmGroup : selectiveCharmGroups) {
      charmGroup.extractCharms(charmsById, this);
    }
  }

  public void addChild(Charm child) {
    children.add(child);
  }

  @Override
  public List<String> getParentSubeffects() {
    return requiredSubeffects;
  }

  @Override
  public Set<ICharm> getRenderingPrerequisiteCharms() {
    Set<ICharm> prerequisiteCharms = new HashSet<>();
    prerequisiteCharms.addAll(parentCharms);
    for (SelectiveCharmGroup charmGroup : selectiveCharmGroups.getOpenGroups()) {
      prerequisiteCharms.addAll(Arrays.asList(charmGroup.getAllGroupCharms()));
    }
    return prerequisiteCharms;
  }

  @Override
  public Set<IndirectCharmRequirement> getIndirectRequirements() {
    Set<IndirectCharmRequirement> indirectRequirements = new HashSet<>();
    for (SelectiveCharmGroup charmGroup : selectiveCharmGroups.getCombinedGroups()) {
      GroupedCharmRequirement requirement = new GroupedCharmRequirement(charmGroup);
      indirectRequirements.add(requirement);
    }
    Collections.addAll(indirectRequirements, getAttributeRequirements());
    return indirectRequirements;
  }

  @Override
  public Set<ICharm> getLearnPrerequisitesCharms(ICharmLearnArbitrator learnArbitrator) {
    Set<ICharm> prerequisiteCharms = new HashSet<>();
    for (ICharm charm : getParentCharms()) {
      prerequisiteCharms.addAll(charm.getLearnPrerequisitesCharms(learnArbitrator));
      prerequisiteCharms.add(charm);
    }
    for (SelectiveCharmGroup charmGroup : selectiveCharmGroups) {
      prerequisiteCharms.addAll(charmGroup.getLearnPrerequisitesCharms(learnArbitrator));
    }
    return prerequisiteCharms;
  }

  @Override
  public boolean isTreeRoot() {
    return parentCharms.size() == 0 &&  selectiveCharmGroups.isEmpty() && getAttributeRequirements().length == 0;
  }

  @Override
  public Set<ICharm> getLearnFollowUpCharms(ICharmLearnArbitrator learnArbitrator) {
    CompositeLearnWorker learnWorker = new CompositeLearnWorker(learnArbitrator);
    for (Charm child : children) {
      child.addCharmsToForget(learnWorker);
    }
    return learnWorker.getForgottenCharms();
  }

  @Override
  public Set<ICharm> getLearnChildCharms() {
    return new HashSet<ICharm>(children);
  }

  private void addCharmsToForget(ICharmLearnWorker learnWorker) {
    if (isCharmPrerequisiteListFullfilled(learnWorker)) {
      return;
    }
    learnWorker.forget(this);
    for (Charm child : children) {
      child.addCharmsToForget(learnWorker);
    }
  }

  private boolean isCharmPrerequisiteListFullfilled(ICharmLearnArbitrator learnArbitrator) {
    for (ICharm parent : parentCharms) {
      if (!learnArbitrator.isLearned(parent)) {
        return false;
      }
    }
    for (SelectiveCharmGroup selectiveGroup : selectiveCharmGroups) {
      if (!selectiveGroup.holdsThreshold(learnArbitrator)) {
        return false;
      }
    }
    return true;
  }

  @Override
  public ICharmAttribute[] getAttributes() {
    return charmAttributes.toArray(new ICharmAttribute[charmAttributes.size()]);
  }

  @Override
  public boolean hasAttribute(Identified attribute) {
    return charmAttributes.contains(attribute);
  }

  @Override
  public String getAttributeValue(Identified attribute) {
    int index = charmAttributes.indexOf(attribute);
    if (index < 0) {
      return null;
    } else {
      return charmAttributes.get(index).getValue();
    }
  }

  @Override
  public IndirectCharmRequirement[] getAttributeRequirements() {
    return prerequisisteList.getAttributeRequirements();
  }

  public void addFavoredCasteId(String casteId) {
    favoredCasteIds.add(casteId);
  }

  @Override
  public boolean isFavored(IBasicCharacterData basicCharacter, IGenericTraitCollection traitCollection) {
    boolean specialFavored = favoredCasteIds.contains(basicCharacter.getCasteType().getId());
    if (specialFavored) {
      return true;
    }
    if (getPrerequisites().length <= 0) {
      return false;
    }

    final boolean[] characterCanFavorMagicOfPrimaryType = new boolean[1];
    final ITraitType primaryTraitType = getPrimaryTraitType();
    if (hasAttribute(new Identificate("MartialArts")) && ((IFavorableGenericTrait) traitCollection.getTrait(
            AbilityType.MartialArts)).isCasteOrFavored()) {
      return true;
    }

    basicCharacter.getCharacterType().getFavoringTraitType().accept(new IFavoringTraitTypeVisitor() {
      @Override
      public void visitAbilityType(FavoringTraitType visitedType) {
        characterCanFavorMagicOfPrimaryType[0] = primaryTraitType instanceof AbilityType;
      }

      @Override
      public void visitAttributeType(FavoringTraitType visitedType) {
        characterCanFavorMagicOfPrimaryType[0] = primaryTraitType instanceof AttributeType;
      }

      @Override
      public void visitYoziType(FavoringTraitType visitedType) {
        characterCanFavorMagicOfPrimaryType[0] = primaryTraitType instanceof YoziType;
      }

      @Override
      public void visitVirtueType(FavoringTraitType visitedType) {
        characterCanFavorMagicOfPrimaryType[0] = false;
      }
    });
    if (!characterCanFavorMagicOfPrimaryType[0]) {
      return false;
    }
    IGenericTrait trait = traitCollection.getTrait(primaryTraitType);
    return trait instanceof IFavorableGenericTrait && ((IFavorableGenericTrait) trait).isCasteOrFavored();
  }

  @Override
  public ITraitType getPrimaryTraitType() {
    return getPrerequisites().length == 0 ? OtherTraitType.Essence : getPrerequisites()[0].getType();
  }
}