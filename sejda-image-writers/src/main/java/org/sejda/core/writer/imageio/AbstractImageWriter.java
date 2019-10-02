/*
 * Created on 01 feb 2017
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
package org.sejda.core.writer.imageio;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

import java.awt.image.RenderedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.metadata.IIOMetadata;
import javax.imageio.stream.ImageOutputStream;

import org.sejda.commons.util.IOUtils;
import org.sejda.core.writer.model.ImageWriter;
import org.sejda.model.exception.TaskIOException;
import org.sejda.model.image.TiffCompressionType;
import org.sejda.model.parameter.image.PdfToImageParameters;

/**
 * Abstract implementation of an adapter for an ImageIO image writer
 * 
 * @param <T>
 *            task parameter
 * @author Andrea Vacondio
 */
abstract class AbstractImageWriter<T extends PdfToImageParameters> implements ImageWriter<T> {

    static final Map<TiffCompressionType, String> TIFF_COMPRESSION_TYPE_CACHE;
    static {
        Map<TiffCompressionType, String> compressionTypesCache = new HashMap<TiffCompressionType, String>();
        compressionTypesCache.put(TiffCompressionType.PACKBITS, "PackBits");
        compressionTypesCache.put(TiffCompressionType.NONE, "None");
        compressionTypesCache.put(TiffCompressionType.JPEG_TTN2, "JPEG");
        compressionTypesCache.put(TiffCompressionType.DEFLATE, "Deflate");
        compressionTypesCache.put(TiffCompressionType.LZW, "LZW");
        compressionTypesCache.put(TiffCompressionType.ZLIB, "ZLib");
        compressionTypesCache.put(TiffCompressionType.CCITT_GROUP_3_1D, "CCITT RLE");
        compressionTypesCache.put(TiffCompressionType.CCITT_GROUP_3_2D, "CCITT T.4");
        compressionTypesCache.put(TiffCompressionType.CCITT_GROUP_4, "CCITT T.6");
        TIFF_COMPRESSION_TYPE_CACHE = Collections.unmodifiableMap(compressionTypesCache);
    }

    private ImageOutputStream out;
    private OutputStream wrappedOut;
    protected final javax.imageio.ImageWriter writer;

    AbstractImageWriter(String format) {
        Iterator<javax.imageio.ImageWriter> writers = ImageIO.getImageWritersByFormatName(format);
        if (isNull(writers) || !writers.hasNext()) {
            throw new IllegalArgumentException(
                    String.format("Unable to find an ImageWriter for the format %s", format));
        }
        writer = writers.next();
    }

    /**
     * @param params
     * @return parameters for the {@link ImageWriter}. Each concrete class has to implement this to provide specific parameters for the adapted writer.
     */
    abstract ImageWriteParam newImageWriterParams(T params);

    /**
     * @param image
     * @param params
     * @param writerParams
     * @return metadata for the {@link ImageWriter}
     */
    public IIOMetadata newImageMetadata(RenderedImage image, T params, ImageWriteParam writerParams) {
        return null;
    }

    @Override
    public void openDestination(File file, T params) throws TaskIOException {
        try {
            wrappedOut = params.getOutput().getEncryptionAtRestPolicy().encrypt(new FileOutputStream(file));

            out = ImageIO.createImageOutputStream(wrappedOut);
            TaskIOException.require(nonNull(out), "Unable to create image output stream");
            writer.setOutput(getOutput());
        } catch (IOException e) {
            throw new TaskIOException("Unable to create output stream.", e);
        }
    }

    ImageOutputStream getOutput() {
        return out;
    }

    @Override
    public void close() throws IOException {
        if (nonNull(writer)) {
            writer.dispose();
        }
    }

    @Override
    public void closeDestination() throws TaskIOException {
        try {
            IOUtils.close(getOutput());
            IOUtils.close(wrappedOut);
        } catch (IOException e) {
            throw new TaskIOException("Unable to close destination", e);
        }
    }

}
