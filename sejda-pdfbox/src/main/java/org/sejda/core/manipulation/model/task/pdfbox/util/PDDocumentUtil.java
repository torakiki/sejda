/*
 * Created on 02/nov/2010
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
package org.sejda.core.manipulation.model.task.pdfbox.util;

import java.io.IOException;

import org.apache.commons.lang.StringUtils;
import org.apache.pdfbox.exceptions.CryptographyException;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.encryption.AccessPermission;
import org.apache.pdfbox.pdmodel.encryption.BadSecurityHandlerException;
import org.apache.pdfbox.pdmodel.encryption.DecryptionMaterial;
import org.apache.pdfbox.pdmodel.encryption.StandardDecryptionMaterial;
import org.sejda.core.Sejda;
import org.sejda.core.exception.TaskIOException;
import org.sejda.core.exception.TaskPermissionsException;
import org.sejda.core.exception.TaskWrongPasswordException;
import org.sejda.core.manipulation.model.pdf.PdfVersion;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Utility class to deal with the {@link PDDocument} entity
 * 
 * @author Andrea Vacondio
 * 
 */
public final class PDDocumentUtil {

    private static final Logger LOG = LoggerFactory.getLogger(PDDocumentUtil.class);

    private PDDocumentUtil() {
        // on purpose
    }

    /**
     * Decrypt the input document if necessary
     * 
     * @param password
     * @param document
     * @throws BadSecurityHandlerException
     * @throws IOException
     * @throws CryptographyException
     * @throws TaskWrongPasswordException
     */
    public static void decryptPDDocumentIfNeeded(PDDocument document, String password)
            throws BadSecurityHandlerException, IOException, CryptographyException, TaskWrongPasswordException {
        if (document.isEncrypted()) {
            if (StringUtils.isNotBlank(password)) {
                DecryptionMaterial decryptionMaterial = new StandardDecryptionMaterial(password);
                document.openProtection(decryptionMaterial);
            } else {
                throw new TaskWrongPasswordException("Unable to open the document due to an empty password.");
            }
        }
    }

    /**
     * Ensures that the input document is opened with Owner permissions
     * 
     * @param document
     * @throws TaskPermissionsException
     */
    public static void ensureOwnerPermissions(PDDocument document) throws TaskPermissionsException {
        AccessPermission ap = document.getCurrentAccessPermission();
        if (!ap.isOwnerPermission()) {
            throw new TaskPermissionsException("Owner permission is required.");
        }
    }

    /**
     * Sets the sejda creator on the input document
     * 
     * @param document
     */
    public static void setCreatorOnPDDocument(PDDocument document) {
        if (document != null) {
            document.getDocumentInformation().setCreator(Sejda.CREATOR);
        }
    }

    /**
     * Sets the version on the input document
     * 
     * @param document
     * @param version
     */
    public static void setVersionOnPDDocument(PDDocument document, PdfVersion version) {
        if (document != null && version != null) {
            document.getDocument().setVersion((float) version.getVersionAsDouble());
            document.getDocument().setHeaderString(version.getVersionHeader());
        }
    }

    /**
     * Set compression of the XRef table on the document
     * 
     * @param document
     * @throws TaskIOException
     */
    public static void compressXrefStream(PDDocument document) throws TaskIOException {
        LOG.warn("Compression not yet supported by PDFbox");
    }

}
