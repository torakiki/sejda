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

import static java.util.Optional.ofNullable;
import static org.sejda.commons.util.IOUtils.closeQuietly;
import static org.sejda.core.notification.dsl.ApplicationEventsNotifier.notifyEvent;
import static org.sejda.impl.sambox.component.SignatureClipper.clipSignatures;

import java.io.Closeable;
import java.io.File;
import java.util.Set;
import java.util.function.Consumer;

import org.sejda.commons.LookupTable;
import org.sejda.impl.sambox.component.optimization.NameResourcesDuplicator;
import org.sejda.impl.sambox.component.optimization.ResourceDictionaryCleaner;
import org.sejda.impl.sambox.component.optimization.ResourcesHitter;
import org.sejda.model.encryption.EncryptionAtRestPolicy;
import org.sejda.model.exception.TaskException;
import org.sejda.model.exception.TaskExecutionException;
import org.sejda.model.pdf.PdfVersion;
import org.sejda.model.pdf.form.AcroFormPolicy;
import org.sejda.model.task.TaskExecutionContext;
import org.sejda.sambox.pdmodel.PDDocument;
import org.sejda.sambox.pdmodel.PDPage;
import org.sejda.sambox.pdmodel.PageNotFoundException;
import org.sejda.sambox.pdmodel.interactive.annotation.PDAnnotation;
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
    private AcroFormsMerger acroFormsMerger;
    private PDDocument origin;
    private PDDocumentHandler destinationDocument;
    private LookupTable<PDPage> pagesLookup = new LookupTable<>();

    public PagesExtractor(PDDocument origin) {
        this.origin = origin;
        init();
    }

    private void init() {
        this.outlineMerger = new OutlineDistiller(origin);
        this.destinationDocument = new PDDocumentHandler();
        this.destinationDocument.initialiseBasedOn(origin);
        this.acroFormsMerger = new AcroFormsMerger(AcroFormPolicy.MERGE,
                this.destinationDocument.getUnderlyingPDDocument());
    }

    public void retain(Set<Integer> pages, TaskExecutionContext executionContext) throws TaskExecutionException {
        int currentStep = 0;
        for (Integer page : pages) {
            retain(page, executionContext);
            notifyEvent(executionContext.notifiableTaskMetadata()).stepsCompleted(++currentStep).outOf(pages.size());
        }
    }

    public void retain(int page, TaskExecutionContext executionContext) throws TaskExecutionException {
        try {
            PDPage existingPage = origin.getPage(page - 1);
            pagesLookup.addLookupEntry(existingPage, destinationDocument.importPage(existingPage));
            LOG.trace("Imported page number {}", page);
        } catch (PageNotFoundException e) {
            executionContext.assertTaskIsLenient(e);
            notifyEvent(executionContext.notifiableTaskMetadata())
                    .taskWarning(String.format("Page %d was skipped, could not be processed", page), e);
        }
    }

    public void setVersion(PdfVersion version) {
        destinationDocument.setVersionOnPDDocument(version);
    }

    public void setCompress(boolean compress) {
        destinationDocument.setCompress(compress);
    }

    public void optimize() {
        LOG.trace("Optimizing document");
        Consumer<PDPage> hitter = new NameResourcesDuplicator().andThen(new ResourcesHitter());
        pagesLookup.values().forEach(hitter::accept);
        new ResourceDictionaryCleaner().accept(destinationDocument.getUnderlyingPDDocument());
    }

    public void save(File file, boolean discardOutline, EncryptionAtRestPolicy encryptionAtRestSecurity)
            throws TaskException {
        if (!discardOutline) {
            createOutline();
        }

        LookupTable<PDAnnotation> annotations = new AnnotationsDistiller(origin).retainRelevantAnnotations(pagesLookup);
        clipSignatures(annotations.values());

        acroFormsMerger.mergeForm(origin.getDocumentCatalog().getAcroForm(), annotations);

        ofNullable(acroFormsMerger.getForm()).filter(f -> !f.getFields().isEmpty()).ifPresent(f -> {
            LOG.debug("Adding generated AcroForm");
            destinationDocument.setDocumentAcroForm(f);
        });

        destinationDocument.savePDDocument(file, encryptionAtRestSecurity);
    }

    private void createOutline() {
        PDDocumentOutline outline = new PDDocumentOutline();
        outlineMerger.appendRelevantOutlineTo(outline, pagesLookup);
        if (outline.hasChildren()) {
            destinationDocument.setDocumentOutline(outline);
        }
    }

    @Override
    public void close() {
        closeQuietly(destinationDocument);
        pagesLookup.clear();
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
