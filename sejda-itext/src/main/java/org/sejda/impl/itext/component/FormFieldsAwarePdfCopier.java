/*
 * Created on 15/ago/2011
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
package org.sejda.impl.itext.component;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.sejda.core.Sejda;
import org.sejda.model.exception.TaskException;
import org.sejda.model.exception.TaskIOException;
import org.sejda.model.pdf.PdfVersion;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lowagie.text.DocumentException;
import com.lowagie.text.pdf.PdfCopyFields;
import com.lowagie.text.pdf.PdfName;
import com.lowagie.text.pdf.PdfObject;
import com.lowagie.text.pdf.PdfPageLabels;
import com.lowagie.text.pdf.PdfReader;
import com.lowagie.text.pdf.PdfStream;
import com.lowagie.text.pdf.PdfString;

/**
 * {@link PdfCopier} implementation that includes form fields while coping pages and uses a {@link File} as output
 * 
 * @author Andrea Vacondio
 * 
 */
public class FormFieldsAwarePdfCopier implements PdfCopier {

    private static final Logger LOG = LoggerFactory.getLogger(FormFieldsAwarePdfCopier.class);

    private PdfCopyFields pdfCopy;
    private OutputStream outputStream;
    private int numberOfCopiedPages = 0;

    public FormFieldsAwarePdfCopier(File outputFile, PdfVersion version) throws TaskException {
        try {
            outputStream = new FileOutputStream(outputFile);
            if (version == null) {
                pdfCopy = new PdfCopyFields(outputStream);
            } else {
                pdfCopy = new PdfCopyFields(outputStream, version.getVersionAsCharacter());
            }
            pdfCopy.getWriter().getInfo().put(PdfName.CREATOR, new PdfString(Sejda.CREATOR, PdfObject.TEXT_UNICODE));
        } catch (FileNotFoundException e) {
            throw new TaskException(String.format("Unable to find the output file %s", outputFile.getPath()), e);
        } catch (DocumentException e) {
            throw new TaskException("An error occurred opening the PdfCopyFields.", e);
        }
    }

    public void close() {
        if (pdfCopy != null) {
            pdfCopy.close();
        }
        IOUtils.closeQuietly(outputStream);
    }

    public void addPage(PdfReader reader, int pageNumber) throws TaskException {
        try {
            pdfCopy.addDocument(reader, Collections.singletonList(Integer.valueOf(pageNumber)));
            numberOfCopiedPages++;
        } catch (DocumentException e) {
            throw new TaskException(String.format("An error occurred adding page %d.", pageNumber), e);
        } catch (IOException e) {
            throw new TaskIOException(String.format("An IO error occurred adding page %d.", pageNumber), e);
        }

    }

    public void addAllPages(PdfReader reader) throws TaskException {
        try {
            pdfCopy.addDocument(reader);
            numberOfCopiedPages += reader.getNumberOfPages();
        } catch (DocumentException e) {
            throw new TaskException("An error occurred adding all pages.", e);
        } catch (IOException e) {
            throw new TaskIOException("An IO error occurred adding all pages.", e);
        }
    }

    public void setCompression(boolean compress) {
        if (compress) {
            pdfCopy.setFullCompression();
            pdfCopy.getWriter().setCompressionLevel(PdfStream.BEST_COMPRESSION);
        }
    }

    public void setPageLabels(PdfPageLabels labels) {
        pdfCopy.getWriter().setPageLabels(labels);
    }

    public void freeReader(PdfReader reader) throws TaskIOException {
        try {
            pdfCopy.getWriter().freeReader(reader);
        } catch (IOException e) {
            throw new TaskIOException("An IO error occurred freeing the pdf reader.", e);
        }
    }

    public void setOutline(List<Map<String, Object>> outline) {
        if (outline != null && !outline.isEmpty()) {
            pdfCopy.setOutlines(outline);
        }
    }

    public void addBlankPage(PdfReader reader) {
        LOG.warn("Add blank page not implemented for FormFieldsAwarePdfCopier");
    }

    public void addBlankPageIfOdd(PdfReader reader) {
        if (reader.getNumberOfPages() % 2 != 0) {
            addBlankPage(reader);
        }
    }

    public int getNumberOfCopiedPages() {
        return numberOfCopiedPages;
    }

}
