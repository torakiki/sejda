/*
 * Created on 13/mar/2013
 * Copyright 2011 by Andrea Vacondio (andrea.vacondio@gmail.com).
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
package org.sejda.cli;

import java.util.Arrays;
import java.util.Collection;

import org.junit.Test;
import org.junit.runners.Parameterized.Parameters;
import org.sejda.cli.command.StandardTestableTask;
import org.sejda.cli.command.TestableTask;
import org.sejda.model.pdf.page.PageRange;
import org.sejda.model.pdf.page.PageRangeSelection;

/**
 * For tasks that support page ranges, test various scenarios related to this trait
 *
 * @author Edi Weissmann
 * 
 */
public class PageRangesTraitTest extends AbstractTaskTraitTest {
    public PageRangesTraitTest(TestableTask testableTask) {
        super(testableTask);
    }

    @Parameters
    public static Collection<Object[]> data() {
        return asParameterizedTestData(
                Arrays.asList(StandardTestableTask.PDF_TO_JPEG, StandardTestableTask.PDF_TO_PNG,
                        StandardTestableTask.PDF_TO_MULTIPLE_TIFF,
                        StandardTestableTask.EXTRACT_PAGES));
    }

    @Test
    public void pageRanges() {
        PageRangeSelection parameters = defaultCommandLine().with("-s", "3,5,8-10,2,2,9-9,30-").invokeSejdaConsole();

        assertContainsAll(parameters.getPageSelection(), Arrays.asList(new PageRange(3, 3), new PageRange(5, 5),
                new PageRange(8, 10), new PageRange(2, 2), new PageRange(9, 9), new PageRange(30)));
    }
}
