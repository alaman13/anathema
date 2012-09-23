package net.sf.anathema.character.generic.impl.magic.persistence.builder;

import net.sf.anathema.character.generic.impl.magic.CharmAttribute;
import net.sf.anathema.character.generic.magic.charms.ICharmAttribute;
import net.sf.anathema.character.generic.traits.IGenericTrait;
import net.sf.anathema.lib.xml.ElementUtilities;
import org.dom4j.Element;

import java.util.ArrayList;
import java.util.List;

import static net.sf.anathema.character.generic.impl.magic.ICharmXMLConstants.ATTRIB_ATTRIBUTE;
import static net.sf.anathema.character.generic.impl.magic.ICharmXMLConstants.ATTRIB_VALUE;
import static net.sf.anathema.character.generic.impl.magic.ICharmXMLConstants.ATTRIB_VISUALIZE;
import static net.sf.anathema.character.generic.impl.magic.ICharmXMLConstants.TAG_ATTRIBUTE;
import static net.sf.anathema.character.generic.impl.magic.ICharmXMLConstants.TAG_GENERIC_ATTRIBUTE;

public class CharmAttributeBuilder {

  public ICharmAttribute[] buildCharmAttributes(Element rulesElement, IGenericTrait primaryPrerequisite) {
    List<ICharmAttribute> attributes = new ArrayList<>();
    for (Element attributeElement : ElementUtilities.elements(rulesElement, TAG_ATTRIBUTE)) {
      String attributeId = attributeElement.attributeValue(ATTRIB_ATTRIBUTE);
      boolean visualizeAttribute = ElementUtilities.getBooleanAttribute(attributeElement, ATTRIB_VISUALIZE, false);
      String value = attributeElement.attributeValue(ATTRIB_VALUE);
      if (value == null || value.isEmpty()) {
        attributes.add(new CharmAttribute(attributeId, visualizeAttribute));
      }
      else {
        attributes.add(new CharmAttribute(attributeId, visualizeAttribute, value));
      }
    }
    if (primaryPrerequisite != null) {
      String id = primaryPrerequisite.getType().getId();
      attributes.add(new CharmAttribute(id, false));
      for (Element genericAttributeElement : ElementUtilities.elements(rulesElement, TAG_GENERIC_ATTRIBUTE)) {
        String attributeId = genericAttributeElement.attributeValue(ATTRIB_ATTRIBUTE) + id;
        attributes.add(new CharmAttribute(attributeId, false));
      }
    }
    return attributes.toArray(new ICharmAttribute[attributes.size()]);
  }
}