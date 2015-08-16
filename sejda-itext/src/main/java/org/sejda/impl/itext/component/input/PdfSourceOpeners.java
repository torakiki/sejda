/*
 * Created on 23/jul/2011
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
package org.sejda.impl.itext.component.input;

import org.sejda.model.input.PdfSourceOpener;

import com.lowagie.text.pdf.PdfReader;

/**
 * This class contains only static factory methods to create {@link PdfSourceOpener} implementations.
 * 
 * @author Andrea Vacondio
 * 
 */
public final class PdfSourceOpeners {

    private PdfSourceOpeners() {
        // hide
    }

    /**
     * Factory method returning a {@link PdfSourceOpener} that performs a full read on the opened source.
     * 
     * @return the newly created {@link PdfSourceOpener}
     */
    public static PdfSourceOpener<PdfReader> newFullReadOpener() {
        return new FullReadPdfSourceOpener();
    }

    /**
     * Factory method returning a {@link PdfSourceOpener} that performs a partial read on the opened source.
     * 
     * @return the newly created {@link PdfSourceOpener}
     */
    public static PdfSourceOpener<PdfReader> newPartialReadOpener() {
        return new PartialReadPdfSourceOpener();
    }

}
