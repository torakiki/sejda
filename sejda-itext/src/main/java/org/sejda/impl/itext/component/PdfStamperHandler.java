/*
 * Created on 31/mag/2010
 *
 * Copyright 2010 by Andrea Vacondio (andrea.vacondio@gmail.com).
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
package org.sejda.impl.itext.component;

import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.sejda.impl.itext.util.TransitionUtils.getTransition;

import java.io.Closeable;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;

import org.apache.commons.io.IOUtils;
import org.sejda.core.Sejda;
import org.sejda.model.exception.TaskException;
import org.sejda.model.exception.TaskIOException;
import org.sejda.model.pdf.PdfMetadataKey;
import org.sejda.model.pdf.PdfVersion;
import org.sejda.model.pdf.transition.PdfPageTransition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lowagie.text.DocumentException;
import com.lowagie.text.pdf.PdfName;
import com.lowagie.text.pdf.PdfObject;
import com.lowagie.text.pdf.PdfReader;
import com.lowagie.text.pdf.PdfStamper;
import com.lowagie.text.pdf.PdfStream;
import com.lowagie.text.pdf.PdfTransition;

/**
 * Component responsible for handling operations related to a {@link PdfStamper} instance.
 * 
 * @author Andrea Vacondio
 * 
 */
public final class PdfStamperHandler implements Closeable {

    private static final Logger LOG = LoggerFactory.getLogger(PdfStamperHandler.class);

    private PdfStamper stamper = null;
    private FileOutputStream ouputStream = null;

    /**
     * Creates a new instance initializing the inner {@link PdfStamper} instance.
     * 
     * @param reader
     *            input reader
     * @param ouputFile
     *            {@link File} to stamp on
     * @param version
     *            version for the created stamper, if null the version number is taken from the input {@link PdfReader}
     * @throws TaskException
     *             in case of error
     */
    public PdfStamperHandler(PdfReader reader, File ouputFile, PdfVersion version) throws TaskException {
        try {
            ouputStream = new FileOutputStream(ouputFile);
            if (version != null) {
                stamper = new PdfStamper(reader, ouputStream, version.getVersionAsCharacter());
            } else {
                stamper = new PdfStamper(reader, ouputStream, reader.getPdfVersion());
            }
        } catch (DocumentException e) {
            throw new TaskException("An error occurred opening the PdfStamper.", e);
        } catch (IOException e) {
            throw new TaskIOException("An IO error occurred opening the PdfStamper.", e);
        }
    }

    /**
     * Enables compression if compress is true
     * 
     * @param compress
     */
    public void setCompressionOnStamper(boolean compress) {
        if (compress) {
            stamper.setFullCompression();
            stamper.getWriter().setCompressionLevel(PdfStream.BEST_COMPRESSION);
        }
    }

    public void close() throws IOException {
        try {
            stamper.close();
        } catch (DocumentException e) {
            LOG.error("Error closing the PdfStamper.", e);
        }
        IOUtils.closeQuietly(ouputStream);
    }

    /**
     * Adds the creator to the metadata taken from the reader and it sets it to the {@link PdfStamper}
     * 
     * @param reader
     */
    public void setCreatorOnStamper(PdfReader reader) {
        @SuppressWarnings("unchecked")
        HashMap<String, String> meta = reader.getInfo();
        setMetadataOnStamper(meta);
    }

    /**
     * Sets to the {@link PdfStamper} the input map as document metadata adding the creator to it.
     * 
     * @param meta
     */
    public void setMetadataOnStamper(HashMap<String, String> meta) {
        meta.put(PdfMetadataKey.CREATOR.getKey(), Sejda.CREATOR);
        stamper.setMoreInfo(meta);
    }

    /**
     * Sets the encryption for this document delegating encryption to the stamper.
     * 
     * @see PdfStamper#setEncryption(int, String, String, int)
     * @param encryptionType
     * @param userPassword
     * @param ownerPassword
     * @param permissions
     * @throws TaskException
     *             wraps the {@link DocumentException} that can be thrown by the stamper
     */
    public void setEncryptionOnStamper(int encryptionType, String userPassword, String ownerPassword, int permissions)
            throws TaskException {
        try {
            if (isBlank(ownerPassword)) {
                LOG.warn("Owner password not specified, using the user password as per Pdf reference 1.7, Chap. 3.5.2, Algorithm 3.3, Step 1.");
                stamper.setEncryption(encryptionType, userPassword, userPassword, permissions);
            } else {
                stamper.setEncryption(encryptionType, userPassword, ownerPassword, permissions);
            }
        } catch (DocumentException e) {
            throw new TaskException("An error occured while setting encryption on the document", e);
        }
    }

    /**
     * Applies the given transition to the given page.
     * 
     * @param page
     * @param transition
     */
    public void setTransitionOnStamper(Integer page, PdfPageTransition transition) {
        Integer transitionStyle = getTransition(transition.getStyle());
        if (transitionStyle != null) {
            stamper.setDuration(transition.getDisplayDuration(), page);
            stamper.setTransition(new PdfTransition(transitionStyle, transition.getTransitionDuration()), page);
        } else {
            LOG.warn("Transition {} not applied to page {}. Not supported by iText.", transition.getStyle(), page);
        }
    }

    /**
     * Sets the viewer preferences on the stamper
     * 
     * @see PdfStamper#setViewerPreferences(int)
     * @param preferences
     */
    public void setViewerPreferencesOnStamper(int preferences) {
        stamper.setViewerPreferences(preferences);
    }

    /**
     * adds the viewer preference to the stamper
     * 
     * @see PdfStamper#addViewerPreference(PdfName, PdfObject)
     * @param key
     * @param value
     */
    public void addViewerPreferenceOnStamper(PdfName key, PdfObject value) {
        stamper.addViewerPreference(key, value);
    }

    /**
     * 
     * @return the inner {@link PdfStamper} instance
     */
    public PdfStamper getStamper() {
        return stamper;
    }
}
