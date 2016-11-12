/*
 * Created on 19/set/2011
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
package org.sejda.core.writer.model;

import org.sejda.model.parameter.image.PdfToImageParameters;

/**
 * Abstract factory to create {@link ImageWriter}s. Implementations of this interface must be thread safe since the factory instance is going to be shared among the tasks using it.
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
    <T extends PdfToImageParameters> ImageWriter<T> createImageWriter(T params);
}
