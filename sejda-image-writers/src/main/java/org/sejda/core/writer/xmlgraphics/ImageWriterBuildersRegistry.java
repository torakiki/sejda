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
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.sejda.core.writer.xmlgraphics;

import java.util.HashMap;
import java.util.Map;

import org.sejda.core.writer.model.ImageWriter.ImageWriterBuilder;
import org.sejda.model.parameter.image.AbstractPdfToImageParameters;

/**
 * Type safe registry for {@link ImageWriterBuilder}s.
 * 
 * @author Andrea Vacondio
 * 
 */
class ImageWriterBuildersRegistry {

    private final Map<Class<? extends AbstractPdfToImageParameters>, ImageWriterBuilder<?>> builders = new HashMap<Class<? extends AbstractPdfToImageParameters>, ImageWriterBuilder<?>>();

    /**
     * Adds the builder to the registry associating it to the given task parameter class.
     * 
     * @param <T>
     * @param params
     * @param builder
     */
    <T extends AbstractPdfToImageParameters> void addBuilder(Class<T> params, ImageWriterBuilder<T> builder) {
        builders.put(params, builder);
    }

    /**
     * @param <T>
     * @param params
     * @return the builder for the given task parameter.
     */
    @SuppressWarnings("unchecked")
    <T extends AbstractPdfToImageParameters> ImageWriterBuilder<T> getBuilder(T params) {
        return (ImageWriterBuilder<T>) builders.get(params.getClass());
    }
}
