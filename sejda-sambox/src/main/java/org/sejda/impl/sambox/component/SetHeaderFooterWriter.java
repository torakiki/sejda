/*
 * Copyright 2015 by Andrea Vacondio (andrea.vacondio@gmail.com).
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
package org.sejda.impl.sambox.component;

import static org.apache.commons.lang3.ObjectUtils.defaultIfNull;
import static org.sejda.core.notification.dsl.ApplicationEventsNotifier.notifyEvent;
import static org.sejda.impl.sambox.util.FontUtils.getStandardType1Font;

import java.io.Closeable;
import java.util.SortedSet;

import org.apache.commons.io.IOUtils;
import org.sejda.model.HorizontalAlign;
import org.sejda.model.VerticalAlign;
import org.sejda.model.exception.TaskExecutionException;
import org.sejda.model.exception.TaskIOException;
import org.sejda.model.parameter.SetHeaderFooterParameters;
import org.sejda.model.pdf.TextStampPattern;
import org.sejda.model.task.TaskExecutionContext;
import org.sejda.sambox.pdmodel.PageNotFoundException;
import org.sejda.sambox.pdmodel.font.PDFont;
import org.sejda.sambox.pdmodel.font.PDType1Font;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Component adding header or footer to PDF doc, according to the give SetHeaderFooterParameters parameters .
 * 
 * @author Andrea Vacondio
 * 
 */
public class SetHeaderFooterWriter implements Closeable {

    private static final Logger LOG = LoggerFactory.getLogger(SetHeaderFooterWriter.class);

    private PDDocumentHandler documentHandler;
    private PageTextWriter headerFooterWriter;
    private int totalPages;

    /**
     * @param documentHandler
     *            the document handler holding the document where we want to write the footer
     */
    public SetHeaderFooterWriter(PDDocumentHandler documentHandler) {
        this.documentHandler = documentHandler;
        this.headerFooterWriter = new PageTextWriter(documentHandler.getUnderlyingPDDocument());
        this.totalPages = documentHandler.getNumberOfPages();
    }

    public void write(String pattern, SetHeaderFooterParameters parameters, int currentFileCounter, String filename,
            TaskExecutionContext executionContext) throws TaskIOException, TaskExecutionException {
        PDFont font = defaultIfNull(getStandardType1Font(parameters.getFont()), PDType1Font.HELVETICA);
        Double fontSize = defaultIfNull(parameters.getFontSize(), 10d);

        HorizontalAlign hAlign = defaultIfNull(parameters.getHorizontalAlign(), HorizontalAlign.CENTER);
        VerticalAlign vAlign = defaultIfNull(parameters.getVerticalAlign(), VerticalAlign.BOTTOM);

        SortedSet<Integer> pages = parameters.getPages(totalPages);
        int userDefinedPageOffset = 0;
        if(parameters.getPageCountStartFrom() != null && !pages.isEmpty()) {
            // use the first page number and the provided pageCountStartFrom to determine the offset
            // Eg: first page is 10 and user's supplied page count start from is 12 => offset is 2
            // This is useful when we want to skip numbering the intro and start with 1,2,3 when the chapters start.
            userDefinedPageOffset = parameters.getPageCountStartFrom() - pages.first();
        }

        for (int pageNumber : pages) {
            // if user didn't override it, use document actual page numbering
            // otherwise shift by the user provided page numbering offset
            int toLabelPageNumber = pageNumber + userDefinedPageOffset;

            String batesSeq = null;
            if (parameters.getBatesSequence() != null) {
                batesSeq = parameters.getBatesSequence().next();
            }

            String label = new TextStampPattern().withPage(toLabelPageNumber, totalPages).withBatesSequence(batesSeq)
                    .withFileSequence(String.valueOf(currentFileCounter)).withFilename(filename)
                    .build(pattern);

            LOG.debug("Applying {} '{}' to document page {}", vAlign == VerticalAlign.BOTTOM ? "footer" : "header",
                    label, pageNumber);
            try {
                headerFooterWriter.write(documentHandler.getPage(pageNumber), hAlign, vAlign, label, font, fontSize,
                        parameters.getColor());
            } catch (PageNotFoundException e) {
                executionContext.assertTaskIsLenient(e);
                notifyEvent(executionContext.notifiableTaskMetadata())
                        .taskWarning(String.format("Page %d was skipped, could not be processed", pageNumber), e);
            }
        }
    }

    @Override
    public void close() {
        IOUtils.closeQuietly(documentHandler);
    }
}
