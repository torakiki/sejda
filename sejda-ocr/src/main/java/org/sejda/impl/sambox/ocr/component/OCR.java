/*
 * Created on 05 dic 2016
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
package org.sejda.impl.sambox.ocr.component;

import java.awt.image.BufferedImage;
import java.io.IOException;

import org.sejda.impl.sambox.ocr.util.ImageUtils;

import net.sourceforge.tess4j.Tesseract;

/**
 * @author Andrea Vacondio
 *
 */
public class OCR extends Tesseract {

    public String ocrTextFrom(BufferedImage image) throws IOException {
        init();
        setTessVariables();
        StringBuilder sb = new StringBuilder();
        try {
            setImage(image.getWidth(), image.getHeight(), ImageUtils.convertToTiff(image), null,
                    image.getColorModel().getPixelSize());
            return sb.append(getOCRText("", 0)).toString();
        } finally {
            dispose();
        }

    }
}
