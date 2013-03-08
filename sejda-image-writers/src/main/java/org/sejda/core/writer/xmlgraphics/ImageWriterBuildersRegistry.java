/*
 * Created on 20/set/2011
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
