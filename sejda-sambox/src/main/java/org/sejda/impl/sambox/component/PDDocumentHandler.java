/*
 * Created on 29/ago/2011
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
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.sejda.impl.sambox.component;

import static org.sejda.impl.sambox.util.ViewerPreferencesUtils.getPageLayout;
import static org.sejda.impl.sambox.util.ViewerPreferencesUtils.getPageMode;
import static org.sejda.sambox.output.WriteOption.COMPRESS_STREAMS;
import static org.sejda.sambox.output.WriteOption.OBJECT_STREAMS;
import static org.sejda.sambox.output.WriteOption.XREF_STREAM;

import java.awt.image.BufferedImage;
import java.io.Closeable;
import java.io.File;
import java.io.IOException;

import org.sejda.core.Sejda;
import org.sejda.model.exception.TaskException;
import org.sejda.model.exception.TaskIOException;
import org.sejda.model.pdf.PdfVersion;
import org.sejda.model.pdf.viewerpreference.PdfPageLayout;
import org.sejda.model.pdf.viewerpreference.PdfPageMode;
import org.sejda.sambox.cos.COSDictionary;
import org.sejda.sambox.output.WriteOption;
import org.sejda.sambox.pdmodel.*;
import org.sejda.sambox.pdmodel.interactive.documentnavigation.outline.PDDocumentOutline;
import org.sejda.sambox.pdmodel.interactive.viewerpreferences.PDViewerPreferences;
import org.sejda.sambox.rendering.ImageType;
import org.sejda.sambox.rendering.PDFRenderer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Wrapper over a {@link PDDocument}.
 * 
 * @author Andrea Vacondio
 */
public class PDDocumentHandler implements Closeable {

    private static final Logger LOG = LoggerFactory.getLogger(PDDocumentHandler.class);

    private PDDocument document;
    private PDDocumentAccessPermission permissions;
    private boolean compress = true;

    /**
     * Creates a new handler using the given document as underlying {@link PDDocument}.
     * 
     * @param document
     * @throws TaskIOException
     */
    public PDDocumentHandler(PDDocument document) {
        if (document == null) {
            throw new IllegalArgumentException("PDDocument cannot be null.");
        }
        this.document = document;
        permissions = new PDDocumentAccessPermission(document);
    }

    /**
     * Creates a new handler with an empty underlying {@link PDDocument}.
     * 
     * @throws TaskIOException
     */
    public PDDocumentHandler() {
        this.document = new PDDocument();
        permissions = new PDDocumentAccessPermission(document);
    }

    /**
     * set the creator on the underlying {@link PDDocument}
     */
    public void setCreatorOnPDDocument() {
        document.getDocumentInformation().setCreator(Sejda.CREATOR);
    }

    /**
     * Set the document information on the underlying {@link PDDocument}
     * 
     * @param info
     */
    public void setDocumentInformation(PDDocumentInformation info) {
        document.setDocumentInformation(info);
    }

    /**
     * @return access permissions granted to this document.
     */
    public PDDocumentAccessPermission getPermissions() {
        return permissions;
    }

    /**
     * Sets the given page layout on the underlying {@link PDDocument}.
     * 
     * @param layout
     */
    public void setPageLayoutOnDocument(PdfPageLayout layout) {
        document.getDocumentCatalog().setPageLayout(getPageLayout(layout));
        LOG.trace("Page layout set to '{}'", layout);
    }

    /**
     * Sets the given page mode on the underlying {@link PDDocument}.
     * 
     * @param mode
     */
    public void setPageModeOnDocument(PdfPageMode mode) {
        document.getDocumentCatalog().setPageMode(getPageMode(mode));
        LOG.trace("Page mode set to '{}'", mode);
    }

    /**
     * Sets the version on the underlying {@link PDDocument}.
     * 
     * @param version
     */
    public void setVersionOnPDDocument(PdfVersion version) {
        if (version != null) {
            document.setVersion(version.getVersionAsDoubleString());
            LOG.trace("Version set to '{}'", version);
        }
    }

    /**
     * Set compression of the XRef table on underlying {@link PDDocument}.
     * 
     * @param compress
     */
    public void setCompress(boolean compress) {
        this.compress = compress;
    }

    /**
     * @return the view preferences for the underlying {@link PDDocument}.
     */
    public PDViewerPreferences getViewerPreferences() {
        PDViewerPreferences retVal = document.getDocumentCatalog().getViewerPreferences();
        if (retVal == null) {
            retVal = new PDViewerPreferences(new COSDictionary());
        }
        return retVal;
    }

    public void setViewerPreferences(PDViewerPreferences preferences) {
        document.getDocumentCatalog().setViewerPreferences(preferences);
    }

    public void close() throws IOException {
        document.close();
    }

    /**
     * Saves the underlying {@link PDDocument} removing security from it.
     * 
     * @param file
     * @throws TaskException
     */
    public void saveDecryptedPDDocument(File file) throws TaskException {
        savePDDocument(file, true);
    }

    /**
     * Saves the underlying {@link PDDocument} to the given file.
     * 
     * @param file
     * @throws TaskException
     */
    public void savePDDocument(File file) throws TaskException {
        savePDDocument(file, false);
    }

    private void savePDDocument(File file, boolean decrypted) throws TaskException {
        try {
            LOG.trace("Saving document to {}", file);
            WriteOption[] options = new WriteOption[0];
            if (compress) {
                options = new WriteOption[] { COMPRESS_STREAMS, XREF_STREAM, OBJECT_STREAMS };
            }
            document.writeTo(file, options);
        } catch (IOException e) {
            throw new TaskIOException("Unable to save to temporary file.", e);
        }
    }

    public int getNumberOfPages() {
        return document.getNumberOfPages();
    }

    public PDDocument getUnderlyingPDDocument() {
        return document;
    }

    /**
     * Import an existing page to the underlying {@link PDDocument}
     * 
     * @param page
     * @throws TaskIOException
     */
    public PDPage importPage(PDPage page) {
        PDPage imported = document.importPage(page);
        imported.setCropBox(page.getCropBox());
        imported.setMediaBox(page.getMediaBox());
        imported.setResources(page.getResources());
        imported.setRotation(page.getRotation());

        return imported;
    }

    public PDPage addPage(PDPage page) {
        document.addPage(page);
        return page;
    }

    /**
     * Moves designated page to the end of the document.
     *
     * @param oldPageNumber
     *            1-based page number
     */
    public void movePageToDocumentEnd(int oldPageNumber) {
        if (oldPageNumber == document.getNumberOfPages())
            return;

        PDPage page = getPage(oldPageNumber);
        document.addPage(page);
        document.removePage(oldPageNumber - 1);
    }

    public PDPage getPage(int pageNumber) {
        return document.getDocumentCatalog().getPages().get(pageNumber - 1);
    }

    public void initialiseBasedOn(PDDocument other) {
        setDocumentInformation(other.getDocumentInformation());
        setViewerPreferences(other.getDocumentCatalog().getViewerPreferences());
        getUnderlyingPDDocument().getDocumentCatalog().setPageLayout(other.getDocumentCatalog().getPageLayout());
        getUnderlyingPDDocument().getDocumentCatalog().setPageMode(other.getDocumentCatalog().getPageMode());
        setCreatorOnPDDocument();
    }

    public BufferedImage renderImage(int pageNumber, int dpi) throws TaskException {
        try {
            PDFRenderer pdfRenderer = new PDFRenderer(document);
            return pdfRenderer.renderImageWithDPI(pageNumber - 1, dpi, ImageType.RGB);
        } catch (IOException ex) {
            LOG.error("Failed to render page " + pageNumber, ex);
            throw new TaskException("Failed to render page " + pageNumber, ex);
        }
    }

    public void setDocumentOutline(PDDocumentOutline outline) {
        document.getDocumentCatalog().setDocumentOutline(outline);
    }

    public void setPageMode(PageMode pageMode) {
        document.getDocumentCatalog().setPageMode(pageMode);
    }

    public void setPageLayout(PageLayout pageLayout) {
        document.getDocumentCatalog().setPageLayout(pageLayout);
    }
}
