package net.sf.anathema.character.generic.framework.xml.abilitygroup;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import net.sf.anathema.character.generic.framework.xml.core.AbstractXmlTemplateParser;
import net.sf.anathema.character.generic.framework.xml.registry.IXmlTemplateRegistry;
import net.sf.anathema.character.generic.traits.groups.ITraitTypeGroup;
import net.sf.anathema.lib.exception.PersistenceException;
import net.sf.anathema.lib.xml.ElementUtilities;

import org.dom4j.Element;

public class TraitTypeGroupTemplateParser extends AbstractXmlTemplateParser<GenericGroupedTraitTypeProvider> {

  private static final String ATTRIB_ID = "id"; //$NON-NLS-1$
  private static final String CASTE_ID = "casteId"; //$NON-NLS-1$
  private static final String ATTRIB_TYPE = "type"; //$NON-NLS-1$
  private static final String TAG_GROUP = "group"; //$NON-NLS-1$
  private static final String TAG_TRAIT = "trait"; //$NON-NLS-1$
  private final ITraitTypeGroup traitTypeGroup;

  public TraitTypeGroupTemplateParser(
      IXmlTemplateRegistry<GenericGroupedTraitTypeProvider> templateRegistry,
      ITraitTypeGroup traitTypeGroup) {
    super(templateRegistry);
    this.traitTypeGroup = traitTypeGroup;
  }

  @Override
  public GenericGroupedTraitTypeProvider parseTemplate(Element element) throws PersistenceException {
    GenericGroupedTraitTypeProvider abilityGroupProvider = getBasicTemplate(element);
    updateGroups(element, abilityGroupProvider);
    return abilityGroupProvider;
  }

  private void updateGroups(Element element, GenericGroupedTraitTypeProvider abilityGroupProvider) {
    List<Element> groups = ElementUtilities.elements(element, TAG_GROUP);
    for (Element group : groups) {
      String groupId = group.attributeValue(ATTRIB_ID);
      String groupCasteId = group.attributeValue(CASTE_ID);
      List<Element> abilities = ElementUtilities.elements(group, TAG_TRAIT);
      for (Element ability : abilities) {
    	List<String> traitCastes = new ArrayList<>();
        String attributeTypeValue = ability.attributeValue(ATTRIB_TYPE);
        if (ability.attributeValue(CASTE_ID) != null) {
          Collections.addAll(traitCastes, ability.attributeValue(CASTE_ID).split(","));
        }
        abilityGroupProvider.addGroupedAbilityType(attributeTypeValue, groupId, groupCasteId, traitCastes);
      }
    }
  }

  @Override
  protected GenericGroupedTraitTypeProvider createNewBasicTemplate() {
    return new GenericGroupedTraitTypeProvider(traitTypeGroup);
  }
}
