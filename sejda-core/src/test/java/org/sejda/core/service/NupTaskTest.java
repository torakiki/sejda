/*
 * Copyright 2016 by Eduard Weissmann (edi.weissmann@gmail.com).
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
package org.sejda.core.service;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

import java.io.IOException;
import java.util.HashSet;
import java.util.List;

import org.junit.Ignore;
import org.junit.Test;
import org.sejda.model.nup.PageOrder;
import org.sejda.model.output.ExistingOutputPolicy;
import org.sejda.model.parameter.NupParameters;
import org.sejda.sambox.pdmodel.PDDocument;
import org.sejda.sambox.pdmodel.PDPage;
import org.sejda.sambox.pdmodel.common.PDRectangle;
import org.sejda.sambox.pdmodel.interactive.action.PDActionURI;
import org.sejda.sambox.pdmodel.interactive.annotation.PDAnnotation;
import org.sejda.sambox.pdmodel.interactive.annotation.PDAnnotationLink;

@Ignore
public abstract class NupTaskTest extends BaseTaskTest<NupParameters> {
    @Test
    public void test2up() throws IOException {
        NupParameters params = getParams(2, PageOrder.HORIZONTAL, "pdf/bordered.pdf");

        execute(params);
        PDDocument result = testContext.assertTaskCompleted();
        testContext.assertPages(4);
        testContext.forEachPdfOutput(d ->
                assertEquals(new PDRectangle(1224, 792), d.getPage(1).getMediaBox())
        );

        assertPageHasText(result.getPage(0), "PAGE 1PAGE 2");
        assertPageHasText(result.getPage(1), "PAGE 3PAGE 4");
        assertPageHasText(result.getPage(2), "PAGE 5 PAGE 6");
        assertPageHasText(result.getPage(3), "PAGE 7");
    }

    @Test
    public void test4upPreservingSize() throws IOException {
        NupParameters params = getParams(2, PageOrder.HORIZONTAL, "pdf/bordered.pdf");
        params.setPreservePageSize(true);

        execute(params);
        PDDocument result = testContext.assertTaskCompleted();
        testContext.assertPages(4);
        testContext.forEachPdfOutput(d ->
                        assertEquals(new PDRectangle(792, 612), d.getPage(1).getMediaBox())
        );

        assertPageHasText(result.getPage(0), "PAGE 1PAGE 2");
        assertPageHasText(result.getPage(1), "PAGE 3PAGE 4");
        assertPageHasText(result.getPage(2), "PAGE 5 PAGE 6");
        assertPageHasText(result.getPage(3), "PAGE 7");
    }

    @Test
    public void test2upRotated() throws IOException {
        NupParameters params = getParams(2, PageOrder.HORIZONTAL, "pdf/bordered_rotated.pdf");
        execute(params);
        PDDocument result = testContext.assertTaskCompleted();
        testContext.assertPages(4);

        assertPageHasText(result.getPage(0), "PAGE 1PAGE 2");
        assertPageHasText(result.getPage(1), "PAGE 3PAGE 4");
        assertPageHasText(result.getPage(3), "PAGE 7");
    }

    @Test
    public void test4up() throws IOException {
        NupParameters params = getParams(4, PageOrder.HORIZONTAL, "pdf/bordered.pdf");
        execute(params);
        PDDocument result = testContext.assertTaskCompleted();
        testContext.assertPages(2);

        assertPageHasText(result.getPage(0), "PAGE 1PAGE 2PAGE 3PAGE 4");
        assertPageHasText(result.getPage(1), "PAGE 5 PAGE 6PAGE 7");
    }

    @Test
    public void test4upVertical() throws IOException {
        NupParameters params = getParams(4, PageOrder.VERTICAL, "pdf/bordered.pdf");
        execute(params);
        PDDocument result = testContext.assertTaskCompleted();
        testContext.assertPages(2);

        assertPageHasText(result.getPage(0), "PAGE 1 PAGE 3PAGE 2 PAGE 4");
        assertPageHasText(result.getPage(1), "PAGE 5 PAGE 7PAGE 6");
    }

    @Test
    public void test2upWithDuplicatedCroppedPages() throws IOException {
        NupParameters params = getParams(2, PageOrder.VERTICAL, "pdf/split-down-the-middle-with-cropboxes.pdf");
        execute(params);
        PDDocument result = testContext.assertTaskCompleted();
        testContext.assertPages(2);
        assertPageHasText(result.getPage(0), "L1 L1 R1 R1");
        assertPageHasText(result.getPage(1), "L2 L2 R2 R2");
    }

    @Test
    @Ignore
    // TODO
    public void test2upWithSomeSharedContentStream() throws IOException {
        NupParameters params = getParams(2, PageOrder.VERTICAL, "pdf/some-pages-share-contentstream.pdf");
        execute(params);
        PDDocument result = testContext.assertTaskCompleted();
        testContext.assertPages(2);
        assertPageHasText(result.getPage(0), "First L1 L1");
        assertPageHasText(result.getPage(1), "Third R1 R1");
    }

    @Test
    public void testKeepingLinks() throws IOException {
        NupParameters params = getParams(2, PageOrder.HORIZONTAL, "pdf/doc-with-links.pdf");
        execute(params);
        PDDocument result = testContext.assertTaskCompleted();
        testContext.assertPages(1);

        List<PDAnnotation> annotations = result.getPage(0).getAnnotations();

        // Ehm.. the list has 4 items, but only 2 unique
        assertThat(new HashSet<>(annotations).size(), is(2));

        PDAnnotation link1 = annotations.stream()
                .filter(a -> a instanceof PDAnnotationLink)
                .filter(a -> ((PDActionURI) ((PDAnnotationLink) a).getAction()).getURI().contains("google.com"))
                .findFirst().get();

        assertThat(link1.getRectangle(), is(new PDRectangle(684, 561, 149, 45)));
    }

    @Test
    public void testKeepingLinksWhenPreservingPageSize() throws IOException {
        NupParameters params = getParams(2, PageOrder.HORIZONTAL, "pdf/doc-with-links.pdf", true);
        execute(params);
        PDDocument result = testContext.assertTaskCompleted();
        testContext.assertPages(1);

        List<PDAnnotation> annotations = result.getPage(0).getAnnotations();

        // Ehm.. the list has 4 items, but only 2 unique
        assertThat(new HashSet<>(annotations).size(), is(2));

        PDAnnotation link1 = annotations.stream()
                .filter(a -> a instanceof PDAnnotationLink)
                .filter(a -> ((PDActionURI) ((PDAnnotationLink) a).getAction()).getURI().contains("google.com"))
                .findFirst().get();

        assertThat(link1.getRectangle(), is(new PDRectangle(442.58823f, 363.0f, 539.0f - 442.58823f, 392.11765f - 363.0f)));
    }

    private NupParameters getParams(int n, PageOrder order, String input) throws IOException {
        return getParams(n, order, input, false);
    }

    private NupParameters getParams(int n, PageOrder order, String input, boolean preservePageSize) throws IOException {
        NupParameters parameters = new NupParameters(n, order);
        parameters.setPreservePageSize(preservePageSize);
        parameters.addSource(customInput(input));
        parameters.setExistingOutputPolicy(ExistingOutputPolicy.OVERWRITE);

        testContext.directoryOutputTo(parameters);
        return parameters;
    }

    protected abstract void assertPageHasText(PDPage page, String expectedText);
}
