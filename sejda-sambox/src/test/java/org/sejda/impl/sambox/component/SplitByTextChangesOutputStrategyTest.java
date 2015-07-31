/*
 * Copyright 2015 by Edi Weissmann (edi.weissmann@gmail.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.sejda.impl.sambox.component;

import org.sejda.sambox.pdmodel.PDDocument;
import org.junit.Test;
import org.mockito.internal.util.collections.Sets;
import org.sejda.model.TopLeftRectangularBox;
import org.sejda.model.exception.TaskIOException;
import org.sejda.model.input.PdfStreamSource;

import java.io.InputStream;
import java.util.Collection;

import static org.junit.Assert.assertEquals;

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
