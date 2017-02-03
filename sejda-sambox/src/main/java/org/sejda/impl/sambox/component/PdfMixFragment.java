/*
 * Created on 26 ago 2016
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

import static org.sejda.impl.sambox.component.SignatureClipper.clipSignatures;

import java.io.Closeable;
import java.io.IOException;
import java.util.LinkedList;

import org.sejda.common.LookupTable;
import org.sejda.model.exception.TaskIOException;
import org.sejda.model.exception.TaskPermissionsException;
import org.sejda.model.input.PdfMixInput;
import org.sejda.model.pdf.encryption.PdfAccessPermission;
import org.sejda.sambox.pdmodel.PDPage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Fragment of a mix task. It has inputs and current status of the fragment in the mix process.
 * 
 * @author Andrea Vacondio
 *
 */
class PdfMixFragment implements Closeable {

    private static final Logger LOG = LoggerFactory.getLogger(PdfAlternateMixer.class);

    private LookupTable<PDPage> lookups = new LookupTable<>();
    private PDDocumentHandler handler;
    private PdfMixInput input;
    private LinkedList<Integer> pages;

    private PdfMixFragment(PdfMixInput input, PDDocumentHandler handler) {
        this.pages = new LinkedList<>(input.getPages(handler.getNumberOfPages()));
        this.handler = handler;
        this.input = input;
    }

    public PDPage nextPage() {
        if (input.isReverse()) {
            return handler.getPage(pages.removeLast());
        }
        return handler.getPage(pages.removeFirst());
    }

    public boolean hasNextPage() {
        return !pages.isEmpty();
    }

    public int getNumberOfPages() {
        return handler.getNumberOfPages();
    }

    public int getStep() {
        return input.getStep();
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
    public static PdfMixFragment newInstance(PdfMixInput input) throws TaskIOException, TaskPermissionsException {
        LOG.debug("Opening input {} with step {} and reverse {}", input.getSource(), input.getStep(),
                input.isReverse());
        PDDocumentHandler documentHandler = input.getSource().open(new DefaultPdfSourceOpener());
        documentHandler.getPermissions().ensurePermission(PdfAccessPermission.ASSEMBLE);
        return new PdfMixFragment(input, documentHandler);
    }
}
