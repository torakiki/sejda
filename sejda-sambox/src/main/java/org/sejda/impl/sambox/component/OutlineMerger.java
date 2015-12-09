/*
 * Created on 04/set/2015
 * Copyright 2015 by Andrea Vacondio (andrea.vacondio@gmail.com).
 * This file is part of Sejda.
 *
 * Sejda is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Sejda is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with Sejda.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.sejda.impl.sambox.component;

import static java.util.Optional.ofNullable;
import static org.apache.commons.io.FilenameUtils.removeExtension;

import org.apache.commons.lang3.StringUtils;
import org.sejda.common.LookupTable;
import org.sejda.model.outline.OutlinePolicy;
import org.sejda.sambox.pdmodel.PDDocument;
import org.sejda.sambox.pdmodel.PDPage;
import org.sejda.sambox.pdmodel.interactive.documentnavigation.destination.PDPageFitDestination;
import org.sejda.sambox.pdmodel.interactive.documentnavigation.outline.PDDocumentOutline;
import org.sejda.sambox.pdmodel.interactive.documentnavigation.outline.PDOutlineItem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Component that can create a new document outline based on the selected {@link OutlinePolicy}
 * 
 * @author Andrea Vacondio
 */
public class OutlineMerger {

    private static final Logger LOG = LoggerFactory.getLogger(OutlineMerger.class);

    private OutlinePolicy policy;
    private PDDocumentOutline outline = new PDDocumentOutline();

    public OutlineMerger(OutlinePolicy policy) {
        this.policy = policy;
    }

    public void updateOutline(PDDocument document, String sourceName, LookupTable<PDPage> pagesLookup) {
        if (!pagesLookup.isEmpty()) {
            LOG.debug("Updating outline with policy {}", policy);
            switch (policy) {
            case ONE_ENTRY_EACH_DOC:
                updateOneEntryPerDoc(sourceName, pagesLookup);
                break;
            case RETAIN:
                new OutlineDistiller(document).appendRelevantOutlineTo(outline, pagesLookup);
                break;
            case RETAIN_AS_ONE_ENTRY:
                ofNullable(updateOneEntryPerDoc(sourceName, pagesLookup))
                        .ifPresent(item -> new OutlineDistiller(document).appendRelevantOutlineTo(item, pagesLookup));
                break;
            default:
                LOG.debug("Discarding outline for {}", sourceName);
            }
        } else {
            // shouldn't happen
            LOG.info("Skipped outline merge, no relevant page");
        }
    }

    private PDOutlineItem updateOneEntryPerDoc(String sourceName, LookupTable<PDPage> pagesLookup) {
        if (StringUtils.isNotBlank(sourceName)) {
            LOG.debug("Adding outline entry for {}", sourceName);
            PDOutlineItem item = new PDOutlineItem();
            item.setTitle(removeExtension(sourceName));
            PDPageFitDestination destination = new PDPageFitDestination();
            destination.setPage(pagesLookup.first());
            item.setDestination(destination);
            outline.addLast(item);
            return item;
        }
        LOG.warn("Unable to create an outline item for a source with blank name");
        return null;
    }

    public boolean hasOutline() {
        return outline.hasChildren();
    }

    public PDDocumentOutline getOutline() {
        return outline;
    }
}
