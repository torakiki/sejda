/*
 * Created on 13/set/2011
 * Copyright 2011 by Andrea Vacondio (andrea.vacondio@gmail.com).
 * 
 * This file is part of the Sejda source code
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
package org.sejda.impl.pdfbox.component;

import static org.sejda.core.notification.dsl.ApplicationEventsNotifier.notifyEvent;

import java.io.IOException;
import java.util.List;
import java.util.Set;

import org.apache.pdfbox.pdmodel.PDPage;
import org.sejda.common.ComponentsUtility;
import org.sejda.model.exception.TaskIOException;
import org.sejda.model.task.NotifiableTaskMetadata;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Component responsible for extracting a set of pages from an input {@link PDDocumentHandler} to a new {@link PDDocumentHandler}.
 * 
 * @author Andrea Vacondio
 * 
 */
public class PagesExtractor extends PDDocumentHandler {

    private static final Logger LOG = LoggerFactory.getLogger(PagesExtractor.class);

    private PDDocumentHandler sourceDocumentHandler;

    public PagesExtractor(PDDocumentHandler sourceDocumentHandler) {
        this.sourceDocumentHandler = sourceDocumentHandler;
    }

    public void extractPages(Set<Integer> pages, NotifiableTaskMetadata taskMetadata) throws TaskIOException {
        initialiseBasedOn(sourceDocumentHandler);
        doExtract(pages, taskMetadata);
    }

    private void doExtract(Set<Integer> pages, NotifiableTaskMetadata taskMetadata) throws TaskIOException {
        @SuppressWarnings("unchecked")
        List<PDPage> existingPages = sourceDocumentHandler.getUnderlyingPDDocument().getDocumentCatalog().getAllPages();
        int currentStep = 0;
        for (Integer page : pages) {
            PDPage existingPage = existingPages.get(page - 1);
            importPage(existingPage);
            LOG.trace("Imported page number {}", page);
            notifyEvent(taskMetadata).stepsCompleted(++currentStep).outOf(pages.size());
        }
    }

    @Override
    public void close() throws IOException {
        super.close();
        ComponentsUtility.nullSafeCloseQuietly(sourceDocumentHandler);
    }

}
