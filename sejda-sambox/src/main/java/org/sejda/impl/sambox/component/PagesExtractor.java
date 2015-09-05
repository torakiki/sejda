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
import java.util.Set;

import org.sejda.common.ComponentsUtility;
import org.sejda.common.collection.NullSafeSet;
import org.sejda.model.exception.TaskException;
import org.sejda.model.pdf.PdfVersion;
import org.sejda.model.task.NotifiableTaskMetadata;
import org.sejda.sambox.pdmodel.PDDocument;
import org.sejda.sambox.pdmodel.PDPage;
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

    private OutlineDistiller outlineMerger;
    private PDDocument originalDocument;
    private PDDocumentHandler destinationDocument;
    private Set<PDPage> relevantPages = new NullSafeSet<>();

    public PagesExtractor(PDDocument origin) {
        this.originalDocument = origin;
        init();
    }

    private void init() {
        this.outlineMerger = new OutlineDistiller(originalDocument);
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
        relevantPages.add(existingPage);
        LOG.trace("Imported page number {}", page);
    }

    public void setVersion(PdfVersion version) {
        destinationDocument.setVersionOnPDDocument(version);
    }

    public void setCompress(boolean compress) {
        destinationDocument.setCompress(compress);
    }

    public void save(File file) throws TaskException {
        createOutline();
        AnnotationsDistiller.filterAnnotations(relevantPages, originalDocument);
        destinationDocument.savePDDocument(file);
    }

    private void createOutline() {
        PDDocumentOutline outline = new PDDocumentOutline();
        outlineMerger.appendRelevantOutlineTo(outline, relevantPages);
        if (outline.hasChildren()) {
            destinationDocument.setDocumentOutline(outline);
        }
    }

    public void close() {
        ComponentsUtility.nullSafeCloseQuietly(destinationDocument);
        relevantPages.clear();
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
