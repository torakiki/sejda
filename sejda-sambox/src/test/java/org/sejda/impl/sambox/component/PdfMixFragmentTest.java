/*
 * Created on 26 ago 2016
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
package org.sejda.impl.sambox.component;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import java.awt.Rectangle;
import java.io.IOException;

import org.junit.Test;
import org.sejda.model.exception.TaskIOException;
import org.sejda.model.exception.TaskPermissionsException;
import org.sejda.model.input.PdfMixInput;
import org.sejda.model.input.PdfStreamSource;

/**
 * @author Andrea Vacondio
 *
 */
public class PdfMixFragmentTest {

    @Test
    public void nextPage() throws TaskIOException, TaskPermissionsException, IOException {
        try (PdfMixFragment victim = PdfMixFragment.newInstance(new PdfMixInput(PdfStreamSource.newInstanceNoPassword(
                getClass().getClassLoader().getResourceAsStream("pdf/2_pages.pdf"), "test.pdf")))) {
            assertTrue(victim.hasNextPage());
            assertThat(new PdfTextExtractorByArea()
                    .extractTextFromArea(victim.nextPage(), new Rectangle(54, 56, 60, 21)).trim(), is("First page"));
            assertTrue(victim.hasNextPage());
            assertThat(new PdfTextExtractorByArea()
                    .extractTextFromArea(victim.nextPage(), new Rectangle(54, 56, 60, 21)).trim(), is("Second page"));
            assertFalse(victim.hasNextPage());
        }
    }

    @Test
    public void numberOfPages() throws TaskIOException, TaskPermissionsException, IOException {
        try (PdfMixFragment victim = PdfMixFragment.newInstance(new PdfMixInput(PdfStreamSource.newInstanceNoPassword(
                getClass().getClassLoader().getResourceAsStream("pdf/2_pages.pdf"), "test.pdf")))) {
            assertEquals(2, victim.getNumberOfPages());
        }
    }

    @Test
    public void step() throws TaskIOException, TaskPermissionsException, IOException {
        try (PdfMixFragment victim = PdfMixFragment
                .newInstance(new PdfMixInput(
                        PdfStreamSource.newInstanceNoPassword(
                                getClass().getClassLoader().getResourceAsStream("pdf/2_pages.pdf"), "test.pdf"),
                        true, 2))) {
            assertEquals(2, victim.getStep());
        }
    }

    @Test
    public void reverse() throws TaskIOException, TaskPermissionsException, IOException {
        try (PdfMixFragment victim = PdfMixFragment
                .newInstance(new PdfMixInput(
                        PdfStreamSource.newInstanceNoPassword(
                                getClass().getClassLoader().getResourceAsStream("pdf/2_pages.pdf"), "test.pdf"),
                        true, 1))) {
            assertTrue(victim.hasNextPage());
            assertThat(new PdfTextExtractorByArea()
                    .extractTextFromArea(victim.nextPage(), new Rectangle(54, 56, 60, 21)).trim(), is("Second page"));
            assertTrue(victim.hasNextPage());
            assertThat(new PdfTextExtractorByArea()
                    .extractTextFromArea(victim.nextPage(), new Rectangle(54, 56, 60, 21)).trim(), is("First page"));
            assertFalse(victim.hasNextPage());
        }
    }
}
