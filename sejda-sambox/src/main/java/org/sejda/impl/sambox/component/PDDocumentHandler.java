/*
 * Created on 29/ago/2011
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
package org.sejda.impl.sambox.component;

import static org.sejda.impl.sambox.util.ViewerPreferencesUtils.getPageLayout;
import static org.sejda.impl.sambox.util.ViewerPreferencesUtils.getPageMode;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.sejda.sambox.cos.COSDictionary;
import org.sejda.sambox.output.WriteOption;
import org.sejda.sambox.pdmodel.PDDocument;
import org.sejda.sambox.pdmodel.PDDocumentInformation;
import org.sejda.sambox.pdmodel.PDPage;
import org.sejda.sambox.pdmodel.interactive.viewerpreferences.PDViewerPreferences;
import org.sejda.core.Sejda;
import org.sejda.model.exception.TaskException;
import org.sejda.model.exception.TaskIOException;
import org.sejda.model.pdf.PdfVersion;
import org.sejda.model.pdf.viewerpreference.PdfPageLayout;
import org.sejda.model.pdf.viewerpreference.PdfPageMode;
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
    public PDDocumentHandler(PDDocument document) throws TaskIOException {
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
            List<WriteOption> options = new ArrayList<WriteOption>();
            if(compress){
                options.add(WriteOption.COMPRESS_STREAMS);
            }
            document.writeTo(file, options.toArray(new WriteOption[options.size()]));
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
    public PDPage importPage(PDPage page) throws TaskIOException {
        PDPage imported;
        try {
            imported = document.importPage(page);
        } catch (IOException e) {
            throw new TaskIOException("An error occurred copying the page.", e);
        }
        imported.setCropBox(page.getCropBox());
        imported.setMediaBox(page.getMediaBox());
        imported.setResources(page.getResources());
        imported.setRotation(page.getRotation());

        return imported;
    }

    /**
     * Moves designated page to the end of the document.
     *
     * @param oldPageNumber 1-based page number
     */
    public void movePageToDocumentEnd(int oldPageNumber) {
        if(oldPageNumber == document.getNumberOfPages()) return;

        PDPage page = getPage(oldPageNumber);
        document.addPage(page);
        document.removePage(oldPageNumber - 1);
    }

    public PDPage getPage(int pageNumber) {
        return document.getDocumentCatalog().getPages().get(pageNumber - 1);
    }

    public void initialiseBasedOn(PDDocumentHandler other) {
        setDocumentInformation(other.getUnderlyingPDDocument().getDocumentInformation());
        setViewerPreferences(other.getViewerPreferences());
        getUnderlyingPDDocument().getDocumentCatalog().setPageLayout(
                other.getUnderlyingPDDocument().getDocumentCatalog().getPageLayout());
        getUnderlyingPDDocument().getDocumentCatalog().setPageMode(
                other.getUnderlyingPDDocument().getDocumentCatalog().getPageMode());
        setCreatorOnPDDocument();
    }


}
