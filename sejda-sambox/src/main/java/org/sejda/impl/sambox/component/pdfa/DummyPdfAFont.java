/*
 * Created on 16/10/24
 * Copyright 2024 Sober Lemur S.r.l. and Sejda BV
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
package org.sejda.impl.sambox.component.pdfa;

import org.sejda.sambox.cos.COSString;
import org.sejda.sambox.pdmodel.font.PDFont;

import java.io.IOException;

/**
 * @author Andrea Vacondio
 */
public class DummyPdfAFont extends PdfAFont {
    DummyPdfAFont(PDFont font, String name) {
        super(font, name);
    }

    @Override
    void addString(COSString string) {
        //noop
    }

    @Override
    void regenerateFontWidths() throws IOException {

    }
}
