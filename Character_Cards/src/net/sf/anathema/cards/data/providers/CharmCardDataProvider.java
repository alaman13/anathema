package net.sf.anathema.cards.data.providers;

import net.sf.anathema.cards.data.CharmCardData;
import net.sf.anathema.cards.data.ICardData;
import net.sf.anathema.cards.layout.ICardReportResourceProvider;
import net.sf.anathema.character.generic.magic.ICharm;
import net.sf.anathema.character.impl.generic.GenericCharacter;
import net.sf.anathema.character.model.ICharacter;
import net.sf.anathema.character.reporting.pdf.content.stats.magic.CharmStats;
import net.sf.anathema.framework.IAnathemaModel;
import net.sf.anathema.lib.resources.IResources;

import java.util.ArrayList;
import java.util.List;

public class CharmCardDataProvider extends AbstractMagicCardDataProvider {

	public CharmCardDataProvider(IAnathemaModel model, IResources resources) {
		super(model, resources);
	}
	
	@Override
	public ICardData[] getCards(ICharacter character, ICardReportResourceProvider fontProvider) {
		List<ICardData> cards = new ArrayList<>();
		for (ICharm charm : getCurrentCharms(character)) {
			cards.add(new CharmCardData(charm, createCharmStats(character, charm),
					getMagicDescription(charm),
					fontProvider, getResources()));
		}
		return cards.toArray(new ICardData[cards.size()]);
	}
	
	private ICharm[] getCurrentCharms(ICharacter character) {
		return character.getCharms().getLearnedCharms(character.isExperienced());
	}
	
	private CharmStats createCharmStats(ICharacter character, ICharm charm) {
		return new CharmStats(charm, createGenericCharacter(character));
	}
	
	private GenericCharacter createGenericCharacter(ICharacter character) {
		return new GenericCharacter(character);
	}

}
