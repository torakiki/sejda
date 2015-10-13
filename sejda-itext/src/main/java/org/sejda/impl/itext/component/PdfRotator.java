/*
 * Created on 06/jun/2010
 *
 * Copyright 2010 by Andrea Vacondio (andrea.vacondio@gmail.com).
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
package org.sejda.impl.itext.component;

import static org.sejda.model.rotation.Rotation.getRotation;

import java.util.Set;

import org.sejda.model.rotation.Rotation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lowagie.text.pdf.PdfDictionary;
import com.lowagie.text.pdf.PdfName;
import com.lowagie.text.pdf.PdfNumber;
import com.lowagie.text.pdf.PdfReader;

/**
 * Handles rotations on a given PdfReader
 * 
 * @author Andrea Vacondio
 * 
 */
public final class PdfRotator {

    private static final Logger LOG = LoggerFactory.getLogger(PdfRotator.class);

    private PdfReader reader;

    public PdfRotator(PdfReader reader) {
        this.reader = reader;
    }

    public void rotate(int pageNumber, Rotation rotation) {
        LOG.debug("Applying rotation of {} degrees to page {}", rotation.getDegrees(), pageNumber);
        PdfDictionary dictionary = reader.getPageN(pageNumber);
        dictionary.put(PdfName.ROTATE,
                new PdfNumber(rotation.addRotation(getRotation(reader.getPageRotation(pageNumber)))
                        .getDegrees()));
    }
}
