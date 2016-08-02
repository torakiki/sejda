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

import org.junit.Ignore;
import org.junit.Test;
import org.sejda.model.nup.PageOrder;
import org.sejda.model.output.ExistingOutputPolicy;
import org.sejda.model.parameter.NupParameters;
import org.sejda.sambox.pdmodel.PDDocument;
import org.sejda.sambox.pdmodel.PDPage;

import java.io.IOException;

@Ignore
public abstract class NupTaskTest extends BaseTaskTest<NupParameters> {
    @Test
    public void test2up() throws IOException {
        NupParameters params = getParams(2, PageOrder.HORIZONTAL, "pdf/bordered.pdf");
        execute(params);
        PDDocument result = testContext.assertTaskCompleted();
        testContext.assertPages(4);

        assertPageHasText(result.getPage(0), "PAGE 1 PAGE 2");
        assertPageHasText(result.getPage(1), "PAGE 3 PAGE 4");
        assertPageHasText(result.getPage(2), "PAGE 5 PAGE 6");
        assertPageHasText(result.getPage(3), "PAGE 7");
    }

    @Test
    public void test2upRotated() throws IOException {
        NupParameters params = getParams(2, PageOrder.HORIZONTAL, "pdf/bordered_rotated.pdf");
        execute(params);
        PDDocument result = testContext.assertTaskCompleted();
        testContext.assertPages(4);

        assertPageHasText(result.getPage(0), "PAGE 1 PAGE 2");
        assertPageHasText(result.getPage(1), "PAGE 3 PAGE 4");
        assertPageHasText(result.getPage(3), "PAGE 7");
    }

    @Test
    public void test4up() throws IOException {
        NupParameters params = getParams(4, PageOrder.HORIZONTAL, "pdf/bordered.pdf");
        execute(params);
        PDDocument result = testContext.assertTaskCompleted();
        testContext.assertPages(2);

        assertPageHasText(result.getPage(0), "PAGE 1 PAGE 2 PAGE 3 PAGE 4");
        assertPageHasText(result.getPage(1), "PAGE 5 PAGE 6 PAGE 7");
    }

    @Test
    public void test4upVertical() throws IOException {
        NupParameters params = getParams(4, PageOrder.VERTICAL, "pdf/bordered.pdf");
        execute(params);
        PDDocument result = testContext.assertTaskCompleted();
        testContext.assertPages(2);

        assertPageHasText(result.getPage(0), "PAGE 1 PAGE 3 PAGE 2 PAGE 4");
        assertPageHasText(result.getPage(1), "PAGE 5 PAGE 7 PAGE 6");
    }

    private NupParameters getParams(int n, PageOrder order, String input) throws IOException {
        NupParameters parameters = new NupParameters(n, order);
        parameters.addSource(customInput(input));
        parameters.setExistingOutputPolicy(ExistingOutputPolicy.OVERWRITE);

        testContext.directoryOutputTo(parameters);
        return parameters;
    }

    protected abstract void assertPageHasText(PDPage page, String expectedText);
}
