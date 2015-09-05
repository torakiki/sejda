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
import static org.sejda.impl.sambox.component.OutlineUtils.copyOutlineDictionary;
import static org.sejda.impl.sambox.component.OutlineUtils.toPageDestination;

import java.util.Collections;
import java.util.Optional;
import java.util.Set;

import org.sejda.sambox.pdmodel.PDDocument;
import org.sejda.sambox.pdmodel.PDPage;
import org.sejda.sambox.pdmodel.interactive.documentnavigation.destination.PDPageDestination;
import org.sejda.sambox.pdmodel.interactive.documentnavigation.outline.PDDocumentOutline;
import org.sejda.sambox.pdmodel.interactive.documentnavigation.outline.PDOutlineItem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Component that can distill a cloned version of the document outline based on the relevant pages selected and can append it to a given existing {@link PDDocumentOutline},
 * filtering out outline item pointing to irrelevant pages.
 * 
 * @author Andrea Vacondio
 *
 */
class OutlineDistiller {
    private static final Logger LOG = LoggerFactory.getLogger(OutlineDistiller.class);

    private PDDocument document;
    private Set<PDPage> currentRelevantPages;

    public OutlineDistiller(PDDocument document) {
        requireNonNull(document, "Unable to retrieve bookmarks from a null document.");
        this.document = document;
    }

    /**
     * Appends to the given outline, all the outline items whose page destination is relevant
     * 
     * @param to
     * @param relevantPages
     */
    public void appendRelevantOutlineTo(PDDocumentOutline to, Set<PDPage> relevantPages) {
        requireNonNull(to, "Unable to merge relevant outline items to a null outline.");
        this.currentRelevantPages = Optional.ofNullable(relevantPages).orElse(Collections.emptySet());
        PDDocumentOutline outline = document.getDocumentCatalog().getDocumentOutline();
        if (currentRelevantPages.size() > 0 && outline != null) {
            for (PDOutlineItem child : outline.children()) {
                cloneNode(child).ifPresent(c -> to.addLast(c));
            }
            LOG.debug("Appended relevant outline items");
        }
    }

    private Optional<PDOutlineItem> cloneNode(PDOutlineItem node) {
        if (node.hasChildren()) {
            final PDOutlineItem clone = new PDOutlineItem();
            for (PDOutlineItem current : node.children()) {
                cloneNode(current).ifPresent(clonedChild -> {
                    clone.addLast(clonedChild);
                });
            }
            if (clone.hasChildren()) {
                copyOutlineDictionary(node, clone);
                Optional<PDPageDestination> destination = toPageDestination(node, document.getDocumentCatalog());
                if (isNeeded(destination)) {
                    copyDestination(destination, clone);
                }
                return Optional.of(clone);
            }
            return Optional.empty();
        }
        return cloneLeafIfNeeded(node);
    }

    private static void copyDestination(Optional<PDPageDestination> destination, PDOutlineItem to) {
        destination.ifPresent(d -> {
            to.setDestination(d);
        });
    }

    /**
     * @param origin
     * @return a clone of the origin leaf if its page destination falls in the range of the needed pages. Cloned item destination is offset by the given offset.
     */
    private Optional<PDOutlineItem> cloneLeafIfNeeded(PDOutlineItem origin) {
        Optional<PDPageDestination> destination = toPageDestination(origin, document.getDocumentCatalog());
        if (isNeeded(destination)) {
            PDOutlineItem retVal = new PDOutlineItem();
            copyOutlineDictionary(origin, retVal);
            copyDestination(destination, retVal);
            return Optional.of(retVal);
        }
        return Optional.empty();
    }

    private boolean isNeeded(Optional<PDPageDestination> destination) {
        if (destination.isPresent()) {
            return currentRelevantPages.contains(destination.get().getPage());
        }
        return false;
    }
}
