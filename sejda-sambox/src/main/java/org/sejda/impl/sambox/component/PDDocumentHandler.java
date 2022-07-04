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

import static java.util.Optional.ofNullable;
import static org.sejda.impl.sambox.util.ViewerPreferencesUtils.getPageLayout;
import static org.sejda.impl.sambox.util.ViewerPreferencesUtils.getPageMode;

import java.awt.image.BufferedImage;
import java.io.Closeable;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Calendar;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.sejda.core.Sejda;
import org.sejda.impl.sambox.util.FontUtils;
import org.sejda.impl.sambox.util.PageLabelUtils;
import org.sejda.model.encryption.EncryptionAtRestPolicy;
import org.sejda.model.encryption.NoEncryptionAtRest;
import org.sejda.model.exception.TaskException;
import org.sejda.model.exception.TaskIOException;
import org.sejda.model.image.ImageColorType;
import org.sejda.model.pdf.PdfVersion;
import org.sejda.model.pdf.label.PdfPageLabel;
import org.sejda.model.pdf.viewerpreference.PdfPageLayout;
import org.sejda.model.pdf.viewerpreference.PdfPageMode;
import org.sejda.sambox.cos.COSDictionary;
import org.sejda.sambox.cos.COSName;
import org.sejda.sambox.encryption.StandardSecurity;
import org.sejda.sambox.output.WriteOption;
import org.sejda.sambox.pdmodel.PDDocument;
import org.sejda.sambox.pdmodel.PDDocumentCatalog;
import org.sejda.sambox.pdmodel.PDDocumentInformation;
import org.sejda.sambox.pdmodel.PDPage;
import org.sejda.sambox.pdmodel.PDPageTree;
import org.sejda.sambox.pdmodel.PageLayout;
import org.sejda.sambox.pdmodel.PageMode;
import org.sejda.sambox.pdmodel.common.PDRectangle;
import org.sejda.sambox.pdmodel.interactive.documentnavigation.outline.PDDocumentOutline;
import org.sejda.sambox.pdmodel.interactive.form.PDAcroForm;
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

    public static final String SAMBOX_USE_ASYNC_WRITER = "sejda.sambox.asyncwriter";

    private static final Logger LOG = LoggerFactory.getLogger(PDDocumentHandler.class);
    private static final WriteOption[] COMPRESSED_OPTS = new WriteOption[] { WriteOption.COMPRESS_STREAMS,
            WriteOption.OBJECT_STREAMS, WriteOption.XREF_STREAM };

    private PDDocument document;
    private PDDocumentAccessPermission permissions;
    private Set<WriteOption> writeOptions = new HashSet<>();
    private boolean updateProducerModifiedDate = true;

    /**
     * Creates a new handler using the given document as underlying {@link PDDocument}.
     *
     * @param document
     */
    public PDDocumentHandler(PDDocument document) {
        if (document == null) {
            throw new IllegalArgumentException("PDDocument cannot be null.");
        }

        if(Boolean.getBoolean(Sejda.PERFORM_EAGER_ASSERTIONS_PROPERTY_NAME)) {
            document.assertNumberOfPagesIsAccurate();
        }
        
        this.document = document;
        permissions = new PDDocumentAccessPermission(document);
    }

    /**
     * Creates a new handler with an empty underlying {@link PDDocument}.
     */
    public PDDocumentHandler() {
        this.document = new PDDocument();
        permissions = new PDDocumentAccessPermission(document);
        COSDictionary pieceInfo = new COSDictionary();
        COSDictionary pieceLastMod = new COSDictionary();
        pieceLastMod.setDate(COSName.LAST_MODIFIED, Calendar.getInstance());
        pieceInfo.setItem(new String(new byte[] { 0x73, 0x6A, 0x64, 0x61, 0x5F }), pieceLastMod);
        this.document.getDocumentCatalog().getCOSObject().setItem(COSName.PIECE_INFO, pieceInfo);
    }

    /**
     * set the creator on the underlying {@link PDDocument}
     */
    public void setCreatorOnPDDocument() {
        if(!Sejda.CREATOR.isEmpty()) {
            document.getDocumentInformation().setCreator(Sejda.CREATOR);
        }
    }

    /**
     * Set the document information on the underlying {@link PDDocument}
     *
     * @param info
     */
    public void setDocumentInformation(PDDocumentInformation info) {
        document.setDocumentInformation(info);
    }

    public void setDocumentTitle(String title) {
        PDDocumentInformation info = document.getDocumentInformation();
        info.setTitle(title);
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
        setPageLayout(getPageLayout(layout));
        LOG.trace("Page layout set to '{}'", layout);
    }

    /**
     * Sets the given page mode on the underlying {@link PDDocument}.
     *
     * @param mode
     */
    public void setPageModeOnDocument(PdfPageMode mode) {
        setPageMode(getPageMode(mode));
        LOG.trace("Page mode set to '{}'", mode);
    }

    /**
     * Sets the page labels on the underlying {@link PDDocument}.
     *
     * @param labels
     */
    public void setPageLabelsOnDocument(Map<Integer, PdfPageLabel> labels) {
        document.getDocumentCatalog().setPageLabels(PageLabelUtils.getLabels(labels, getNumberOfPages()));
        LOG.trace("Page labels set");
    }

    /**
     * Sets the version on the underlying {@link PDDocument}.
     *
     * @param version
     */
    public void setVersionOnPDDocument(PdfVersion version) {
        if (version != null) {
            document.setVersion(version.getVersionString());
            LOG.trace("Version set to '{}'", version);
        }
    }

    /**
     * Adds the given {@link WriteOption}s to be used when the document is saved
     *
     * @param opts
     */
    public void addWriteOption(WriteOption... opts) {
        for (WriteOption opt : opts) {
            this.writeOptions.add(opt);
        }
    }

    /**
     * Removes the given {@link WriteOption}s to be used when the document is saved
     *
     * @param opts
     */
    public void removeWriteOption(WriteOption... opts) {
        for (WriteOption opt : opts) {
            this.writeOptions.remove(opt);
        }
    }

    /**
     * sets or remove compression options to be used when the resulting document is written
     */
    public void setCompress(boolean compress) {
        if (compress) {
            addWriteOption(COMPRESSED_OPTS);
        } else {
            removeWriteOption(COMPRESSED_OPTS);
        }
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

    @Override
    public void close() throws IOException {
        document.close();
        FontUtils.clearLoadedFontCache(document);
    }

    // convenience method
    public void savePDDocument(File file) throws TaskException {
        savePDDocument(file, null, NoEncryptionAtRest.INSTANCE);
    }

    /**
     * Saves the underlying {@link PDDocument} to the given file.
     *
     * @param file
     * @throws TaskException
     */
    public void savePDDocument(File file, EncryptionAtRestPolicy encryptionAtRestSecurity) throws TaskException {
        savePDDocument(file, null, encryptionAtRestSecurity);
    }

    /**
     * Saves the underlying {@link PDDocument} to the given file and using the given standard security.
     *
     * @param file
     * @param security
     * @throws TaskException
     */
    public void savePDDocument(File file, StandardSecurity security, EncryptionAtRestPolicy encryptionAtRestSecurity)
            throws TaskException {
        try {
            if (Boolean.getBoolean(SAMBOX_USE_ASYNC_WRITER)) {
                this.addWriteOption(WriteOption.ASYNC_BODY_WRITE);
            }
            if(!updateProducerModifiedDate) {
                this.addWriteOption(WriteOption.NO_METADATA_PRODUCER_MODIFIED_DATE_UPDATE);
            }

            if (encryptionAtRestSecurity instanceof NoEncryptionAtRest) {
                LOG.trace("Saving document to {} using options {}", file, writeOptions);
                document.writeTo(file, security, writeOptions.stream().toArray(WriteOption[]::new));
            } else {
                LOG.trace("Saving document to {} using options {}", file, writeOptions);
                document.writeTo(encryptionAtRestSecurity.encrypt(new FileOutputStream(file)), security,
                        writeOptions.stream().toArray(WriteOption[]::new));
            }
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

    public PDDocumentCatalog catalog() {
        return document.getDocumentCatalog();
    }

    /**
     * Creates a copy of the given page and adds it to the underlying {@link PDDocument}
     *
     * @param page
     * @return The newly created page
     */
    public PDPage importPage(PDPage page) {
        PDPage imported = new PDPage(page.getCOSObject().duplicate());
        imported.setCropBox(page.getCropBox());
        imported.setMediaBox(page.getMediaBox());
        imported.setBleedBox(page.getBleedBox());

        imported.setResources(page.getResources());
        imported.setRotation(page.getRotation());
        // we don't retain the /Threads key in the Catalog so it doesn't make sense to keep /B
        imported.getCOSObject().removeItem(COSName.B);
        imported.sanitizeDictionary();
        return addPage(imported);
    }

    /**
     * Adds the given page to the underlying {@link PDDocument}
     *
     * @param page
     * @return the page
     */
    public PDPage addPage(PDPage page) {
        document.addPage(page);
        return page;
    }

    /**
     * Removes the given page to the underlying {@link PDDocument}
     *
     * @param pageNumber
     */
    public void removePage(int pageNumber) {
        document.removePage(pageNumber - 1);
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
        return document.getPage(pageNumber - 1);
    }

    public PDPageTree getPages() {
        return document.getPages();
    }

    public void initialiseBasedOn(PDDocument other) {
        setDocumentInformation(other.getDocumentInformation());
        setViewerPreferences(other.getDocumentCatalog().getViewerPreferences());
        if (other.getDocumentCatalog().getCOSObject().containsKey(COSName.PAGE_LAYOUT)) {
            setPageLayout(other.getDocumentCatalog().getPageLayout());
        }
        if (other.getDocumentCatalog().getCOSObject().containsKey(COSName.PAGE_MODE)) {
            setPageMode(other.getDocumentCatalog().getPageMode());
        }

        document.getDocumentCatalog().setLanguage(other.getDocumentCatalog().getLanguage());
        // TODO named resources?
        // TODO not sure about this, maybe an option to let the user decide if he wants to bring in metadata?
        // getUnderlyingPDDocument().getDocumentCatalog().setMetadata(other.getDocumentCatalog().getMetadata());
        setCreatorOnPDDocument();
        // TODO maybe we bring in the open action?
    }

    public BufferedImage renderImage(int pageNumber, int dpi, ImageColorType type) throws TaskException {
        try {
            PDFRenderer pdfRenderer = new PDFRenderer(document);
            return pdfRenderer.renderImageWithDPI(pageNumber - 1, dpi, toSamboxImageType(type));
        } catch (IOException ex) {
            LOG.error("Failed to render page " + pageNumber, ex);
            throw new TaskException("Failed to render page " + pageNumber, ex);
        }
    }

    private ImageType toSamboxImageType(ImageColorType colorType) {
        for (ImageType type : ImageType.values()) {
            if (type.toBufferedImageType() == colorType.getBufferedImageType()) {
                return type;
            }
        }

        throw new RuntimeException("Could not find a suitable image type for color type:" + colorType);
    }

    public void setDocumentOutline(PDDocumentOutline outline) {
        document.getDocumentCatalog().setDocumentOutline(outline);
    }

    public void setDocumentAcroForm(PDAcroForm acroForm) {
        document.getDocumentCatalog().setAcroForm(acroForm);
    }

    private void setPageMode(PageMode pageMode) {
        document.getDocumentCatalog().setPageMode(pageMode);
    }

    private void setPageLayout(PageLayout pageLayout) {
        document.getDocumentCatalog().setPageLayout(pageLayout);
    }

    /**
     * Adds a blank page if the current total pages number is odd
     *
     * @param mediaBox
     *            media box size for the blank page
     * @return the added page or null if no page has been added
     */
    public PDPage addBlankPageIfOdd(PDRectangle mediaBox) {
        if (document.getNumberOfPages() % 2 != 0) {
            return addBlankPage(mediaBox);
        }
        return null;
    }

    public PDPage addBlankPage(PDRectangle mediaBox) {
        LOG.debug("Adding blank page");
        return addPage(new PDPage(ofNullable(mediaBox).orElse(PDRectangle.LETTER)));
    }

    public PDPage addBlankPageAfter(int pageNumber) {
        PDPage target = document.getPage(pageNumber - 1);
        PDPage result = new PDPage(target.getMediaBox().rotate(target.getRotation()));
        document.getPages().insertAfter(result, target);
        return result;
    }

    public PDPage addBlankPageBefore(int pageNumber) {
        PDPage target = document.getPage(pageNumber - 1);
        PDPage result = new PDPage(target.getMediaBox().rotate(target.getRotation()));
        document.getPages().insertBefore(result, target);
        return result;
    }

    public boolean isUpdateProducerModifiedDate() {
        return updateProducerModifiedDate;
    }

    public void setUpdateProducerModifiedDate(boolean updateProducerModifiedDate) {
        this.updateProducerModifiedDate = updateProducerModifiedDate;
    }
}
