/*
 * This file is part of the Sejda source code
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.sejda.impl.sambox.component;

import org.sejda.impl.sambox.util.FontUtils;
import org.sejda.model.TopLeftRectangularBox;
import org.sejda.model.exception.TaskException;
import org.sejda.model.exception.TaskIOException;
import org.sejda.sambox.cos.IndirectCOSObjectIdentifier;
import org.sejda.sambox.pdmodel.PDDocument;
import org.sejda.sambox.pdmodel.PDPage;
import org.sejda.sambox.pdmodel.common.PDRectangle;
import org.sejda.sambox.pdmodel.font.*;
import org.sejda.sambox.pdmodel.graphics.state.RenderingMode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.*;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Replaces existing text in a page. Existing text is specified by a bounding box.
 * All content streams that contain text within this bounding box will be processed, stripping away the show text operators.
 * Replacement text is written to a new page content stream.
 *
 * We attempt to reuse the same font as the replaced text. If glyphs are missing and it's not possible, we'll try to:
 * - if case of a subset font, load the original from the system, if available
 * - use fallback font for the glyphs that are missing
 */
public class PageTextReplacer {

    private static final Logger LOG = LoggerFactory.getLogger(PageTextReplacer.class);

    private PDDocument document;
    private Map<TopLeftRectangularBox, Set<IndirectCOSObjectIdentifier>> redactedResourceIdsPerBoundingBox = new HashMap<>();

    public PageTextReplacer(PDDocument document) {
        this.document = document;
    }

    public void replaceText(PDPage page, int pageNum, String replacementText, TopLeftRectangularBox boundingBox) throws TaskException {
        PDFTextRedactingStreamEngine engine = null;
        try {
            engine = new PDFTextRedactingStreamEngine(boundingBox);
            engine.processPage(page);
            if(!redactedResourceIdsPerBoundingBox.containsKey(boundingBox)) {
                redactedResourceIdsPerBoundingBox.put(boundingBox, new HashSet<>());
            }
            redactedResourceIdsPerBoundingBox.get(boundingBox).addAll(engine.getRedactedFormXObjectIds());
        } catch (IOException e) {
            throw new TaskIOException(e);
        }

        PDRectangle pageSize = page.getMediaBox().rotate(page.getRotation());
        if(engine.redactedTextPosition == null) {
            // we could not find text in the specified bounding box
            // this could mean that:
            // - (rare) we are processing page1, page2, and both have same shared FormXObject drawn.
            // When we process page1 we redacted the shared FormXObject, so page2 will not find a match.
            if(!redactedResourceIdsPerBoundingBox.get(boundingBox).isEmpty()) {
                // for this bounding box we previously redacted a FormXObject.
                // TODO: we won't figure it out if the same shared FormXObject is rendered in a different position (hence different bounding box)
                LOG.debug("No text found to replace on page {} in bounding box {}, but same bounding box was redacted for a previous page in a shared FormXObject");
                return;
            }

            //
            // - there's a bug, so failing hard will help us identify it and prevent silent failures
            throw new TaskException("No text found to replace on page "+ pageNum +" in bounding box: " + boundingBox.toString());
        } else {
            LOG.debug("Redacted text '{}' at position {}", engine.redactedString, engine.redactedTextPosition);
        }

        if(replacementText.isEmpty()) return;

        PageTextWriter textWriter = new PageTextWriter(document);

        Point position = new Point((int)engine.redactedTextPosition.getX(), (int) pageSize.getHeight() - (int)engine.redactedTextPosition.getY());

        PDFont originalFont = engine.redactedFont;
        PDFont font = originalFont;
        RenderingMode renderingMode = engine.redactedTextRenderingMode;

        if(!FontUtils.canDisplay(replacementText, originalFont)) {
            LOG.debug("Original font: {} cannot display replacement text: '{}'", originalFont.getName(), replacementText);
            // handle subset fonts which might be missing characters we need for the replacement text
            FontUtils.FontSubsetting subsetting = new FontUtils.FontSubsetting(font);
            if(subsetting.isSubset) {
                PDFont replacement = subsetting.loadOriginalOrSimilar(document);
                if(replacement != null) {
                    LOG.debug("Original subset font {} replaced with {}", originalFont.getName(), replacement.getName());
                    font = replacement;
                }
            }
        } else {
            LOG.debug("Will use original font for replacement text: {}", originalFont.getName());
        }


        textWriter.write(page, position, replacementText, font, (double)engine.redactedFontSize, engine.redactedFontColor, renderingMode);
    }
}
