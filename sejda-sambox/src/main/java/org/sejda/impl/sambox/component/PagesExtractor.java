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

import static org.sejda.core.notification.dsl.ApplicationEventsNotifier.notifyEvent;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Set;

import org.sejda.common.ComponentsUtility;
import org.sejda.model.exception.TaskException;
import org.sejda.model.pdf.PdfVersion;
import org.sejda.model.task.NotifiableTaskMetadata;
import org.sejda.sambox.pdmodel.PDDocument;
import org.sejda.sambox.pdmodel.PDPage;
import org.sejda.sambox.pdmodel.interactive.action.PDAction;
import org.sejda.sambox.pdmodel.interactive.action.PDActionGoTo;
import org.sejda.sambox.pdmodel.interactive.annotation.PDAnnotation;
import org.sejda.sambox.pdmodel.interactive.annotation.PDAnnotationLink;
import org.sejda.sambox.pdmodel.interactive.documentnavigation.destination.PDDestination;
import org.sejda.sambox.pdmodel.interactive.documentnavigation.destination.PDPageDestination;
import org.sejda.sambox.pdmodel.interactive.documentnavigation.outline.PDDocumentOutline;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Component that retains pages from a given existing {@link PDDocument} and saves a new document containing retained pages and an outline that patches the new document.
 * 
 * @author Andrea Vacondio
 *
 */
public class PagesExtractor implements Closeable {

    private static final Logger LOG = LoggerFactory.getLogger(PagesExtractor.class);

    private OutlineMerger outlineMerger;
    private PDDocumentOutline outline;
    private PDDocument originalDocument;
    private PDDocumentHandler destinationDocument;

    public PagesExtractor(PDDocument origin) {
        this.originalDocument = origin;
        init();
    }

    private void init() {
        this.outlineMerger = new OutlineMerger(originalDocument);
        this.outline = new PDDocumentOutline();
        this.destinationDocument = new PDDocumentHandler();
        this.destinationDocument.initialiseBasedOn(originalDocument);
    }

    public void retain(Set<Integer> pages, NotifiableTaskMetadata taskMetadata) {
        int currentStep = 0;
        for (Integer page : pages) {
            retain(page);
            notifyEvent(taskMetadata).stepsCompleted(++currentStep).outOf(pages.size());
        }
    }

    public void retain(int page) {
        PDPage existingPage = originalDocument.getPage(page - 1);
        destinationDocument.addPage(existingPage);
        outlineMerger.addRelevantPage(existingPage);
        LOG.trace("Imported page number {}", page);
        processAnnotations(existingPage);
    }

    private void processAnnotations(PDPage imported) {
        try {
            List<PDAnnotation> annotations = imported.getAnnotations();
            for (PDAnnotation annotation : annotations) {
                if (annotation instanceof PDAnnotationLink) {
                    PDAnnotationLink link = (PDAnnotationLink) annotation;
                    PDDestination destination = link.getDestination();
                    if (destination == null && link.getAction() != null) {
                        PDAction action = link.getAction();
                        if (action instanceof PDActionGoTo) {
                            destination = ((PDActionGoTo) action).getDestination();
                        }
                    }
                    if (destination instanceof PDPageDestination) {
                        // TODO preserve links to pages within the splitted result
                        ((PDPageDestination) destination).setPage(null);
                    }
                }
                // TODO preserve links to pages within the splitted result
                annotation.setPage(null);
            }
        } catch (IOException e) {
            LOG.warn("Failed to process annotations for page");
        }
    }

    public void setVersion(PdfVersion version) {
        destinationDocument.setVersionOnPDDocument(version);
    }

    public void setCompress(boolean compress) {
        destinationDocument.setCompress(compress);
    }

    public void save(File file) throws TaskException {
        outlineMerger.mergeRelevantOutlineTo(outline);
        if (outline.hasChildren()) {
            destinationDocument.setDocumentOutline(outline);
        }
        destinationDocument.savePDDocument(file);
    }

    public void close() {
        ComponentsUtility.nullSafeCloseQuietly(destinationDocument);
        outlineMerger = null;
    }

    protected PDDocumentHandler destinationDocument() {
        return destinationDocument;
    }

    /**
     * Resets the component making it ready to start a new extractions from the original document
     */
    public void reset() {
        close();
        init();
    }
}
