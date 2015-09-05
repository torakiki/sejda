/*
 * Created on 26/set/2011
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
package org.sejda.impl.icepdf;

import static org.sejda.common.ComponentsUtility.nullSafeCloseQuietly;

import org.sejda.core.writer.context.ImageWriterContext;
import org.sejda.core.writer.model.ImageWriter;
import org.sejda.model.exception.TaskExecutionException;
import org.sejda.model.parameter.image.AbstractPdfToImageParameters;
import org.sejda.model.task.BaseTask;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Base ICEpdf implementation providing common behavior methods for tasks converting pdf documents to image format.
 * 
 * @param <T>
 *            the type of parameters.
 * @author Andrea Vacondio
 * 
 */
abstract class BasePdfToImageTask<T extends AbstractPdfToImageParameters> extends BaseTask<T> {

    private static final Logger LOG = LoggerFactory.getLogger(BasePdfToImageTask.class);

    private ImageWriter<T> writer;

    @Override
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

    @Override
    public void after() {
        nullSafeCloseQuietly(writer);
    }

    ImageWriter<T> getWriter() {
        return writer;
    }
}
