/*
 * Created on 27 gen 2017
 * Copyright 2015 by Andrea Vacondio (andrea.vacondio@gmail.com).
 * This file is part of Sejda.
 *
 * Sejda is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Sejda is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with Sejda.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.sejda.impl.sambox;

import static org.sejda.common.ComponentsUtility.nullSafeCloseQuietly;

import org.sejda.core.writer.context.ImageWriterContext;
import org.sejda.core.writer.model.ImageWriter;
import org.sejda.model.exception.TaskException;
import org.sejda.model.parameter.image.PdfToImageParameters;
import org.sejda.model.task.BaseTask;
import org.sejda.model.task.TaskExecutionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Base implementation providing common behavior methods for tasks converting pdf documents to image format.
 * 
 * @param <T>
 *            the type of parameters.
 * @author Andrea Vacondio
 * 
 */
abstract class BasePdfToImageTask<T extends PdfToImageParameters> extends BaseTask<T> {

    private static final Logger LOG = LoggerFactory.getLogger(BasePdfToImageTask.class);

    private ImageWriter<T> writer;

    @Override
    public void before(T parameters, TaskExecutionContext executionContext) throws TaskException {
        super.before(parameters, executionContext);
        writer = ImageWriterContext.getContext().createImageWriter(parameters);
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
