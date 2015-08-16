/*
 * Created on 01/mar/2013
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
package org.sejda.core.writer.xmlgraphics;

import static org.apache.commons.lang3.StringUtils.isNotBlank;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.apache.xmlgraphics.image.writer.ImageWriterParams;
import org.sejda.model.image.TiffCompressionType;
import org.sejda.model.parameter.image.AbstractPdfToImageParameters;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Base implementation for an ImageWriter writing TIFF images
 * 
 * @param <T>
 *            task parameter this writer can write
 * @author Andrea Vacondio
 * 
 */
abstract class BaseTiffImageWriterAdapter<T extends AbstractPdfToImageParameters> extends AbstractImageWriterAdapter<T> {

    private static final Logger LOG = LoggerFactory.getLogger(BaseTiffImageWriterAdapter.class);

    private static final Map<TiffCompressionType, String> TIFF_COMPRESSION_TYPE_CACHE;
    static {
        Map<TiffCompressionType, String> compressionTypesCache = new HashMap<TiffCompressionType, String>();
        compressionTypesCache.put(TiffCompressionType.PACKBITS, "PackBits");
        compressionTypesCache.put(TiffCompressionType.NONE, "NONE");
        compressionTypesCache.put(TiffCompressionType.JPEG_TTN2, "JPEG");
        compressionTypesCache.put(TiffCompressionType.DEFLATE, "Deflate");
        TIFF_COMPRESSION_TYPE_CACHE = Collections.unmodifiableMap(compressionTypesCache);
    }

    /**
     * 
     * @param params
     * @param compressionType
     * @return a new {@link ImageWriterParams} for the given input.
     */
    ImageWriterParams newImageWriterParams(T params, TiffCompressionType compressionType) {
        ImageWriterParams imageWriterParams = new ImageWriterParams();
        imageWriterParams.setResolution(params.getResolutionInDpi());
        String compression = TIFF_COMPRESSION_TYPE_CACHE.get(compressionType);
        if (isNotBlank(compression)) {
            imageWriterParams.setCompressionMethod(compression);
        } else {
            LOG.warn("{} compression type is currently not supported by XML Graphics.", compressionType);
        }
        return imageWriterParams;
    }
}
