/*
 * Created on 20/set/2011
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
package org.sejda.core.writer.context;

import static java.util.Objects.isNull;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

import org.sejda.core.writer.imageio.JpegImageWriter;
import org.sejda.core.writer.imageio.PngImageWriter;
import org.sejda.core.writer.imageio.TiffMultiImageWriter;
import org.sejda.core.writer.imageio.TiffSingleImageWriter;
import org.sejda.core.writer.model.ImageWriter;
import org.sejda.model.exception.TaskException;
import org.sejda.model.exception.TaskExecutionException;
import org.sejda.model.parameter.image.PdfToImageParameters;
import org.sejda.model.parameter.image.PdfToJpegParameters;
import org.sejda.model.parameter.image.PdfToMultipleTiffParameters;
import org.sejda.model.parameter.image.PdfToPngParameters;
import org.sejda.model.parameter.image.PdfToSingleTiffParameters;

/**
 * Image Writer Context used to get the proper {@link ImageWriter}.
 * 
 * @author Andrea Vacondio
 * 
 */
public final class ImageWriterContext {

    private static final Map<Class<? extends PdfToImageParameters>, Class<? extends ImageWriter<?>>> BUILDERS_REGISTRY = new HashMap<>();

    static {
        BUILDERS_REGISTRY.put(PdfToMultipleTiffParameters.class, TiffSingleImageWriter.class);
        BUILDERS_REGISTRY.put(PdfToSingleTiffParameters.class, TiffMultiImageWriter.class);
        BUILDERS_REGISTRY.put(PdfToJpegParameters.class, JpegImageWriter.class);
        BUILDERS_REGISTRY.put(PdfToPngParameters.class, PngImageWriter.class);
    }

    public static ImageWriterContext getContext() {
        return ImageContextHolder.IMAGE_WRITER_CONTEXT;
    }

    @SuppressWarnings("unchecked")
    public <T extends PdfToImageParameters> ImageWriter<T> createImageWriter(T params) throws TaskException {
        Class<? extends ImageWriter<?>> writer = BUILDERS_REGISTRY.get(params.getClass());
        if (isNull(writer)) {
            throw new TaskExecutionException(String.format("No suitable ImageWriter found for %s", params));
        }
        try {
            return ImageWriter.class.cast(writer.getConstructor().newInstance());
        } catch (NoSuchMethodException | SecurityException | InstantiationException | IllegalAccessException
                | IllegalArgumentException | InvocationTargetException e) {
            throw new TaskException("Unable to create ImageWriter", e);
        }
    }

    /**
     * Lazy initialization holder class
     * 
     * @author Andrea Vacondio
     * 
     */
    private static final class ImageContextHolder {

        private ImageContextHolder() {
            // hide constructor
        }

        static final ImageWriterContext IMAGE_WRITER_CONTEXT = new ImageWriterContext();
    }
}
