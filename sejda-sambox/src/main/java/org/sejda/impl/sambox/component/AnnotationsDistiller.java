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

import static java.util.Objects.nonNull;
import static org.sejda.util.RequireUtils.requireNotNullArg;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.sejda.common.LookupTable;
import org.sejda.sambox.cos.COSName;
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
     * @return the lookup table to retrieve newly created annotations based on the old ones
     */
    public static LookupTable<PDAnnotation> filterAnnotations(LookupTable<PDPage> relevantPages,
            PDDocument pagesOwner) {
        requireNotNullArg(pagesOwner, "Cannot process annotations for a null document");
        LOG.debug("Filtering annotations");
        LookupTable<PDAnnotation> annotationsLookup = new LookupTable<>();
        for (PDPage page : relevantPages.keys()) {
            try {
                List<PDAnnotation> keptAnnotations = new ArrayList<>();
                for (PDAnnotation annotation : page.getAnnotations()) {
                    if (annotation instanceof PDAnnotationLink) {
                        PDDestination destination = getDestinationFrom((PDAnnotationLink) annotation, pagesOwner);
                        if (destination instanceof PDPageDestination) {
                            PDPage destPage = relevantPages.lookup(((PDPageDestination) destination).getPage());
                            if (nonNull(destPage)) {
                                // not a page dest
                                PDAnnotationLink copyAnnotation = new PDAnnotationLink(
                                        annotation.getCOSObject().duplicate());
                                copyAnnotation.getCOSObject().removeItem(COSName.A);
                                PDPageDestination newDestination = (PDPageDestination) PDDestination
                                        .create(destination.getCOSObject());
                                newDestination.setPage(destPage);
                                copyAnnotation.setDestination(newDestination);
                                annotationsLookup.addLookupEntry(annotation, copyAnnotation);
                                keptAnnotations.add(copyAnnotation);
                            } else {
                                LOG.trace("Removing link annotation");
                            }
                        } else {
                            // not a page dest
                            PDAnnotation copyAnnotation = new PDAnnotationLink(annotation.getCOSObject().duplicate());
                            annotationsLookup.addLookupEntry(annotation, copyAnnotation);
                            keptAnnotations.add(copyAnnotation);
                        }
                    } else {
                        PDPage p = annotation.getPage();
                        if (p == null != relevantPages.hasLookupFor(p)) {
                            PDAnnotation copyAnnotation = PDAnnotation
                                    .createAnnotation(annotation.getCOSObject().duplicate());
                            copyAnnotation.setPage(relevantPages.lookup(page));
                            annotationsLookup.addLookupEntry(annotation, copyAnnotation);
                            keptAnnotations.add(copyAnnotation);
                        }
                    }
                }
                trimSignatures(keptAnnotations);
                relevantPages.lookup(page).setAnnotations(keptAnnotations);
            } catch (IOException e) {
                LOG.warn("Failed to process annotations for page", e);
            }
        }
        return annotationsLookup;
    }

    /**
     * Removes signature values from widget annotations where the fields values are merged to the widget dictionary
     * 
     * @param relevantPages
     * @param pagesOwner
     *            document owning the pages
     */
    public static void trimSignatures(List<PDAnnotation> annotations) {
        for (PDAnnotation annotation : annotations) {
            if (COSName.WIDGET.getName().equals(annotation.getSubtype())
                    && COSName.SIG.equals(annotation.getCOSObject().getCOSName(COSName.FT))) {
                LOG.info("Removing signature value from the widget if any");
                annotation.getCOSObject().removeItem(COSName.V);
                annotation.getCOSObject().removeItem(COSName.SV);
                annotation.getCOSObject().removeItem(COSName.LOCK);
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
