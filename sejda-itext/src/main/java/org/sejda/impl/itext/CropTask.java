/*
 * Created on 10/set/2011
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
package org.sejda.impl.itext;

import static org.sejda.common.ComponentsUtility.nullSafeCloseQuietly;
import static org.sejda.core.notification.dsl.ApplicationEventsNotifier.notifyEvent;
import static org.sejda.core.support.io.IOUtils.createTemporaryPdfBuffer;
import static org.sejda.core.support.io.model.FileOutput.file;
import static org.sejda.impl.itext.util.ITextUtils.nullSafeClosePdfReader;

import java.io.File;
import java.util.LinkedHashSet;
import java.util.Set;

import org.sejda.core.support.io.OutputWriters;
import org.sejda.core.support.io.SingleOutputWriter;
import org.sejda.impl.itext.component.DefaultPdfCopier;
import org.sejda.impl.itext.component.input.PdfSourceOpeners;
import org.sejda.model.RectangularBox;
import org.sejda.model.exception.TaskException;
import org.sejda.model.input.PdfSource;
import org.sejda.model.input.PdfSourceOpener;
import org.sejda.model.parameter.CropParameters;
import org.sejda.model.task.BaseTask;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lowagie.text.pdf.PdfDictionary;
import com.lowagie.text.pdf.PdfName;
import com.lowagie.text.pdf.PdfReader;
import com.lowagie.text.pdf.PdfRectangle;

/**
 * iText implementation of the Crop task to set MEDIABOX and CROPBOX on an input document. This task allow multiple boxes on the same page, generating an output document that
 * contains duplicated pages with different boxes applied.
 * 
 * @author Andrea Vacondio
 * 
 */
public class CropTask extends BaseTask<CropParameters> {

    private static final Logger LOG = LoggerFactory.getLogger(CropTask.class);

    private PdfReader reader = null;
    private DefaultPdfCopier copier = null;
    private SingleOutputWriter outputWriter;
    private PdfSourceOpener<PdfReader> sourceOpener;

    @Override
    public void before(CropParameters parameters) {
        sourceOpener = PdfSourceOpeners.newFullReadOpener();
        outputWriter = OutputWriters.newSingleOutputWriter(parameters.getExistingOutputPolicy());
    }

    @Override
    public void execute(CropParameters parameters) throws TaskException {
        PdfSource<?> source = parameters.getSource();
        LOG.debug("Opening {} ", source);
        reader = source.open(sourceOpener);
        int totalPages = reader.getNumberOfPages();

        File tmpFile = createTemporaryPdfBuffer();
        LOG.debug("Created output temporary buffer {} ", tmpFile);

        copier = new DefaultPdfCopier(reader, tmpFile, parameters.getVersion());
        copier.setCompression(parameters.isCompress());

        Set<PdfRectangle> cropAreas = getPdfRectangles(parameters.getCropAreas());
        for (int page = 1; page <= totalPages; page++) {
            PdfDictionary dictionary = reader.getPageN(page);
            for (PdfRectangle cropBox : cropAreas) {
                stopTaskIfCancelled();

                LOG.trace("Applying crop box {} to page {}", cropBox, page);
                dictionary.put(PdfName.MEDIABOX, cropBox);
                dictionary.put(PdfName.CROPBOX, cropBox);
                copier.addPage(reader, page);
            }
            notifyEvent(getNotifiableTaskMetadata()).stepsCompleted(page).outOf(totalPages);
        }
        nullSafeCloseQuietly(copier);
        nullSafeClosePdfReader(reader);

        outputWriter.setOutput(file(tmpFile).name(parameters.getOutputName()));
        parameters.getOutput().accept(outputWriter);
        LOG.debug("Crop areas applied to {}", parameters.getOutput());
    }

    @Override
    public void after() {
        nullSafeCloseQuietly(copier);
        nullSafeClosePdfReader(reader);
    }

    private Set<PdfRectangle> getPdfRectangles(Set<RectangularBox> areas) {
        Set<PdfRectangle> retVal = new LinkedHashSet<PdfRectangle>();
        for (RectangularBox box : areas) {
            retVal.add(new PdfRectangle(box.getLeft(), box.getBottom(), box.getRight(), box.getTop()));
        }
        return retVal;
    }
}
