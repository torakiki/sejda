/*
 * Created on 06 dic 2016
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

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import java.io.Writer;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Locale;
import java.util.Set;

import org.junit.Test;

/**
 * @author Andrea Vacondio
 *
 */
public class OcrTextExtractorTest {

    @Test(expected = IllegalArgumentException.class)
    public void nullWriter() {
        new OcrTextExtractor(null, mock(OCR.class));
    }

    @Test(expected = IllegalArgumentException.class)
    public void nullOCR() {
        new OcrTextExtractor(mock(Writer.class), null);
    }

    @Test
    public void testSetEmptyLanguage() {
        OCR ocr = mock(OCR.class);
        try (OcrTextExtractor victim = new OcrTextExtractor(mock(Writer.class), ocr)) {
            victim.setLanguage(Collections.emptySet());
            verify(ocr).setLanguage("eng");
        }
    }

    @Test
    public void testSetLanguage() {
        OCR ocr = mock(OCR.class);
        Set<Locale> locales = new LinkedHashSet<>();
        locales.add(Locale.ENGLISH);
        locales.add(Locale.ITALIAN);
        try (OcrTextExtractor victim = new OcrTextExtractor(mock(Writer.class), ocr)) {
            victim.setLanguage(locales);
            verify(ocr).setLanguage("eng+ita");
        }
    }
}
