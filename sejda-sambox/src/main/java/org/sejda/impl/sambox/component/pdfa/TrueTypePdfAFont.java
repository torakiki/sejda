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

import org.sejda.sambox.cos.COSArrayList;
import org.sejda.sambox.cos.COSName;
import org.sejda.sambox.cos.COSString;
import org.sejda.sambox.pdmodel.font.PDTrueTypeFont;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

import static java.util.Optional.ofNullable;

/**
 * @author Andrea Vacondio
 */
class TrueTypePdfAFont extends PdfAFont {

    private static final Logger LOG = LoggerFactory.getLogger(TrueTypePdfAFont.class);

    public TrueTypePdfAFont(PDTrueTypeFont font, String name) {
        super(font, name);
    }

    public void addString(COSString string) {
        //if we don't already have found a glyph with invalid width
        if (!wrongWidth()) {
            var buffer = ByteBuffer.wrap(string.getBytes());
            while (buffer.remaining() > 0 && !wrongWidth()) {
                int code = Byte.toUnsignedInt(buffer.get());
                //we need to regenerate widths values if we don't have an explicit width or
                // it differs from the one on the font (
                wrongWidth(ofNullable(font().getExplicitWidth(code)).map(explicit -> {
                    try {
                        return Math.abs(explicit - font().getWidthFromFont(code)) > 1;
                    } catch (IOException e) {
                        LOG.warn("Unable to get width from the font: " + name(), e);
                        return true;
                    }
                }).orElse(true));
            }
        }
    }

    @Override
    void regenerateFontWidths() throws IOException {
        LOG.debug("Regenerating widths array for font '{}'", name());
        List<Integer> widths = new ArrayList<>(256);
        for (int code = 0; code <= 255; code++) {
            widths.add(Math.round(font().getWidthFromFont(code)));
        }

        font().getCOSObject().setInt(COSName.FIRST_CHAR, 0);
        font().getCOSObject().setInt(COSName.LAST_CHAR, 255);
        font().getCOSObject().setItem(COSName.WIDTHS, COSArrayList.converterToCOSArray(widths));
    }
}
