/* 
 * This file is part of the Sejda source code
 * Copyright 2015 by Andrea Vacondio (andrea.vacondio@gmail.com).
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

import static java.util.Objects.requireNonNull;
import static java.util.Optional.ofNullable;
import static org.sejda.impl.sambox.component.OutlineUtils.clonePageDestination;
import static org.sejda.impl.sambox.component.OutlineUtils.copyOutlineDictionary;
import static org.sejda.impl.sambox.component.OutlineUtils.toPageDestination;
import static org.sejda.util.RequireUtils.requireNotNullArg;

import java.util.Optional;

import org.sejda.common.LookupTable;
import org.sejda.sambox.pdmodel.PDDocument;
import org.sejda.sambox.pdmodel.PDPage;
import org.sejda.sambox.pdmodel.interactive.documentnavigation.destination.PDPageDestination;
import org.sejda.sambox.pdmodel.interactive.documentnavigation.outline.PDDocumentOutline;
import org.sejda.sambox.pdmodel.interactive.documentnavigation.outline.PDOutlineItem;
import org.sejda.sambox.pdmodel.interactive.documentnavigation.outline.PDOutlineNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Component that can distill a cloned version of the document outline based on the relevant pages selected and can append it to a given existing {@link PDDocumentOutline},
 * filtering out outline item pointing to irrelevant pages.
 * 
 * @author Andrea Vacondio
 *
 */
public class OutlineDistiller {
    private static final Logger LOG = LoggerFactory.getLogger(OutlineDistiller.class);

    private PDDocument document;

    public OutlineDistiller(PDDocument document) {
        requireNotNullArg(document, "Unable to retrieve bookmarks from a null document.");
        this.document = document;
    }

    /**
     * Appends to the given outline, all the outline items whose page destination is relevant
     * 
     * @param to
     * @param pagesLookup
     */
    public void appendRelevantOutlineTo(PDOutlineNode to, LookupTable<PDPage> pagesLookup) {
        requireNonNull(to, "Unable to merge relevant outline items to a null outline.");
        if (!pagesLookup.isEmpty()) {
            ofNullable(document.getDocumentCatalog().getDocumentOutline()).ifPresent(outline -> {
                for (PDOutlineItem child : outline.children()) {
                    cloneNode(child, pagesLookup).ifPresent(c -> to.addLast(c));
                }
                LOG.debug("Appended relevant outline items");
            });
        }
    }

    private Optional<PDOutlineItem> cloneNode(PDOutlineItem node, LookupTable<PDPage> pagesLookup) {
        if (node.hasChildren()) {
            final PDOutlineItem clone = new PDOutlineItem();
            for (PDOutlineItem current : node.children()) {
                cloneNode(current, pagesLookup).ifPresent(clonedChild -> {
                    clone.addLast(clonedChild);
                });
            }
            Optional<PDPageDestination> pageDestination = toPageDestination(node, document.getDocumentCatalog());
            Optional<PDPage> destinationPage = pageDestination.map(PDPageDestination::getPage)
                    .map(p -> pagesLookup.lookup(p));
            if (clone.hasChildren() || destinationPage.isPresent()) {
                copyOutlineDictionary(node, clone);
                destinationPage.ifPresent(p -> clone.setDestination(clonePageDestination(pageDestination.get(), p)));
                return Optional.of(clone);
            }
            return Optional.empty();
        }
        return cloneLeafIfNeeded(node, pagesLookup);
    }

    /**
     * @param origin
     * @return a clone of the origin leaf if its page destination falls in the range of the needed pages. Cloned item destination is offset by the given offset.
     */
    private Optional<PDOutlineItem> cloneLeafIfNeeded(PDOutlineItem origin, LookupTable<PDPage> pagesLookup) {
        return toPageDestination(origin, document.getDocumentCatalog()).flatMap(d -> {
            PDPage mapped = pagesLookup.lookup(d.getPage());
            if (mapped != null) {
                PDOutlineItem retVal = new PDOutlineItem();
                copyOutlineDictionary(origin, retVal);
                retVal.setDestination(clonePageDestination(d, mapped));
                return Optional.of(retVal);
            }
            return Optional.empty();
        });
    }
}
