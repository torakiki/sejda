/*
 * Copyright 2015 by Edi Weissmann (edi.weissmann@gmail.com)
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
package org.sejda.impl.sambox.component.split;

import static org.junit.Assert.assertEquals;

import java.io.InputStream;
import java.util.Collection;

import org.junit.Test;
import org.mockito.internal.util.collections.Sets;
import org.sejda.impl.sambox.component.DefaultPdfSourceOpener;
import org.sejda.impl.sambox.component.split.SplitByTextChangesOutputStrategy;
import org.sejda.model.TopLeftRectangularBox;
import org.sejda.model.exception.TaskIOException;
import org.sejda.model.input.PdfStreamSource;
import org.sejda.sambox.pdmodel.PDDocument;

public class SplitByTextChangesOutputStrategyTest {

    @Test
    public void testDeterminingPagesToSplitAt() throws TaskIOException {
        InputStream stream = getClass().getClassLoader().getResourceAsStream("pdf/split_by_text_contents_sample.pdf");

        PdfStreamSource source = PdfStreamSource.newInstanceNoPassword(stream, "test_file.pdf");
        PDDocument document = new DefaultPdfSourceOpener().open(source).getUnderlyingPDDocument();

        TopLeftRectangularBox area = new TopLeftRectangularBox(114, 70, 41, 15);

        Collection<Integer> pagesToSplitAt = new SplitByTextChangesOutputStrategy(document, area).getPages();
        assertEquals(Sets.newSet(3, 4), pagesToSplitAt);
    }
}
