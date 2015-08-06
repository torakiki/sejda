/*
 * Created on 16/jul/2011
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
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.sejda.impl.itext.component.input;

import java.io.IOException;

import org.sejda.model.input.PdfFileSource;
import org.sejda.model.input.PdfStreamSource;
import org.sejda.model.input.PdfURLSource;

import com.lowagie.text.pdf.PdfReader;
import com.lowagie.text.pdf.RandomAccessFileOrArray;

/**
 * iText component able to open a PdfSource and return the corresponding {@link PdfReader} opened in "partial" mode.
 * 
 * @author Andrea Vacondio
 * 
 */
class PartialReadPdfSourceOpener extends AbstractPdfSourceOpener {

    @Override
    PdfReader openSource(PdfURLSource source) throws IOException {
        return new PdfReader(new RandomAccessFileOrArray(source.getSource()), source.getPasswordBytes());
    }

    @Override
    PdfReader openSource(PdfFileSource source) throws IOException {
        return new PdfReader(new RandomAccessFileOrArray(source.getSource().getAbsolutePath()),
                source.getPasswordBytes());
    }

    @Override
    PdfReader openSource(PdfStreamSource source) throws IOException {
        return new PdfReader(new RandomAccessFileOrArray(source.getSource()), source.getPasswordBytes());
    }

}
