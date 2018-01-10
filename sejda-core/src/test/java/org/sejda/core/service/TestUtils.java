/*
 * Copyright 2017 by Eduard Weissmann (edi.weissmann@gmail.com).
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
package org.sejda.core.service;

import org.sejda.core.support.util.StringUtils;
import org.sejda.sambox.pdmodel.PDPage;
import org.sejda.sambox.pdmodel.common.PDRectangle;
import org.sejda.sambox.pdmodel.interactive.annotation.PDAnnotationLink;
import org.sejda.sambox.pdmodel.interactive.documentnavigation.destination.PDPageFitWidthDestination;
import org.sejda.sambox.text.PDFTextStripperByArea;

import java.awt.*;
import java.io.IOException;
import java.util.*;
import java.util.List;
import java.util.function.Consumer;

import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import static org.sejda.core.support.util.StringUtils.normalizeLineEndings;

public class TestUtils {

    public static void withPageText(PDPage page, Consumer<String> callback) {
        PDFTextStripperByArea textStripper;
        try {
            textStripper = new PDFTextStripperByArea();
            PDRectangle pageSize = page.getCropBox();
            Rectangle cropBoxRectangle = new Rectangle(0, 0, (int) pageSize.getWidth(), (int) pageSize.getHeight());
            if(page.getRotation() == 90 || page.getRotation() == 270) {
                cropBoxRectangle = new Rectangle(0, 0, (int) pageSize.getHeight(), (int) pageSize.getWidth());
            }
            textStripper.setSortByPosition(true);
            textStripper.addRegion("area1", cropBoxRectangle);
            textStripper.extractRegions(page);
            callback.accept(textStripper.getTextForRegion("area1"));
        } catch (IOException e) {
            fail(e.getMessage());
        }
    }

    public static void assertPageText(PDPage page, String text) {
        withPageText(page, pageText -> {
            assertEquals(text, pageText.replaceAll("[^A-Za-z0-9]", ""));
        });
    }

    public static void assertPageTextExact(PDPage page, String text) {
        withPageText(page, pageText -> {
            assertEquals(text, pageText);
        });
    }

    public static void assertPageTextExactLines(PDPage page, String text) {
        withPageText(page, pageText -> {
            assertEquals(normalizeLineEndings(text), normalizeLineEndings(pageText));
        });
    }

    public static void assertPageTextContains(PDPage page, String text) {
        withPageText(page, pageText -> {
            pageText = StringUtils.normalizeWhitespace(pageText);
            // ignores whitespace
            pageText = pageText.replaceAll("\\s", "");
            assertThat(pageText, containsString(text.replaceAll("\\s", "")));
        });
    }

    public static void assertPageTextDoesNotContain(PDPage page, String text) {
        withPageText(page, pageText -> {
            pageText = StringUtils.normalizeWhitespace(pageText);
            // ignores whitespace
            pageText = pageText.replaceAll("\\s", "");
            assertThat(pageText, not(containsString(text.replaceAll("\\s", ""))));
        });
    }

    public static <T> java.util.List<T> getAnnotationsOf(PDPage page, Class<T> clazz) {
        return iteratorToList(page.getAnnotations().stream()
                .filter(a -> clazz.isInstance(a))
                .map(a -> (T) a)
                .iterator());
    }

    public static <T> List<T> iteratorToList(Iterator<T> iterator) {
        List<T> result = new ArrayList<>();
        while(iterator.hasNext()) {
            result.add(iterator.next());
        }
        return result;
    }

    public static void assertPDRectanglesEqual(PDRectangle expected, PDRectangle actual) {
        assertEquals("lower left x", expected.getLowerLeftX(), actual.getLowerLeftX(), 0.1);
        assertEquals("lower left y", expected.getLowerLeftY(), actual.getLowerLeftY(), 0.1);
        assertEquals("width", expected.getWidth(), actual.getWidth(), 0.1);
        assertEquals("height", expected.getHeight(), actual.getHeight(), 0.1);
    }

    public static void assertPageDestination(PDAnnotationLink link, PDPage expectedPage) throws IOException {
        PDPage actualPage = ((PDPageFitWidthDestination)link.getDestination()).getPage();
        assertEquals(expectedPage, actualPage);
    }
}
