package net.sf.anathema.test.character.main.impl.costs;

import net.sf.anathema.character.dummy.trait.DummyCoreTraitConfiguration;
import net.sf.anathema.character.generic.IBasicCharacterData;
import net.sf.anathema.character.generic.dummy.DummyBasicCharacterData;
import net.sf.anathema.character.generic.magic.IMagic;
import net.sf.anathema.character.generic.magic.charms.MartialArtsLevel;
import net.sf.anathema.character.impl.model.advance.CostAnalyzer;
import net.sf.anathema.dummy.character.magic.DummyCharm;
import net.sf.anathema.lib.util.Identified;
import org.junit.Test;
import org.mockito.Mockito;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

public class CostAnalyzerTest {

  private static final String CHARM_ID = "charmId"; //$NON-NLS-1$
  private DummyCoreTraitConfiguration dummyCoreTraitConfiguration = new DummyCoreTraitConfiguration();
  private DummyBasicCharacterData basicCharacterData = new DummyBasicCharacterData();
  private CostAnalyzer costAnalyzer = new CostAnalyzer(null, basicCharacterData, dummyCoreTraitConfiguration);

  @Test
  public void testIsFavoredMagicDelegatesToMagic() throws Exception {
    IMagic magic = Mockito.mock(IMagic.class);
    IBasicCharacterData trueCharacterData = new DummyBasicCharacterData();
    Mockito.when(magic.isFavored(trueCharacterData, dummyCoreTraitConfiguration)).thenReturn(true);
    IBasicCharacterData falseCharacterData = new DummyBasicCharacterData();
    when(magic.isFavored(falseCharacterData, dummyCoreTraitConfiguration)).thenReturn(false);
    assertTrue(new CostAnalyzer(null, trueCharacterData, dummyCoreTraitConfiguration).isMagicFavored(magic));
    assertFalse(new CostAnalyzer(null, falseCharacterData, dummyCoreTraitConfiguration).isMagicFavored(magic));
  }

  @Test
  public void testGetMartialArtsLevelFromMartialArtsCharm() throws Exception {
    assertEquals(MartialArtsLevel.Terrestrial, costAnalyzer.getMartialArtsLevel(new DummyCharm(CHARM_ID) {
      @Override
      public boolean hasAttribute(Identified attribute) {
        return attribute.getId().equals("MartialArts") || attribute.getId().equals("Terrestrial"); //$NON-NLS-1$ //$NON-NLS-2$
      }
    }));
  }

  @Test
  public void testGetMartialArtsLevelFromNonMartialArtsCharm() throws Exception {
    assertNull(costAnalyzer.getMartialArtsLevel(new DummyCharm(CHARM_ID)));
  }
}