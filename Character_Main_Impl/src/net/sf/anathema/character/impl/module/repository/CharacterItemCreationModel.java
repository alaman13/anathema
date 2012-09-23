package net.sf.anathema.character.impl.module.repository;

import net.sf.anathema.character.generic.framework.ICharacterGenerics;
import net.sf.anathema.character.generic.template.ICharacterTemplate;
import net.sf.anathema.character.generic.template.ITemplateRegistry;
import net.sf.anathema.character.generic.type.CharacterType;
import net.sf.anathema.character.generic.type.ICharacterType;
import net.sf.anathema.character.impl.model.CharacterStatisticsConfiguration;
import net.sf.anathema.character.view.repository.ITemplateTypeAggregation;
import net.sf.anathema.lib.collection.MultiEntryMap;
import net.sf.anathema.lib.control.IChangeListener;
import org.jmock.example.announcer.Announcer;

import java.util.ArrayList;
import java.util.List;

public class CharacterItemCreationModel implements ICharacterItemCreationModel {

  private ICharacterType selectedType;
  private final Announcer<IChangeListener> control = Announcer.to(IChangeListener.class);
  private ITemplateTypeAggregation selectedTemplate;
  private final MultiEntryMap<ICharacterType, ITemplateTypeAggregation> aggregationsByType = new MultiEntryMap<>();
  private final CharacterStatisticsConfiguration configuration;
  private final ICharacterGenerics generics;
  private final ICharacterType[] types;

  public CharacterItemCreationModel(ICharacterGenerics generics, CharacterStatisticsConfiguration configuration) {
    this.generics = generics;
    this.configuration = configuration;
    this.types = collectCharacterTypes(generics.getTemplateRegistry());
    aggregateTemplates();
    setCharacterType(CharacterType.SOLAR);
  }

  private ICharacterType[] collectCharacterTypes(ITemplateRegistry registry) {
    List<ICharacterType> availableTypes = new ArrayList<>();
    for (ICharacterType type : CharacterType.values()) {
      if (registry.getAllSupportedTemplates(type).length > 0) {
        availableTypes.add(type);
      }
    }
    return availableTypes.toArray(new ICharacterType[availableTypes.size()]);
  }

  private void aggregateTemplates() {
    TemplateTypeAggregator aggregator = new TemplateTypeAggregator(generics.getTemplateRegistry());
    for (ICharacterType type : CharacterType.values()) {
      ITemplateTypeAggregation[] aggregations = aggregator.aggregateTemplates(type);
      if (aggregations.length == 0) {
        continue;
      }
      aggregationsByType.add(type, aggregations);
    }
  }

  @Override
  public boolean isSelectionComplete() {
    return configuration.getTemplate() != null;
  }

  @Override
  public boolean isCharacterTypeSelected() {
    return selectedType != null;
  }

  @Override
  public ICharacterType[] getAvailableCharacterTypes() {
    return types;
  }

  @Override
  public void setCharacterType(ICharacterType type) {
    if (selectedType == type) {
      return;
    }
    this.selectedType = type;
    setTemplateToDefault();
    control.announce().changeOccurred();
  }

  private void setTemplateToDefault() {
    if (getAvailableTemplates().length == 0) {
      setSelectedTemplate(null);
    } else {
      ICharacterTemplate defaultTemplate = generics.getTemplateRegistry().getDefaultTemplate(selectedType);
      for (ITemplateTypeAggregation aggregation : aggregationsByType.get(selectedType)) {
        if (aggregation.contains(defaultTemplate)) {
          setSelectedTemplate(aggregation);
          return;
        }
      }
      throw new IllegalStateException("Template not contained in aggregations."); //$NON-NLS-1$
    }
  }

  @Override
  public ITemplateTypeAggregation[] getAvailableTemplates() {
    List<ITemplateTypeAggregation> list = aggregationsByType.get(selectedType);
    List<ITemplateTypeAggregation> copyList = new ArrayList<>(list);
    return copyList.toArray(new ITemplateTypeAggregation[copyList.size()]);
  }

  @Override
  public void setSelectedTemplate(ITemplateTypeAggregation newValue) {
    if (selectedTemplate == newValue) {
      return;
    }
    this.selectedTemplate = newValue;
    if (selectedTemplate == null) {
      configuration.setTemplate(null);
    } else {
      setEditionDependentTemplate();
    }
    control.announce().changeOccurred();
  }

  private void setEditionDependentTemplate() {
    configuration.setTemplate(
            generics.getTemplateRegistry().getTemplate(selectedTemplate.getTemplateType()));
  }

  @Override
  public ITemplateTypeAggregation getSelectedTemplate() {
    return selectedTemplate;
  }

  @Override
  public void addListener(IChangeListener listener) {
    control.addListener(listener);
  }
}