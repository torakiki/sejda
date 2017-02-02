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

import java.awt.image.RenderedImage;
import java.io.Closeable;
import java.io.File;

import org.sejda.model.exception.TaskIOException;
import org.sejda.model.parameter.image.PdfToImageParameters;

/**
 * Interface for a writer capable of writing {@link RenderedImage}. A writer implementation is a statefull component which may or may not support write of multiple images on a
 * single image file. The writer has a lifecycle where a destination is first open, images written and the destination is closed. A writer is {@link Closeable} and once closed it
 * should not be reused, it's behavior is unpredictable once it's closed.
 * <p>
 * The writer is meant to be used in the context of a Sejda task and its lifecycle methods have a task parameter instance as parameter to allow the writer to perform adjustments
 * depending on the parameters specified.
 * 
 * @param <T>
 * @author Andrea Vacondio
 * 
 */
public interface ImageWriter<T extends PdfToImageParameters> extends Closeable {

    /**
     * Open the provided destination where image/s will be written to. This method must be called before {@link #write(RenderedImage, PdfToImageParameters)} in order to be able to
     * write images.
     * 
     * @param destination
     *            file where the image/s will be written.
     * @param params
     *            task parameter instance.
     * @throws TaskIOException
     */
    void openDestination(File destination, T params) throws TaskIOException;

    /**
     * Close the previously opened destination.
     * 
     * @throws TaskIOException
     */
    void closeDestination() throws TaskIOException;

    /**
     * Writes the given image to the previously opened destination.
     * 
     * @param image
     *            image to write.
     * @param params
     *            task parameter instance.
     * @throws TaskIOException
     */
    void write(RenderedImage image, T params) throws TaskIOException;

    /**
     * 
     * @return true if the writer can write multiple images into the same image file. If a writer supports multiple images the write method ca be called multiple times once the
     *         write destination is opened.
     */
    boolean supportMultiImage();
}
