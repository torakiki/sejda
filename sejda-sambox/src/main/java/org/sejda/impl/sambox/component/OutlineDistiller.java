/* 
 * This file is part of the Sejda source code
 * Copyright 2015 Sober Lemur S.r.l. and Sejda BV.
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
import static org.sejda.commons.util.RequireUtils.requireNotNullArg;
import static org.sejda.impl.sambox.component.OutlineUtils.clonePageDestination;
import static org.sejda.impl.sambox.component.OutlineUtils.copyOutlineDictionary;
import static org.sejda.impl.sambox.component.OutlineUtils.resolvePageDestination;
import static org.sejda.impl.sambox.component.OutlineUtils.toPageDestination;

import java.util.*;

import org.sejda.commons.LookupTable;
import org.sejda.sambox.cos.COSObjectKey;
import org.sejda.sambox.cos.COSObjectable;
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
                cloneOutline(outline, to, pagesLookup);
                LOG.debug("Appended relevant outline items");
            });
        }
    }
    
    private String objIdOf(COSObjectable o) {
        try {
            COSObjectKey ident = o.getCOSObject().id().objectIdentifier;
            String gen = ident.generation() == 0 ? "" : ident.generation() + "";
            return ident.objectNumber() + "R" + gen;
        } catch (Exception e) {
            return "";
        }
    }
    
    private void cloneOutline(PDDocumentOutline from, PDOutlineNode to, LookupTable<PDPage> pagesLookup) {
        // keep track of nodes already visited, to avoid infinite loops
        Set<PDOutlineItem> alreadyVisited = new HashSet<>();
        for (PDOutlineItem child : from.children()) {
            cloneNode(child, pagesLookup, alreadyVisited).ifPresent(to::addLast);
        }
    }

    private Optional<PDOutlineItem> cloneNode(PDOutlineItem node, LookupTable<PDPage> pagesLookup, Set<PDOutlineItem> alreadyVisited) {
        String nodeObjId = objIdOf(node);
        LOG.debug("Cloning node: " + nodeObjId + " " + node.getTitle() + " #" + node.hashCode());
        
        if(alreadyVisited.contains(node)) {
            LOG.warn("Detected already visited node: " + nodeObjId + " " + node.getTitle() + " #" + node.hashCode() + ", skipping at cloning to avoid infinite loop");
            return Optional.empty();
        }
        alreadyVisited.add(node);
        
        if (node.hasChildren()) {
            final PDOutlineItem clone = new PDOutlineItem();
            for (PDOutlineItem current : node.children()) {
                if (current.equals(node)) {
                    LOG.warn("Outline item has a child pointing to the parent, skipping at cloning");
                } else {
                    cloneNode(current, pagesLookup, alreadyVisited).ifPresent(clone::addLast);
                }
            }
            Optional<PDPageDestination> pageDestination = toPageDestination(node, document.getDocumentCatalog());
            Optional<PDPage> destinationPage = pageDestination.map(d -> resolvePageDestination(d, document))
                    .map(pagesLookup::lookup);
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
     * @return a clone of the origin leaf if its page destination falls in the range of the needed pages.
     */
    private Optional<PDOutlineItem> cloneLeafIfNeeded(PDOutlineItem origin, LookupTable<PDPage> pagesLookup) {
        return toPageDestination(origin, document.getDocumentCatalog()).flatMap(d -> {
            PDPage mapped = pagesLookup.lookup(resolvePageDestination(d, document));
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
