/*
 * Copyright 2018 by Eduard Weissmann (edi.weissmann@gmail.com).
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

package org.sejda.impl.sambox.util;

import org.junit.jupiter.api.Test;
import org.sejda.sambox.pdmodel.PDDocument;
import org.sejda.sambox.pdmodel.common.PDPageLabels;
import org.sejda.tests.DocBuilder;

import java.io.IOException;
import java.util.List;
import java.util.function.Consumer;

import static java.util.Arrays.asList;
import static org.sejda.tests.TestUtils.assertPageLabelIndexesAre;
import static org.sejda.tests.TestUtils.assertPageLabelRangeIs;

public class PageLabelUtilsTest {

    @Test
    public void removeFirstPage() throws IOException {
        PDDocument doc = new DocBuilder().withPages(10)
                .withPageLabelRange(0, "r")
                .withPageLabelRange(2, "D")
                .get();

        removePages(doc, List.of(1), result -> {
            assertPageLabelIndexesAre(result, 0, 1);
            assertPageLabelRangeIs(result, 0, "r");
            assertPageLabelRangeIs(result, 1, "D");
        });
    }

    @Test
    public void removeFirstPages() throws IOException {
        PDDocument doc = new DocBuilder().withPages(10)
                .withPageLabelRange(0, "r")
                .withPageLabelRange(2, "D")
                .get();

        removePages(doc, asList(1, 2), result -> {
            assertPageLabelIndexesAre(result, 0);
            assertPageLabelRangeIs(result, 0, "D");
        });
    }

    @Test
    public void removeMiddlePage() throws IOException {
        PDDocument doc = new DocBuilder().withPages(10)
                .withPageLabelRange(0, "r")
                .withPageLabelRange(2, "D")
                .get();

        removePages(doc, List.of(2), result -> {
            assertPageLabelIndexesAre(result, 0, 1);
            assertPageLabelRangeIs(result, 0, "r");
            assertPageLabelRangeIs(result, 1, "D");
        });
    }

    @Test
    public void removeMiddlePages() throws IOException {
        PDDocument doc = new DocBuilder().withPages(10)
                .withPageLabelRange(0, "r")
                .withPageLabelRange(2, "D")
                .withPageLabelRange(4, "A")
                .get();

        removePages(doc, asList(3, 4, 5), result -> {
            assertPageLabelIndexesAre(result, 0, 2);
            assertPageLabelRangeIs(result, 0, "r");
            assertPageLabelRangeIs(result, 2, "A");
        });
    }

    @Test
    public void removeLastPage() throws IOException {
        PDDocument doc = new DocBuilder().withPages(10)
                .withPageLabelRange(0, "r")
                .withPageLabelRange(2, "D")
                .get();

        removePages(doc, List.of(10), result -> {
            assertPageLabelIndexesAre(result, 0, 2);
            assertPageLabelRangeIs(result, 0, "r");
            assertPageLabelRangeIs(result, 2, "D");
        });
    }

    @Test
    public void removeLastPages() throws IOException {
        PDDocument doc = new DocBuilder().withPages(5)
                .withPageLabelRange(0, "r")
                .withPageLabelRange(2, "D")
                .get();

        removePages(doc, asList(3, 4, 5), result -> {
            assertPageLabelIndexesAre(result, 0);
            assertPageLabelRangeIs(result, 0, "r");
        });
    }

    private void removePages(PDDocument doc, List<Integer> pages, Consumer<PDPageLabels> consumer) throws IOException {
        PDPageLabels result = PageLabelUtils.removePages(doc.getDocumentCatalog().getPageLabels(), pages, doc.getNumberOfPages());
        consumer.accept(result);
    }
}