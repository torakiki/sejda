/*
 * Created on 01/mar/2013
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
