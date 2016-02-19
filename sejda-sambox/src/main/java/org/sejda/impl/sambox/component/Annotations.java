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
 * Component that can distill pages annotations filtering out those pointing to irrelevant pages and updating the annotationsif necessasy.
 * 
 * @author Andrea Vacondio
 *
 */
public final class Annotations {
    private static final Logger LOG = LoggerFactory.getLogger(Annotations.class);

    private Annotations() {
        // utility
    }

    /**
     * Removes from the given set of pages all the annotations pointing to a page that is not in the lookup (an irrelevant page) and replaces annotations pointing to an old page
     * with a new one pointing to the looked up page.
     * 
     * @param relevantPages
     * @param pagesOwner
     *            document owning the pages (and the named destinations)
     * @return the lookup table to retrieve newly created annotations based on the old ones
     */
    public static LookupTable<PDAnnotation> processAnnotations(LookupTable<PDPage> relevantPages,
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
                                LOG.trace("Removing not relevant link annotation");
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
                            LOG.trace("Updated annotation page reference with the looked up page");
                        }
                    }
                }
                relevantPages.lookup(page).setAnnotations(keptAnnotations);
            } catch (IOException e) {
                LOG.warn("Failed to process annotations for page", e);
            }
        }
        return annotationsLookup;
    }

    private static PDDestination getDestinationFrom(PDAnnotationLink link, PDDocument pageOwner) {
        try {
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
        } catch(Exception e){
            LOG.warn("Failed to get destination for annotation", e);
            return null;
        }
    }
}
