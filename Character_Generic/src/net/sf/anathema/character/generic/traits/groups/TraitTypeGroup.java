package net.sf.anathema.character.generic.traits.groups;

import net.sf.anathema.character.generic.traits.ITraitType;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class TraitTypeGroup implements ITraitTypeGroup {

  public static ITraitType[] getAllTraitTypes(ITraitTypeGroup... traitTypeGroups) {
    List<ITraitType> traitTypes = new ArrayList<>();
    for (ITraitTypeGroup group : traitTypeGroups) {
      Collections.addAll(traitTypes, group.getAllGroupTypes());
    }
    return traitTypes.toArray(new ITraitType[traitTypes.size()]);
  }

  private final ITraitType[] traitTypes;

  public TraitTypeGroup(ITraitType[] traitTypes) {
    this.traitTypes = traitTypes;
  }

  @Override
  public final ITraitType getById(String typeId) {
    for (ITraitType element : traitTypes) {
      if (element.getId().equals(typeId)) {
        return element;
      }
    }
    throw new IllegalArgumentException("No trait type with found in group with id " + typeId); //$NON-NLS-1$
  }

  @Override
  public final ITraitType[] getAllGroupTypes() {
    return traitTypes;
  }

  @Override
  public final boolean contains(ITraitType traitType) {
    return net.sf.anathema.lib.lang.ArrayUtilities.containsValue(traitTypes, traitType);
  }
}