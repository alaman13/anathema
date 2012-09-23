package net.sf.anathema.character.impl.persistence;

import com.google.common.base.Preconditions;
import net.sf.anathema.character.generic.caste.ICasteCollection;
import net.sf.anathema.character.generic.framework.ICharacterGenerics;
import net.sf.anathema.character.generic.impl.magic.SpellException;
import net.sf.anathema.character.generic.magic.charms.CharmException;
import net.sf.anathema.character.generic.template.ICharacterTemplate;
import net.sf.anathema.character.generic.template.ITemplateType;
import net.sf.anathema.character.generic.template.TemplateType;
import net.sf.anathema.character.generic.traits.types.OtherTraitType;
import net.sf.anathema.character.generic.type.CharacterType;
import net.sf.anathema.character.generic.type.ICharacterType;
import net.sf.anathema.character.impl.model.ExaltedCharacter;
import net.sf.anathema.character.impl.persistence.charm.CharmConfigurationPersister;
import net.sf.anathema.character.model.ICharacter;
import net.sf.anathema.framework.messaging.IMessaging;
import net.sf.anathema.lib.exception.PersistenceException;
import net.sf.anathema.lib.util.Identificate;
import net.sf.anathema.lib.util.Identified;
import net.sf.anathema.lib.xml.ElementUtilities;
import org.dom4j.Element;

import static net.sf.anathema.character.impl.persistence.ICharacterXmlConstants.ATTRIB_EXPERIENCED;
import static net.sf.anathema.character.impl.persistence.ICharacterXmlConstants.ATTRIB_SUB_TYPE;
import static net.sf.anathema.character.impl.persistence.ICharacterXmlConstants.TAG_CHARACTER_TYPE;
import static net.sf.anathema.character.impl.persistence.ICharacterXmlConstants.TAG_STATISTICS;

public class CharacterStatisticPersister {

  private final AttributeConfigurationPersister attributePersister = new AttributeConfigurationPersister();
  private final AbilityConfigurationPersister abilityPersister = new AbilityConfigurationPersister();
  private final CharacterConceptPersister characterConceptPersister = new CharacterConceptPersister();
  private final EssenceConfigurationPersister essencePersister = new EssenceConfigurationPersister();
  private final VirtueConfigurationPersister virtuePersister = new VirtueConfigurationPersister();
  private final BackgroundConfigurationPersister backgroundPersister;
  private final WillpowerConfigurationPersister willpowerPersister = new WillpowerConfigurationPersister();
  private final CharmConfigurationPersister charmPersister;
  private final SpellConfigurationPersister spellPersister = new SpellConfigurationPersister();
  private final ExperiencePointsPersister experiencePersister = new ExperiencePointsPersister();
  private final RulesPersister rulesPersister = new RulesPersister();
  private final ICharacterGenerics generics;
  private final AdditionalModelPersister additonalModelPersister;
  private final CharacterDescriptionPersister descriptionPersister = new CharacterDescriptionPersister();

  public CharacterStatisticPersister(ICharacterGenerics generics, IMessaging messaging) {
    this.generics = generics;
    this.backgroundPersister = new BackgroundConfigurationPersister(generics.getBackgroundRegistry());
    this.charmPersister = new CharmConfigurationPersister(messaging);
    this.additonalModelPersister = new AdditionalModelPersister(generics.getAdditonalPersisterFactoryRegistry(), messaging);
  }

  public void save(Element parent, ICharacter character) {
    Preconditions.checkNotNull(character);
    descriptionPersister.save(parent, character.getDescription());
    Element statisticsElement = parent.addElement(TAG_STATISTICS);
    rulesPersister.save(statisticsElement);
    statisticsElement.addAttribute(ATTRIB_EXPERIENCED, String.valueOf(character.isExperienced()));
    ICharacterTemplate template = character.getCharacterTemplate();
    Element characterTypeElement = statisticsElement.addElement(TAG_CHARACTER_TYPE);
    characterTypeElement.addAttribute(ATTRIB_SUB_TYPE, template.getTemplateType().getSubType().getId());
    characterTypeElement.addText(template.getTemplateType().getCharacterType().getId());
    characterConceptPersister.save(statisticsElement, character.getCharacterConcept());
    essencePersister.save(statisticsElement, character.getTraitConfiguration());
    willpowerPersister.save(statisticsElement, character.getTraitConfiguration().getTrait(OtherTraitType.Willpower));
    virtuePersister.save(statisticsElement, character.getTraitConfiguration());
    attributePersister.save(statisticsElement, character.getTraitConfiguration());
    abilityPersister.save(statisticsElement, character.getTraitConfiguration());
    backgroundPersister.save(statisticsElement, character.getTraitConfiguration().getBackgrounds());
    charmPersister.save(statisticsElement, character);
    spellPersister.save(statisticsElement, character.getSpells());
    experiencePersister.save(statisticsElement, character.getExperiencePoints());
    additonalModelPersister.save(statisticsElement, character.getExtendedConfiguration().getAdditionalModels());
  }

  public ICharacter load(Element parent) throws PersistenceException {
    try {
      Element statisticsElement = parent.element(TAG_STATISTICS);
      ITemplateType templateType = loadTemplateType(statisticsElement);
      boolean experienced = ElementUtilities.getBooleanAttribute(statisticsElement, ATTRIB_EXPERIENCED, false);
      ICharacterTemplate template = generics.getTemplateRegistry().getTemplate(templateType);
      ExaltedCharacter character = new ExaltedCharacter(template, generics);
      descriptionPersister.load(parent, character.getDescription());
      ICasteCollection casteCollection = template.getCasteCollection();
      characterConceptPersister.load(statisticsElement, character.getCharacterConcept(), character.getDescription(), casteCollection);
      character.setExperienced(experienced);
      essencePersister.load(statisticsElement, character.getTraitConfiguration());
      virtuePersister.load(statisticsElement, character.getTraitConfiguration());
      attributePersister.load(statisticsElement, character.getTraitConfiguration());
      abilityPersister.load(statisticsElement, character.getTraitConfiguration());
      backgroundPersister.load(statisticsElement, character.getTraitConfiguration().getBackgrounds());
      charmPersister.load(statisticsElement, character);
      spellPersister.load(statisticsElement, character.getSpells());
      experiencePersister.load(statisticsElement, character.getExperiencePoints());
      willpowerPersister.load(statisticsElement, character.getTraitConfiguration().getTrait(OtherTraitType.Willpower));
      additonalModelPersister.load(statisticsElement, character.getExtendedConfiguration().getAdditionalModels());
      return character;
    } catch (CharmException | SpellException e) {
      throw new PersistenceException(e);
    }
  }

  private ITemplateType loadTemplateType(Element parent) throws PersistenceException {
    String typeId = ElementUtilities.getRequiredText(parent, TAG_CHARACTER_TYPE);
    ICharacterType characterType = CharacterType.getById(typeId);
    String subTypeValue = parent.element(TAG_CHARACTER_TYPE).attributeValue(ATTRIB_SUB_TYPE);
    Identified subtype = subTypeValue == null ? TemplateType.DEFAULT_SUB_TYPE : new Identificate(subTypeValue);
    return new TemplateType(characterType, subtype);
  }
}