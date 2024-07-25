/*
 * Created on 03/set/2015
 * Copyright 2015 Sober Lemur S.r.l. and Sejda BV.
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

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static java.util.Optional.ofNullable;
import static org.sejda.commons.util.RequireUtils.requireNotNullArg;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.Set;

import org.sejda.commons.LookupTable;
import org.sejda.sambox.cos.COSName;
import org.sejda.sambox.pdmodel.PDDocument;
import org.sejda.sambox.pdmodel.PDPage;
import org.sejda.sambox.pdmodel.interactive.annotation.*;
import org.sejda.sambox.pdmodel.interactive.documentnavigation.destination.PDDestination;
import org.sejda.sambox.pdmodel.interactive.documentnavigation.destination.PDPageDestination;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Component that can distill pages annotations filtering out those pointing to irrelevant pages and updating the annotationsif necessasy.
 * 
 * @author Andrea Vacondio
 *
 */
public final class AnnotationsDistiller {
    private static final Logger LOG = LoggerFactory.getLogger(AnnotationsDistiller.class);

    private PDDocument document;
    private LookupTable<PDAnnotation> annotationsLookup = new LookupTable<>();

    /**
     * Document where pages and annotations come from, it's used to resolve named destinations.
     * 
     * @param document
     */
    public AnnotationsDistiller(PDDocument document) {
        requireNotNullArg(document, "Cannot process annotations for a null document");
        this.document = document;
    }

    /**
     * Removes from the given set of pages all the annotations pointing to a page that is not in the lookup (an irrelevant page) and replaces annotations pointing to an old page
     * with a new one pointing to the looked up page.
     * 
     * @param relevantPages
     * @return the lookup table to retrieve newly created annotations based on the old ones
     */
    public LookupTable<PDAnnotation> retainRelevantAnnotations(LookupTable<PDPage> relevantPages) {
        LOG.debug("Filtering annotations");
        for (PDPage page : relevantPages.keys()) {
            try {
                Set<PDAnnotation> keptAnnotations = new LinkedHashSet<>();
                for (PDAnnotation annotation : page.getAnnotations()) {
                    PDAnnotation mapped = annotationsLookup.lookup(annotation);
                    if (nonNull(mapped)) {
                        keptAnnotations.add(mapped);
                    } else {
                        // handle the scenario where the annotation page != actual render page
                        PDPage annotationPage = annotation.getPage();
                        if (annotationPage != null && !annotationPage.equals(page)) {
                            // inconsistent annotation, this creates problems; let's fix it
                            if(COSName.SCREEN.equals(annotation.getSubtype())) {
                                annotation.setPage(page);
                            } else {
                                annotation.setPage(null);
                            }
                        }
                        
                        
                        if (annotation instanceof PDAnnotationLink) {
                            processLinkAnnotation(relevantPages, keptAnnotations, (PDAnnotationLink) annotation, page);
                        } else {
                            processNonLinkAnnotation(relevantPages, keptAnnotations, annotation, page);
                        }
                    }
                }
                relevantPages.lookup(page).setAnnotations(new ArrayList<>(keptAnnotations));
            } catch (IOException e) {
                LOG.warn("Failed to process annotations for page", e);
            }
        }
        return annotationsLookup;
    }

    private void processLinkAnnotation(LookupTable<PDPage> relevantPages, Set<PDAnnotation> keptAnnotations,
            PDAnnotationLink annotation, PDPage p) throws IOException {
        PDPageDestination destination = getDestinationFrom(annotation);
        if (nonNull(destination)) {
            PDPage destPage = relevantPages.lookup(destination.getPage());
            if (nonNull(destPage)) {
                // relevant page dest
                PDAnnotationLink duplicate = (PDAnnotationLink) duplicate(annotation, p, relevantPages);
                duplicate.getCOSObject().removeItem(COSName.A);
                PDPageDestination newDestination = (PDPageDestination) PDDestination
                        .create(destination.getCOSObject().duplicate());
                newDestination.setPage(destPage);
                duplicate.setDestination(newDestination);
                keptAnnotations.add(duplicate);
            } else {
                LOG.trace("Removing not relevant link annotation");
            }
        } else {
            // not a page dest
            keptAnnotations.add(duplicate(annotation, p, relevantPages));
        }
    }

    private void processNonLinkAnnotation(LookupTable<PDPage> relevantPages, Set<PDAnnotation> keptAnnotations,
            PDAnnotation annotation, PDPage p) {
        if (isNull(p) || relevantPages.hasLookupFor(p)) {
            PDAnnotation duplicate = duplicate(annotation, p, relevantPages);
            if (duplicate instanceof PDAnnotationMarkup) {
                PDAnnotationPopup popup = ((PDAnnotationMarkup) duplicate).getPopup();
                if (nonNull(popup)) {
                    COSName subtype = popup.getCOSObject().getCOSName(COSName.SUBTYPE);
                    if (COSName.POPUP.equals(subtype)) {
                        PDAnnotationPopup popupDuplicate = ofNullable(
                                (PDAnnotationPopup) annotationsLookup.lookup(popup))
                                        .orElseGet(() -> (PDAnnotationPopup) duplicate(popup, p, relevantPages));
                        ((PDAnnotationMarkup) duplicate).setPopup(popupDuplicate);
                        if (nonNull(popupDuplicate.getParent())) {
                            popupDuplicate.setParent((PDAnnotationMarkup) duplicate);
                            LOG.trace("Popup parent annotation updated");
                        }
                        keptAnnotations.add(popupDuplicate);
                    } else {
                        ((PDAnnotationMarkup) duplicate).setPopup(null);
                        LOG.warn("Removed Popup annotation of unexpected subtype {}", subtype);
                    }
                }
            }
            keptAnnotations.add(duplicate);
        }
    }

    private PDAnnotation duplicate(PDAnnotation annotation, PDPage p, LookupTable<PDPage> relevantPages) {
        PDAnnotation duplicate = PDAnnotation.createAnnotation(annotation.getCOSObject().duplicate());
        if (nonNull(p)) {
            duplicate.setPage(relevantPages.lookup(p));
            LOG.trace("Updated annotation page reference with the looked up page");
        }
        annotationsLookup.addLookupEntry(annotation, duplicate);
        return duplicate;
    }

    private PDPageDestination getDestinationFrom(PDAnnotationLink link) {
        try {
            return link.resolveToPageDestination(document.getDocumentCatalog()).orElse(null);
        } catch (Exception e) {
            LOG.warn("Failed to get destination for annotation", e);
            return null;
        }
    }
}
