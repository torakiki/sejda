/*
 * Created on 13/set/2011
 * Copyright 2011 by Andrea Vacondio (andrea.vacondio@gmail.com).
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); 
 * you may not use this file except in compliance with the License. 
 * You may obtain a copy of the License at 
 * 
 * http://www.apache.org/licenses/LICENSE-2.0 
 * 
 * Unless required by applicable law or agreed to in writing, software 
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. 
 * See the License for the specific language governing permissions and 
 * limitations under the License. 
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

    public PagesExtractor(PDDocumentHandler sourceDocumentHandler) throws TaskIOException {
        super();
        this.sourceDocumentHandler = sourceDocumentHandler;
    }

    public void extractPages(Set<Integer> pages, NotifiableTaskMetadata taskMetadata) throws TaskIOException {
        initializeDocument();
        doExtract(pages, taskMetadata);
    }

    private void initializeDocument() {
        setDocumentInformation(sourceDocumentHandler.getUnderlyingPDDocument().getDocumentInformation());
        setViewerPreferences(sourceDocumentHandler.getViewerPreferences());
        getUnderlyingPDDocument().getDocumentCatalog().setPageLayout(
                sourceDocumentHandler.getUnderlyingPDDocument().getDocumentCatalog().getPageLayout());
        getUnderlyingPDDocument().getDocumentCatalog().setPageMode(
                sourceDocumentHandler.getUnderlyingPDDocument().getDocumentCatalog().getPageMode());
        setCreatorOnPDDocument();
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
