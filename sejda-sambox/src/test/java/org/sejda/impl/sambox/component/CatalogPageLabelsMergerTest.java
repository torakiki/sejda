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

package org.sejda.impl.sambox.component;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.sejda.core.service.TestUtils.assertPageLabelIndexesAre;
import static org.sejda.core.service.TestUtils.assertPageLabelRangeIs;
import static org.sejda.core.service.TestUtils.assertPageLabelRangeIsDefault;

import java.util.Arrays;
import java.util.Collection;
import java.util.Set;
import java.util.TreeSet;

import org.junit.Test;
import org.sejda.model.outline.CatalogPageLabelsPolicy;
import org.sejda.sambox.pdmodel.PDDocument;
import org.sejda.sambox.pdmodel.common.PDPageLabels;

public class CatalogPageLabelsMergerTest {

    @Test
    public void policyDiscard() {
        PDDocument doc1 = new DocBuilder().withPages(2).get();
        PDDocument doc2 = new DocBuilder().withPages(3).get();

        CatalogPageLabelsMerger merger = new CatalogPageLabelsMerger(CatalogPageLabelsPolicy.DISCARD);
        merger.add(doc1, all(doc1));
        merger.add(doc2, all(doc2));

        assertFalse(merger.hasPageLabels());
        assertNull(merger.getMergedPageLabels());
    }

    @Test
    public void mergeDocsWithoutLabels() {
        PDDocument doc1 = new DocBuilder().withPages(2).get();
        PDDocument doc2 = new DocBuilder().withPages(3).get();

        CatalogPageLabelsMerger merger = new CatalogPageLabelsMerger(CatalogPageLabelsPolicy.RETAIN);
        merger.add(doc1, all(doc1));
        merger.add(doc2, all(doc2));

        PDPageLabels mergedLabels = merger.getMergedPageLabels();

        assertPageLabelIndexesAre(mergedLabels, 0, 2);
        assertPageLabelRangeIsDefault(mergedLabels, 0);
        assertPageLabelRangeIsDefault(mergedLabels, 2);
    }

    @Test
    public void mergeDocsWithLabels() {
        PDDocument doc1 = new DocBuilder().withPages(2)
                .withPageLabelRange(0, "r")
                .get();
        PDDocument doc2 = new DocBuilder().withPages(4)
                .withPageLabelRange(0, "D")
                .get();

        CatalogPageLabelsMerger merger = new CatalogPageLabelsMerger(CatalogPageLabelsPolicy.RETAIN);
        merger.add(doc1, all(doc1));
        merger.add(doc2, all(doc2));

        PDPageLabels mergedLabels = merger.getMergedPageLabels();
        assertPageLabelIndexesAre(mergedLabels, 0, 2);
        assertPageLabelRangeIs(mergedLabels, 0,"r");
        assertPageLabelRangeIs(mergedLabels, 2, "D");
    }

    @Test
    public void mergeDocsOneWithLabels() {
        PDDocument doc1 = new DocBuilder().withPages(2).get();
        PDDocument doc2 = new DocBuilder().withPages(4)
                .withPageLabelRange(0, "r")
                .withPageLabelRange(2, "D")
                .get();

        CatalogPageLabelsMerger merger = new CatalogPageLabelsMerger(CatalogPageLabelsPolicy.RETAIN);
        merger.add(doc1, all(doc1));
        merger.add(doc2, all(doc2));

        PDPageLabels mergedLabels = merger.getMergedPageLabels();
        assertPageLabelIndexesAre(mergedLabels, 0, 2, 4);
        assertPageLabelRangeIsDefault(mergedLabels, 0);
        assertPageLabelRangeIs(mergedLabels, 2, "r");
        assertPageLabelRangeIs(mergedLabels, 4, "D");
    }

    @Test
    public void mergeDocsSubsetOfPagesFromDoc1() {
        PDDocument doc1 = new DocBuilder().withPages(5)
                .withPageLabelRange(0, "r")
                .get();
        PDDocument doc2 = new DocBuilder().withPages(4)
                .withPageLabelRange(0, "D")
                .get();

        CatalogPageLabelsMerger merger = new CatalogPageLabelsMerger(CatalogPageLabelsPolicy.RETAIN);
        merger.add(doc1, allBut(doc1, 2));
        merger.add(doc2, all(doc2));

        PDPageLabels mergedLabels = merger.getMergedPageLabels();

        assertPageLabelIndexesAre(mergedLabels, 0, 4);
        assertPageLabelRangeIs(mergedLabels, 0, "r");
        assertPageLabelRangeIs(mergedLabels, 4, "D");
    }

    @Test
    public void mergeDocsSubsetOfPagesFromDoc2() {
        PDDocument doc1 = new DocBuilder().withPages(5)
                .withPageLabelRange(0, "r")
                .get();
        PDDocument doc2 = new DocBuilder().withPages(4)
                .withPageLabelRange(0, "D")
                .get();

        CatalogPageLabelsMerger merger = new CatalogPageLabelsMerger(CatalogPageLabelsPolicy.RETAIN);
        merger.add(doc1, all(doc1));
        merger.add(doc2, allBut(doc2, 1, 2, 3));

        PDPageLabels mergedLabels = merger.getMergedPageLabels();

        assertPageLabelIndexesAre(mergedLabels, 0, 5);
        assertPageLabelRangeIs(mergedLabels, 0, "r");
        assertPageLabelRangeIs(mergedLabels, 5, "D");
    }

    @Test
    public void mergeDocsSubsetOfPagesFromDocWithComplexLabeling() {
        PDDocument doc1 = new DocBuilder().withPages(2)
                .withPageLabelRange(0, "r")
                .get();
        PDDocument doc2 = new DocBuilder().withPages(5)
                .withPageLabelRange(0, "D") // these are getting all removed
                .withPageLabelRange(3, "A")
                .get();

        CatalogPageLabelsMerger merger = new CatalogPageLabelsMerger(CatalogPageLabelsPolicy.RETAIN);
        merger.add(doc1, all(doc1));
        merger.add(doc2, allBut(doc2, 1, 2, 3));

        PDPageLabels mergedLabels = merger.getMergedPageLabels();

        assertPageLabelIndexesAre(mergedLabels, 0, 2);
        assertPageLabelRangeIs(mergedLabels, 0, "r");
        assertPageLabelRangeIs(mergedLabels, 2, "A");
    }

    @Test
    public void mergeDocsSubsetOfPagesFromDocWithComplexLabeling2() {
        PDDocument doc1 = new DocBuilder().withPages(2)
                .withPageLabelRange(0, "r")
                .get();
        PDDocument doc2 = new DocBuilder().withPages(5)
                .withPageLabelRange(0, "D") // these are getting partly removed
                .withPageLabelRange(3, "A")
                .get();

        CatalogPageLabelsMerger merger = new CatalogPageLabelsMerger(CatalogPageLabelsPolicy.RETAIN);
        merger.add(doc1, all(doc1));
        merger.add(doc2, allBut(doc2, 2, 3));

        PDPageLabels mergedLabels = merger.getMergedPageLabels();

        assertPageLabelIndexesAre(mergedLabels, 0, 2, 3);
        assertPageLabelRangeIs(mergedLabels, 0, "r");
        assertPageLabelRangeIs(mergedLabels, 2, "D");
        assertPageLabelRangeIs(mergedLabels, 3, "A");
    }

    @Test
    public void mergeDocsWithDecimalIgnoringStart() {
        PDDocument doc1 = new DocBuilder().withPages(2)
                .withPageLabelRange(0, "D", null, 1)
                .get();
        PDDocument doc2 = new DocBuilder().withPages(5)
                .withPageLabelRange(0, "D", null, 1)
                .withPageLabelRange(3, "A")
                .get();

        CatalogPageLabelsMerger merger = new CatalogPageLabelsMerger(CatalogPageLabelsPolicy.RETAIN);
        merger.add(doc1, all(doc1));
        merger.add(doc2, allBut(doc2));

        PDPageLabels mergedLabels = merger.getMergedPageLabels();

        assertPageLabelIndexesAre(mergedLabels, 0, 2, 5);
        assertPageLabelRangeIs(mergedLabels, 0, "D", null, null);
        assertPageLabelRangeIs(mergedLabels, 2, "D", null, null);
        assertPageLabelRangeIs(mergedLabels, 5, "A");
    }

    private static Set<Integer> all(PDDocument doc) {
        Set<Integer> pages = new TreeSet<>();
        for(int i = 1; i <= doc.getNumberOfPages(); i++){
            pages.add(i);
        }
        return pages;
    }

    private static Set<Integer> allBut(PDDocument doc, Integer... exceptPages) {
        Set<Integer> pages = new TreeSet<>();
        Collection<Integer> except = Arrays.asList(exceptPages);

        for(int i = 1; i <= doc.getNumberOfPages(); i++){
            if(except.contains(i)) {
                continue;
            }
            pages.add(i);
        }
        return pages;
    }
}