/*
 * Created on 04 feb 2016
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
package org.sejda.impl.sambox.component.optimization;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;

import java.io.IOException;

import org.junit.Test;
import org.sejda.io.SeekableSources;
import org.sejda.model.optimization.OptimizationPolicy;
import org.sejda.sambox.input.PDFParser;
import org.sejda.sambox.pdmodel.PDDocument;

/**
 * @author Andrea Vacondio
 *
 */
public class OptimizationRulerTest {

    @Test
    public void noPolicy() {
        assertFalse(new OptimizationRuler(OptimizationPolicy.NO).apply(mock(PDDocument.class)));
    }

    @Test
    public void yesPolicy() {
        assertTrue(new OptimizationRuler(OptimizationPolicy.YES).apply(mock(PDDocument.class)));
    }

    @Test
    public void sharedResourceWithImagesDictionary() throws IOException {
        try (PDDocument document = PDFParser.parse(SeekableSources.inMemorySeekableSourceFrom(
                getClass().getClassLoader().getResourceAsStream("pdf/shared_resource_dic_w_images.pdf")))) {
            assertTrue(new OptimizationRuler(OptimizationPolicy.AUTO).apply(document));
        }
    }

    @Test
    public void sharedResourceWithFontsDictionary() throws IOException {
        try (PDDocument document = PDFParser.parse(SeekableSources.inMemorySeekableSourceFrom(
                getClass().getClassLoader().getResourceAsStream("pdf/shared_resource_dic_w_fonts.pdf")))) {
            assertTrue(new OptimizationRuler(OptimizationPolicy.AUTO).apply(document));
        }
    }

    @Test
    public void sharedXobjectsDictionary() throws IOException {
        try (PDDocument document = PDFParser.parse(SeekableSources.inMemorySeekableSourceFrom(
                getClass().getClassLoader().getResourceAsStream("pdf/shared_xobjects_dics.pdf")))) {
            assertTrue(new OptimizationRuler(OptimizationPolicy.AUTO).apply(document));
        }
    }

    @Test
    public void sharedFontsDictionary() throws IOException {
        try (PDDocument document = PDFParser.parse(SeekableSources.inMemorySeekableSourceFrom(
                getClass().getClassLoader().getResourceAsStream("pdf/shared_fonts_dics.pdf")))) {
            assertTrue(new OptimizationRuler(OptimizationPolicy.AUTO).apply(document));
        }
    }
}
