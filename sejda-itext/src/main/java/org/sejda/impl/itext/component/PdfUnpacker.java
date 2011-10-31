/*
 * Created on 22/ago/2011
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
package org.sejda.impl.itext.component;

import static org.sejda.core.support.io.IOUtils.createTemporaryBuffer;
import static org.sejda.core.support.io.model.FileOutput.file;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.sejda.common.collection.NullSafeSet;
import org.sejda.core.support.io.MultipleOutputWriter;
import org.sejda.core.support.io.OutputWriters;
import org.sejda.model.exception.TaskException;
import org.sejda.model.exception.TaskIOException;
import org.sejda.model.output.TaskOutput;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lowagie.text.pdf.PRStream;
import com.lowagie.text.pdf.PdfArray;
import com.lowagie.text.pdf.PdfDictionary;
import com.lowagie.text.pdf.PdfName;
import com.lowagie.text.pdf.PdfNameTree;
import com.lowagie.text.pdf.PdfObject;
import com.lowagie.text.pdf.PdfReader;
import com.lowagie.text.pdf.PdfString;

/**
 * Component responsible for unpacking attachments attached to multiple pdf documents.
 * 
 * @author Andrea Vacondio
 * 
 */
public class PdfUnpacker {

    private static final Logger LOG = LoggerFactory.getLogger(PdfUnpacker.class);

    private MultipleOutputWriter outputWriter = OutputWriters.newMultipleOutputWriter();

    public void unpack(PdfReader reader) throws TaskException {
        if (reader == null) {
            throw new TaskException("Unable to unpack a null reader.");
        }
        LOG.debug("Unpacking started");
        Set<PdfDictionary> dictionaries = getAttachmentsDictionaries(reader);
        if (dictionaries.isEmpty()) {
            LOG.info("No attachments found.");
        } else {
            unpack(dictionaries);
        }
    }

    private void unpack(Set<PdfDictionary> dictionaries) throws TaskIOException {
        for (PdfDictionary dictionary : dictionaries) {
            PdfName type = (PdfName) PdfReader.getPdfObject(dictionary.get(PdfName.TYPE));
            if (PdfName.F.equals(type) || PdfName.FILESPEC.equals(type)) {
                PdfDictionary ef = (PdfDictionary) PdfReader.getPdfObject(dictionary.get(PdfName.EF));
                PdfString fn = (PdfString) PdfReader.getPdfObject(dictionary.get(PdfName.F));
                if (fn != null && ef != null) {
                    PRStream prs = (PRStream) PdfReader.getPdfObject(ef.get(PdfName.F));
                    if (prs != null) {
                        File tmpFile = copyToTemporaryFile(prs);
                        outputWriter.addOutput(file(tmpFile).name(fn.toUnicodeString()));
                    }
                }
            }
        }
    }

    private File copyToTemporaryFile(PRStream prs) throws TaskIOException {
        File tmpFile = createTemporaryBuffer();
        LOG.debug("Created output temporary buffer {}", tmpFile);

        ByteArrayInputStream inputStream = null;
        try {
            inputStream = new ByteArrayInputStream(PdfReader.getStreamBytes(prs));
            FileUtils.copyInputStreamToFile(inputStream, tmpFile);
            LOG.debug("Attachment unpacked to temporary buffer");
        } catch (IOException e) {
            throw new TaskIOException("Unable to copy attachment to temporary file.", e);
        } finally {
            IOUtils.closeQuietly(inputStream);
        }
        return tmpFile;
    }

    /**
     * Flush the unpacked files.
     * 
     * @param output
     * @param overwrite
     * @throws TaskIOException
     */
    public void flushOutputs(TaskOutput output, boolean overwrite) throws TaskIOException {
        outputWriter.flushOutputs(output, overwrite);
    }

    private Set<PdfDictionary> getAttachmentsDictionaries(PdfReader reader) {
        Set<PdfDictionary> retSet = new NullSafeSet<PdfDictionary>();
        retSet.addAll(getEmbeddedFilesDictionaries(reader));
        retSet.addAll(getFileAttachmentsDictionaries(reader));
        return retSet;
    }

    private Set<PdfDictionary> getEmbeddedFilesDictionaries(PdfReader reader) {
        Set<PdfDictionary> retSet = new NullSafeSet<PdfDictionary>();
        PdfDictionary catalog = reader.getCatalog();
        PdfDictionary names = (PdfDictionary) PdfReader.getPdfObject(catalog.get(PdfName.NAMES));
        if (names != null) {
            PdfDictionary embFiles = (PdfDictionary) PdfReader.getPdfObject(names.get(PdfName.EMBEDDEDFILES));
            if (embFiles != null) {
                @SuppressWarnings("unchecked")
                HashMap<String, PdfObject> embMap = PdfNameTree.readTree(embFiles);
                for (PdfObject value : embMap.values()) {
                    retSet.add((PdfDictionary) PdfReader.getPdfObject(value));
                }
            }
        }
        return retSet;
    }

    private Set<PdfDictionary> getFileAttachmentsDictionaries(PdfReader reader) {
        Set<PdfDictionary> retSet = new NullSafeSet<PdfDictionary>();
        for (int k = 1; k <= reader.getNumberOfPages(); ++k) {
            PdfArray annots = (PdfArray) PdfReader.getPdfObject(reader.getPageN(k).get(PdfName.ANNOTS));
            if (annots != null) {
                for (@SuppressWarnings("unchecked")
                Iterator<PdfObject> iter = annots.listIterator(); iter.hasNext();) {
                    PdfDictionary annot = (PdfDictionary) PdfReader.getPdfObject(iter.next());
                    PdfName subType = (PdfName) PdfReader.getPdfObject(annot.get(PdfName.SUBTYPE));
                    if (PdfName.FILEATTACHMENT.equals(subType)) {
                        retSet.add((PdfDictionary) PdfReader.getPdfObject(annot.get(PdfName.FS)));
                    }
                }
            }
        }
        return retSet;
    }
}
