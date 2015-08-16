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
package org.sejda.core.writer.xmlgraphics;

import org.sejda.core.writer.model.ImageWriter;
import org.sejda.core.writer.model.ImageWriter.ImageWriterBuilder;
import org.sejda.core.writer.model.ImageWriterAbstractFactory;
import org.sejda.core.writer.xmlgraphics.JpegImageWriterAdapter.JpegImageWriterAdapterBuilder;
import org.sejda.core.writer.xmlgraphics.MultipleOutputTiffImageWriterAdapter.MultipleOutputTiffImageWriterAdapterBuilder;
import org.sejda.core.writer.xmlgraphics.SingleOutputTiffImageWriterAdapter.SingleOutputTiffImageWriterAdapterBuilder;
import org.sejda.model.parameter.image.AbstractPdfToImageParameters;
import org.sejda.model.parameter.image.PdfToJpegParameters;
import org.sejda.model.parameter.image.PdfToMultipleTiffParameters;
import org.sejda.model.parameter.image.PdfToSingleTiffParameters;

/**
 * {@link ImageWriterAbstractFactory} implementation returning {@link ImageWriter} XML Graphics implementations.
 * 
 * @author Andrea Vacondio
 * 
 */
public class ImageWriterFactory implements ImageWriterAbstractFactory {

    private static final ImageWriterBuildersRegistry BUILDERS_REGISTRY = new ImageWriterBuildersRegistry();

    static {
        BUILDERS_REGISTRY.addBuilder(PdfToMultipleTiffParameters.class,
                new MultipleOutputTiffImageWriterAdapterBuilder());
        BUILDERS_REGISTRY.addBuilder(PdfToSingleTiffParameters.class, new SingleOutputTiffImageWriterAdapterBuilder());
        BUILDERS_REGISTRY.addBuilder(PdfToJpegParameters.class, new JpegImageWriterAdapterBuilder());

    }

    public <T extends AbstractPdfToImageParameters> ImageWriter<T> createImageWriter(T params) {
        ImageWriterBuilder<T> builder = BUILDERS_REGISTRY.getBuilder(params);
        if (builder != null) {
            return builder.build();
        }
        return null;
    }

}
