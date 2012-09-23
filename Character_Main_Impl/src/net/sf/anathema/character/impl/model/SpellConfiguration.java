package net.sf.anathema.character.impl.model;

import net.sf.anathema.character.generic.impl.magic.SpellException;
import net.sf.anathema.character.generic.impl.magic.persistence.ISpellCache;
import net.sf.anathema.character.generic.magic.ISpell;
import net.sf.anathema.character.generic.magic.spells.CircleType;
import net.sf.anathema.character.generic.template.ICharacterTemplate;
import net.sf.anathema.character.generic.template.magic.ISpellMagicTemplate;
import net.sf.anathema.character.model.IMagicLearnListener;
import net.sf.anathema.character.model.ISpellConfiguration;
import net.sf.anathema.character.model.ISpellLearnStrategy;
import net.sf.anathema.character.model.ISpellMapper;
import net.sf.anathema.character.model.charm.ICharmConfiguration;
import net.sf.anathema.lib.control.IChangeListener;
import org.jmock.example.announcer.Announcer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SpellConfiguration implements ISpellConfiguration {

  private final List<ISpell> creationLearnedList = new ArrayList<>();
  private final List<ISpell> experiencedLearnedList = new ArrayList<>();
  private final Announcer<IChangeListener> changeControl = Announcer.to(IChangeListener.class);
  private final Announcer<IMagicLearnListener> magicLearnControl = Announcer.to(IMagicLearnListener.class);
  private final Map<CircleType, List<ISpell>> spellsByCircle = new HashMap<>();
  private final ICharmConfiguration charms;
  private final ISpellLearnStrategy strategy;
  private final ICharacterTemplate characterTemplate;
  private final ISpellMapper spellMapper;

  public SpellConfiguration(ICharmConfiguration charms,
		  ISpellLearnStrategy strategy,
		  ICharacterTemplate template,
		  ISpellCache cache) throws SpellException {
    this.charms = charms;
    this.strategy = strategy;
    this.characterTemplate = template;
    for (CircleType type : CircleType.values()) {
      spellsByCircle.put(type, new ArrayList<ISpell>());
    }
    for (ISpell spell : cache.getSpells()) {
      spellsByCircle.get(spell.getCircleType()).add(spell);
    }
    spellMapper = new SpellMapper();
  }

  @Override
  public void removeSpells(ISpell[] removedSpells) {
    strategy.removeSpells(this, removedSpells);
  }

  @Override
  public void removeSpells(ISpell[] removedSpells, boolean experienced) {
    for (ISpell spell : removedSpells) {
      if (experienced) {
        experiencedLearnedList.remove(spell);
      } else {
        creationLearnedList.remove(spell);
      }
    }
    fireSpellsForgottenEvent(removedSpells);
  }

  @Override
  public void addSpells(ISpell[] addedSpells) {
    strategy.addSpells(this, addedSpells);
  }

  @Override
  public void addSpells(ISpell[] addedSpells, boolean experienced) {
    for (ISpell spell : addedSpells) {
      if (isSpellAllowed(spell, experienced)) {
        if (experienced) {
          experiencedLearnedList.add(spell);
        } else {
          creationLearnedList.add(spell);
        }
      } else {
        throw new IllegalArgumentException("Cannot learn Spell: " + spell); //$NON-NLS-1$
      }
    }
    fireSpellsAddedEvent(addedSpells);
  }

  private void fireSpellsAddedEvent(ISpell[] addedSpells) {
    magicLearnControl.announce().magicLearned(addedSpells);
    changeControl.announce().changeOccurred();
  }

  private void fireSpellsForgottenEvent(ISpell[] removedSpells) {
    magicLearnControl.announce().magicForgotten(removedSpells);
    changeControl.announce().changeOccurred();
  }

  @Override
  public boolean isSpellAllowed(ISpell spell) {
    return strategy.isSpellAllowed(this, spell);
  }

  @Override
  public boolean isSpellAllowed(ISpell spell, boolean experienced) {
    if (creationLearnedList.contains(spell) || (experienced && experiencedLearnedList.contains(spell))) {
      return false;
    }
    ISpellMagicTemplate template = characterTemplate.getMagicTemplate().getSpellMagic();
    return template.canLearnSpell(spell, charms.getLearnedCharms(true));
  }

  @Override
  public ISpell[] getLearnedSpells() {
    return strategy.getLearnedSpells(this);
  }

  @Override
  public ISpell[] getLearnedSpells(boolean experienced) {
    List<ISpell> list = new ArrayList<>();
    list.addAll(creationLearnedList);
    if (experienced) {
      list.addAll(experiencedLearnedList);
    }
    return list.toArray(new ISpell[list.size()]);
  }

  @Override
  public void addChangeListener(IChangeListener listener) {
    changeControl.addListener(listener);
  }

  @Override
  public void addMagicLearnListener(IMagicLearnListener<ISpell> listener) {
    magicLearnControl.addListener(listener);
  }

  @Override
  public ISpell[] getSpellsByCircle(CircleType circle) {
    List<ISpell> spells = spellsByCircle.get(circle);
    if (spells != null) {
      return spells.toArray(new ISpell[spells.size()]);
    }
    return new ISpell[0];
  }

  @Override
  public ISpell getSpellById(String id) {
    String correctedId = spellMapper.getId(id);
    for (ISpell spell : getAllSpells()) {
      if (spell.getId().equals(correctedId)) {
        return spell;
      }
    }
    throw new IllegalArgumentException("No Spell for id: " + id); //$NON-NLS-1$
  }

  private Iterable<ISpell> getAllSpells() {
    List<ISpell> allSpells = new ArrayList<>();
    for (List<ISpell> circleSpells : spellsByCircle.values()) {
      allSpells.addAll(circleSpells);
    }
    return allSpells;
  }

  @Override
  public boolean isLearnedOnCreation(ISpell spell) {
    return creationLearnedList.contains(spell);
  }

  @Override
  public boolean isLearnedOnCreationOrExperience(ISpell spell) {
    return experiencedLearnedList.contains(spell) || isLearnedOnCreation(spell);
  }

  @Override
  public boolean isLearned(ISpell spell) {
    return strategy.isLearned(this, spell);
  }
}