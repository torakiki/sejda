/*
 * Created on 19/set/2011
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
package org.sejda.core.writer.model;

import org.sejda.core.manipulation.model.parameter.image.AbstractPdfToImageParameters;

/**
 * Abstract factory to create {@link ImageWriter}s.
 * 
 * @author Andrea Vacondio
 * 
 */
public interface ImageWriterAbstractFactory {

    /**
     * creates a new {@link ImageWriter} instance capable of writing images according to the input task parameters class.
     * 
     * @param <T>
     *            task parameters
     * @param params
     * @return an ImageWriter which can write images for the input task parameters or null if no writer suitable for the given parameters is found.
     */
    <T extends AbstractPdfToImageParameters> ImageWriter<T> createImageWriter(T params);
}
