/*
 * Created on 26 ago 2016
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

import org.sejda.commons.LookupTable;
import org.sejda.model.exception.TaskIOException;
import org.sejda.model.exception.TaskPermissionsException;
import org.sejda.model.input.PdfMixInput;
import org.sejda.model.input.PdfSource;
import org.sejda.model.pdf.encryption.PdfAccessPermission;
import org.sejda.model.task.TaskExecutionContext;
import org.sejda.sambox.pdmodel.PDPage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Closeable;
import java.io.IOException;
import java.util.LinkedList;

import static org.sejda.impl.sambox.component.SignatureClipper.clipSignatures;

/**
 * Fragment of a mix task. It has inputs and current status of the fragment in the mix process.
 * 
 * @author Andrea Vacondio
 *
 */
class PdfMixFragment implements Closeable {

    private static final Logger LOG = LoggerFactory.getLogger(PdfMixFragment.class);

    private LookupTable<PDPage> lookups = new LookupTable<>();
    private PDDocumentHandler handler;
    private PdfMixInput input;
    private LinkedList<Integer> pages;
    private boolean hasNotReachedTheEnd = true;

    private PdfMixFragment(PdfMixInput input, PDDocumentHandler handler) {
        this.handler = handler;
        this.input = input;
        populatePages();
    }
    
    private void populatePages() {
        this.pages = new LinkedList<>(input.getPages(handler.getNumberOfPages()));
    }

    private void populatePagesIfRequired() {
        if(pages.isEmpty()) {
            this.hasNotReachedTheEnd = false;
            if(input.isRepeatForever()) {
                populatePages();    
            }
        }
    }

    public PDPage nextPage() {
        PDPage result;
        
        if (input.isReverse()) {
            result = handler.getPage(pages.removeLast());
        } else {
            result = handler.getPage(pages.removeFirst());
        }
        
        populatePagesIfRequired();
        return result;
    }

    public boolean hasNextPage() {
        return !pages.isEmpty();
    }
    
    public boolean hasNotReachedTheEnd() {
        return this.hasNotReachedTheEnd;
    }

    public int getNumberOfPages() {
        return handler.getNumberOfPages();
    }

    public int getStep() {
        return input.getStep();
    }

    public PdfSource<?> source() {
        return input.getSource();
    }

    public void addLookupEntry(PDPage current, PDPage importPage) {
        lookups.addLookupEntry(current, importPage);
    }

    /**
     * Removes unnecessary annotations and updates the relevant ones with the new generated pages so that link annotations, etc are poiting to valid pages
     */
    public void saintizeAnnotations() {
        clipSignatures(new AnnotationsDistiller(handler.getUnderlyingPDDocument()).retainRelevantAnnotations(lookups)
                .values());
    }

    @Override
    public void close() throws IOException {
        handler.close();
        lookups.clear();
    }

    /**
     * @param input
     * @return a new fragment from the given input
     * @throws TaskIOException
     * @throws TaskPermissionsException
     */
    public static PdfMixFragment newInstance(PdfMixInput input, TaskExecutionContext executionContext) throws TaskIOException, TaskPermissionsException {
        LOG.debug("Opening input {} with step {} and reverse {}", input.getSource(), input.getStep(),
                input.isReverse());
        PDDocumentHandler documentHandler = input.getSource().open(new DefaultPdfSourceOpener(executionContext));
        documentHandler.getPermissions().ensurePermission(PdfAccessPermission.ASSEMBLE);
        return new PdfMixFragment(input, documentHandler);
    }
}
