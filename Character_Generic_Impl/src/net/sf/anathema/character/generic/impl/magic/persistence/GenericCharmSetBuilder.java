package net.sf.anathema.character.generic.impl.magic.persistence;

import net.sf.anathema.character.generic.impl.magic.Charm;
import net.sf.anathema.character.generic.impl.magic.persistence.builder.GenericComboRulesBuilder;
import net.sf.anathema.character.generic.impl.magic.persistence.builder.GenericIdStringBuilder;
import net.sf.anathema.character.generic.impl.magic.persistence.builder.prerequisite.GenericAttributeRequirementBuilder;
import net.sf.anathema.character.generic.impl.magic.persistence.builder.prerequisite.GenericTraitPrerequisitesBuilder;
import net.sf.anathema.character.generic.magic.charms.special.ISpecialCharm;
import net.sf.anathema.character.generic.traits.ITraitType;
import net.sf.anathema.lib.exception.PersistenceException;
import net.sf.anathema.lib.xml.ElementUtilities;
import org.dom4j.Element;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static net.sf.anathema.character.generic.impl.magic.ICharmXMLConstants.ATTRIB_ID;
import static net.sf.anathema.character.generic.impl.magic.ICharmXMLConstants.TAG_GENERIC_CHARM;
import static net.sf.anathema.character.generic.impl.magic.ICharmXMLConstants.TAG_GENERIC_TRAIT;
import static net.sf.anathema.character.generic.impl.magic.ICharmXMLConstants.TAG_GENERIC_TRAIT_SET;

public class GenericCharmSetBuilder extends AbstractCharmSetBuilder {
  private final GenericCharmBuilder genericsBuilder = new GenericCharmBuilder(
      new GenericIdStringBuilder(),
      new GenericTraitPrerequisitesBuilder(),
      new GenericAttributeRequirementBuilder(),
      new GenericComboRulesBuilder(),
      new GenericCharmPrerequisiteBuilder());
  private ITraitType[] types;

  @Override
  protected void buildCharms(Collection<Charm> allCharms, List<ISpecialCharm> specialCharms, Element charmListElement) throws PersistenceException {
    List<Element> elements = ElementUtilities.elements(charmListElement, TAG_GENERIC_CHARM);
    Map<Element, Set<String>> traitSets = new HashMap<>();
    if (elements.isEmpty()) {
      return;
    }
    for (Element charmElementObject : elements) {
      List<Element> traitSetElements = ElementUtilities.elements(charmElementObject, TAG_GENERIC_TRAIT_SET);
      if (traitSetElements.isEmpty()) {
        continue;
      }
      Set<String> traits = new HashSet<>();
      for (Element traitSetObject : traitSetElements) {
        for (Element traitObject : ElementUtilities.elements(traitSetObject, TAG_GENERIC_TRAIT)) {
          traits.add(ElementUtilities.getRequiredAttrib(traitObject, ATTRIB_ID));
        }
      }
      traitSets.put(charmElementObject, traits);
    }
    for (ITraitType type : types) {
      genericsBuilder.setType(type);
      for (Element charmElementObject : elements) {
        if (!traitSets.containsKey(charmElementObject) || traitSets.get(charmElementObject).contains(type.getId())) {
          createCharm(allCharms, specialCharms, genericsBuilder, charmElementObject); 
        }
      }
    }
  }

  public void setTypes(ITraitType[] types) {
    this.types = types;
  }
}