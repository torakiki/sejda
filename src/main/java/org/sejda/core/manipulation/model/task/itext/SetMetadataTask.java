/*
 * Created on 09/lug/2010
 * Copyright (C) 2010 by Andrea Vacondio (andrea.vacondio@gmail.com).
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License.
 * 
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 */
package org.sejda.core.manipulation.model.task.itext;

import static org.sejda.core.support.io.model.FileOutput.file;

import java.io.File;
import java.util.HashMap;
import java.util.Map.Entry;

import org.sejda.core.exception.TaskException;
import org.sejda.core.manipulation.model.input.PdfSource;
import org.sejda.core.manipulation.model.parameter.SetMetadataParameters;
import org.sejda.core.manipulation.model.pdf.PdfMetadataKey;
import org.sejda.core.manipulation.model.task.Task;
import org.sejda.core.manipulation.model.task.itext.component.PdfReaderHandler;
import org.sejda.core.manipulation.model.task.itext.component.PdfStamperHandler;
import org.sejda.core.support.io.SingleOutputWriterSupport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.itextpdf.text.pdf.PdfReader;

/**
 * Task setting metadata on an input {@link PdfSource}
 * 
 * @author Andrea Vacondio
 * 
 */
public class SetMetadataTask extends SingleOutputWriterSupport implements Task<SetMetadataParameters> {

    private static final Logger LOG = LoggerFactory.getLogger(SetMetadataTask.class);

    private PdfReader reader = null;
    private PdfStamperHandler stamperHandler = null;
    private PdfReaderHandler readerHandler = null;

    public void before(SetMetadataParameters parameters) throws TaskException {
        readerHandler = new PdfReaderHandler();

    }

    public void execute(SetMetadataParameters parameters) throws TaskException {
        PdfSource source = parameters.getInputSource();
        LOG.debug(String.format("Opening %s ...", source));
        reader = readerHandler.openReader(source);

        File tmpFile = createTemporaryPdfBuffer();
        LOG.debug(String.format("Creating output on temporary buffer %s ...", tmpFile));
        stamperHandler = new PdfStamperHandler(reader, tmpFile, parameters.getVersion());

        stamperHandler.setCompressionOnStamper(parameters.isCompress());

        LOG.debug("Setting metadata on temporary document.");
        HashMap<String, String> actualMeta = reader.getInfo();
        for (Entry<PdfMetadataKey, String> meta : parameters.entrySet()) {
            actualMeta.put(meta.getKey().getKey(), meta.getValue());
        }
        stamperHandler.setMetadataOnStamper(actualMeta);

        ITextUtils.closePdfReader(reader);
        ITextUtils.closePdfStamperHandlerQuietly(stamperHandler);

        flushSingleOutput(file(tmpFile).name(source.getName()), parameters.getOutput(), parameters.isOverwrite());

        LOG.debug(String.format("Metadata set on %s", parameters.getOutput()));

    }

    public void after() {
        ITextUtils.closePdfReader(reader);
        ITextUtils.closePdfStamperHandlerQuietly(stamperHandler);
    }

}
