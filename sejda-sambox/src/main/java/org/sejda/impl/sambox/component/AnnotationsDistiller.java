/*
 * Created on 03/set/2015
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

import static org.sejda.util.RequireUtils.requireNotNullArg;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.sejda.sambox.pdmodel.PDDocument;
import org.sejda.sambox.pdmodel.PDPage;
import org.sejda.sambox.pdmodel.interactive.action.PDAction;
import org.sejda.sambox.pdmodel.interactive.action.PDActionGoTo;
import org.sejda.sambox.pdmodel.interactive.annotation.PDAnnotation;
import org.sejda.sambox.pdmodel.interactive.annotation.PDAnnotationLink;
import org.sejda.sambox.pdmodel.interactive.documentnavigation.destination.PDDestination;
import org.sejda.sambox.pdmodel.interactive.documentnavigation.destination.PDNamedDestination;
import org.sejda.sambox.pdmodel.interactive.documentnavigation.destination.PDPageDestination;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Component that can distill pages annotations filtering out those pointing to irrelevant pages.
 * 
 * @author Andrea Vacondio
 *
 */
public final class AnnotationsDistiller {
    private static final Logger LOG = LoggerFactory.getLogger(AnnotationsDistiller.class);

    private AnnotationsDistiller() {
        // utility
    }

    /**
     * Removes from the given set of pages all the annotations pointing to a page that is not in the set (an irrelevant page)
     * 
     * @param relevantPages
     * @param pagesOwner
     *            document owning the pages
     */
    public static void filterAnnotations(Collection<PDPage> relevantPages, PDDocument pagesOwner) {
        requireNotNullArg(pagesOwner, "Cannot process annotations for a null document");
        LOG.debug("Pocessing annotations");
        for (PDPage page : relevantPages) {
            try {
                List<PDAnnotation> keptAnnotation = new ArrayList<>();
                for (PDAnnotation annotation : page.getAnnotations()) {
                    if (annotation instanceof PDAnnotationLink) {
                        PDDestination destination = getDestinationFrom((PDAnnotationLink) annotation, pagesOwner);
                        if (destination instanceof PDPageDestination
                                && !relevantPages.contains(((PDPageDestination) destination).getPage())) {
                            LOG.trace("Removing link annotation {}", annotation);
                        } else {
                            keptAnnotation.add(annotation);
                        }
                    } else {
                        PDPage p = annotation.getPage();
                        if (p == null != relevantPages.contains(p)) {
                            keptAnnotation.add(annotation);
                        }
                    }
                }
                page.setAnnotations(keptAnnotation);
            } catch (IOException e) {
                LOG.warn("Failed to process annotations for page", e);
            }
        }
    }

    private static PDDestination getDestinationFrom(PDAnnotationLink link, PDDocument pageOwner) throws IOException {
        PDDestination destination = link.getDestination();
        if (destination == null) {
            PDAction action = link.getAction();
            if (action instanceof PDActionGoTo) {
                destination = ((PDActionGoTo) action).getDestination();
            }
        }
        if (destination instanceof PDNamedDestination) {
            destination = pageOwner.getDocumentCatalog().findNamedDestinationPage((PDNamedDestination) destination);
        }
        return destination;
    }
}
