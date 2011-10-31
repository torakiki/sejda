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

import java.awt.image.RenderedImage;
import java.io.Closeable;
import java.io.File;
import java.io.OutputStream;

import org.sejda.model.exception.TaskIOException;
import org.sejda.model.parameter.image.AbstractPdfToImageParameters;

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
public interface ImageWriter<T extends AbstractPdfToImageParameters> extends Closeable {

    /**
     * Open the provided destination where image/s will be written to. This method must be called before {@link #write(RenderedImage, AbstractPdfToImageParameters)} in order to be
     * able to write images.
     * 
     * @param destination
     *            stream where the image/s will be written.
     * @param params
     *            task parameter instance.
     * @throws TaskIOException
     */
    void openWriteDestination(OutputStream destination, T params) throws TaskIOException;

    /**
     * Open the provided destination where image/s will be written to. This method must be called before {@link #write(RenderedImage, AbstractPdfToImageParameters)} in order to be
     * able to write images.
     * 
     * @param destination
     *            file where the image/s will be written.
     * @param params
     *            task parameter instance.
     * @throws TaskIOException
     */
    void openWriteDestination(File destination, T params) throws TaskIOException;

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
     * Close the previously opened destination.
     * 
     * @throws TaskIOException
     */
    void closeDestination() throws TaskIOException;

    /**
     * 
     * @return true if the writer can write multiple images into the same image file. If a writer supports multiple images the write method ca be called multiple times once the
     *         write destination is opened.
     */
    boolean supportMultiImage();

    /**
     * Builder interface for an {@link ImageWriter}.
     * 
     * @author Andrea Vacondio
     * 
     * @param <T>
     *            type of the built {@link ImageWriter}
     */
    interface ImageWriterBuilder<T extends AbstractPdfToImageParameters> {

        /**
         * @return the newly built instance.
         */
        ImageWriter<T> build();
    }
}
