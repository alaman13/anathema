package net.sf.anathema.character.reporting.sheet.common;

import java.awt.Point;

import net.disy.commons.core.geometry.SmartRectangle;
import net.sf.anathema.character.generic.character.IGenericTraitCollection;
import net.sf.anathema.character.generic.template.abilities.IGroupedTraitType;
import net.sf.anathema.character.generic.traits.ITraitType;
import net.sf.anathema.character.reporting.sheet.util.PdfTraitEncoder;
import net.sf.anathema.lib.resources.IResources;

import com.lowagie.text.pdf.BaseFont;
import com.lowagie.text.pdf.PdfContentByte;

public class PdfAttributesEncoder {

  private final IResources resources;
  private PdfTraitEncoder smallTraitEncoder;
  private final int essenceMax;

  public PdfAttributesEncoder(BaseFont baseFont, IResources resources, int essenceMax) {
    this.resources = resources;
    this.essenceMax = essenceMax;
    this.smallTraitEncoder = PdfTraitEncoder.createSmallTraitEncoder(baseFont);
  }

  public final void encodeAttributes(
      PdfContentByte directContent,
      SmartRectangle contentBounds,
      IGroupedTraitType[] attributeGroups,
      IGenericTraitCollection traitCollection) {
    int groupSpacing = smallTraitEncoder.getTraitHeight() / 2;
    int y = (int) contentBounds.getMaxY() - groupSpacing;
    String groupId = null;
    for (IGroupedTraitType groupedTraitType : attributeGroups) {
      if (!groupedTraitType.getGroupId().equals(groupId)) {
        groupId = groupedTraitType.getGroupId();
        y -= groupSpacing;
      }
      ITraitType traitType = groupedTraitType.getTraitType();
      String traitLabel = resources.getString("AttributeType.Name." + traitType.getId()); //$NON-NLS-1$
      int value = traitCollection.getTrait(traitType).getCurrentValue();
      Point position = new Point(contentBounds.x, y);
      y -= smallTraitEncoder.encodeWithText(directContent, traitLabel, position, contentBounds.width, value, essenceMax);
    }
  }
}