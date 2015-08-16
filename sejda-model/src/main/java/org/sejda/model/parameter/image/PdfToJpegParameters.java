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
package org.sejda.model.parameter.image;

import org.sejda.model.image.ImageColorType;
import org.sejda.model.image.ImageType;

/**
 * Parameter meant to convert an existing pdf source to JPEG images.
 * 
 * @author Andrea Vacondio
 * 
 */
public class PdfToJpegParameters extends AbstractPdfToMultipleImageParameters {

    public PdfToJpegParameters() {
        super(ImageColorType.COLOR_RGB);
    }

    @Override
    public ImageType getOutputImageType() {
        return ImageType.JPEG;
    }

}
