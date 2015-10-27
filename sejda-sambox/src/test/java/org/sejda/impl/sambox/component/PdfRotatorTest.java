/*
 * Created on 16/ago/2015
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

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.HashSet;

import org.junit.Test;
import org.sejda.model.rotation.Rotation;
import org.sejda.sambox.pdmodel.PDDocument;
import org.sejda.sambox.pdmodel.PDPage;

/**
 * @author Andrea Vacondio
 *
 */
public class PdfRotatorTest {
    @Test
    public void singlePage() {
        PDDocument document = mock(PDDocument.class);
        PDPage page = mock(PDPage.class);
        when(page.getRotation()).thenReturn(180);
        when(document.getPage(2)).thenReturn(page);
        new PdfRotator(document).rotate(3, Rotation.DEGREES_270);
        verify(page).setRotation(90);
    }

    @Test
    public void multiplePages() {
        PDDocument document = mock(PDDocument.class);
        PDPage page1 = mock(PDPage.class);
        when(page1.getRotation()).thenReturn(180);
        when(document.getPage(0)).thenReturn(page1);
        PDPage page2 = mock(PDPage.class);
        when(page2.getRotation()).thenReturn(90);
        when(document.getPage(1)).thenReturn(page2);
        when(document.getNumberOfPages()).thenReturn(2);
        PdfRotator victim = new PdfRotator(document);
        new HashSet<>(Arrays.asList(1, 2)).forEach(page -> victim.rotate(page, Rotation.DEGREES_270));
        verify(page1).setRotation(90);
        verify(page2).setRotation(0);
    }
}
