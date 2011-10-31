/*
 * Created on 26/set/2011
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
package org.sejda.impl.icepdf;

import static org.sejda.common.ComponentsUtility.nullSafeCloseQuietly;

import org.sejda.core.writer.model.ImageWriter;
import org.sejda.core.writer.model.ImageWriterContext;
import org.sejda.model.exception.TaskExecutionException;
import org.sejda.model.parameter.image.AbstractPdfToImageParameters;
import org.sejda.model.task.BaseTask;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Base ICEpdf implementation providing common behavior methods for tasks coverting pdf documents to image format.
 * 
 * @param <T>
 *            the type of parameters.
 * @author Andrea Vacondio
 * 
 */
abstract class BasePdfToImageTask<T extends AbstractPdfToImageParameters> extends BaseTask<T> {

    private static final Logger LOG = LoggerFactory.getLogger(PdfToSingleImageTask.class);

    private ImageWriter<T> writer;

    public void before(T parameters) throws TaskExecutionException {
        writer = ImageWriterContext.getContext().getImageWriterFactory().createImageWriter(parameters);
        if (writer == null) {
            LOG.info("Unable to create an ImageWriter using the provided factory, falling back on default factory.");
            writer = ImageWriterContext.getContext().getDefaultImageWriterFactory().createImageWriter(parameters);
        }
        if (writer == null) {
            throw new TaskExecutionException(String.format("No suitable ImageWriter found for %s.", parameters));
        }
        LOG.trace("Found image writer {}", writer);
    }

    public void after() {
        nullSafeCloseQuietly(writer);
    }

    ImageWriter<T> getWriter() {
        return writer;
    }
}
