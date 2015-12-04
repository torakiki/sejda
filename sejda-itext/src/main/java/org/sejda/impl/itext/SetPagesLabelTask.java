/*
 * Created on 23/gen/2011
 * Copyright 2010 by Andrea Vacondio (andrea.vacondio@gmail.com).
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
import static org.sejda.impl.itext.util.PageLabelUtils.getLabels;

import java.io.File;

import org.sejda.core.support.io.OutputWriters;
import org.sejda.core.support.io.SingleOutputWriter;
import org.sejda.impl.itext.component.DefaultPdfCopier;
import org.sejda.impl.itext.component.input.PdfSourceOpeners;
import org.sejda.model.exception.TaskException;
import org.sejda.model.input.PdfSource;
import org.sejda.model.input.PdfSourceOpener;
import org.sejda.model.parameter.SetPagesLabelParameters;
import org.sejda.model.task.BaseTask;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lowagie.text.pdf.PdfReader;

/**
 * Task that apply page labels to a input pdf.
 * 
 * @author Andrea Vacondio
 * 
 */
public class SetPagesLabelTask extends BaseTask<SetPagesLabelParameters> {

    private static final Logger LOG = LoggerFactory.getLogger(SetPagesLabelTask.class);

    private PdfReader reader = null;
    private DefaultPdfCopier copier = null;
    private SingleOutputWriter outputWriter;
    private PdfSourceOpener<PdfReader> sourceOpener;

    @Override
    public void before(SetPagesLabelParameters parameters) {
        sourceOpener = PdfSourceOpeners.newPartialReadOpener();
        outputWriter = OutputWriters.newSingleOutputWriter(parameters.isOverwrite());
    }

    @Override
    public void execute(SetPagesLabelParameters parameters) throws TaskException {
        stopTaskIfCancelled();
        notifyEvent(getNotifiableTaskMetadata()).progressUndetermined();

        PdfSource<?> source = parameters.getSource();
        LOG.debug("Opening {} ", source);
        reader = source.open(sourceOpener);

        File tmpFile = createTemporaryPdfBuffer();
        LOG.debug("Created output temporary buffer {} ", tmpFile);

        copier = new DefaultPdfCopier(reader, tmpFile, parameters.getVersion());
        copier.setCompression(parameters.isCompress());

        copier.addAllPages(reader);

        nullSafeClosePdfReader(reader);

        LOG.debug("Applying {} labels ", parameters.getLabels().size());
        copier.setPageLabels(getLabels(parameters.getLabels(), reader.getNumberOfPages()));

        nullSafeCloseQuietly(copier);

        outputWriter.setOutput(file(tmpFile).name(parameters.getOutputName()));
        parameters.getOutput().accept(outputWriter);
        LOG.debug("Labels applied to {}", parameters.getOutput());
    }

    @Override
    public void after() {
        nullSafeClosePdfReader(reader);
        nullSafeCloseQuietly(copier);
    }

}
