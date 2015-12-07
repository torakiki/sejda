/*
 * Copyright 2015 by Edi Weissmann (edi.weissmann@gmail.com).
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
package org.sejda.core.service;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.sejda.TestUtils;
import org.sejda.core.context.DefaultSejdaContext;
import org.sejda.core.context.SejdaContext;
import org.sejda.model.exception.TaskException;
import org.sejda.model.input.PdfSource;
import org.sejda.model.input.PdfStreamSource;
import org.sejda.model.output.ExistingOutputPolicy;
import org.sejda.model.parameter.SplitDownTheMiddleParameters;
import org.sejda.model.pdf.PdfVersion;
import org.sejda.model.repaginate.Repagination;
import org.sejda.model.task.Task;

import com.lowagie.text.pdf.PdfReader;

@Ignore
public abstract class SplitDownTheMiddleTaskTest extends PdfOutEnabledTest implements TestableTask<SplitDownTheMiddleParameters> {

    private DefaultTaskExecutionService victim = new DefaultTaskExecutionService();

    private SejdaContext context = mock(DefaultSejdaContext.class);
    private SplitDownTheMiddleParameters parameters;

    @Before
    public void setUp() {
        TestUtils.setProperty(victim, "context", context);
    }

    private void setUpParameters(List<PdfSource<?>> sources) {
        parameters = new SplitDownTheMiddleParameters();
        parameters.setExistingOutputPolicy(ExistingOutputPolicy.OVERWRITE);
        parameters.setCompress(true);
        parameters.setVersion(PdfVersion.VERSION_1_6);
        parameters.addSources(sources);
    }

    private List<PdfSource<?>> landscapeInput() {
        List<PdfSource<?>> input = new ArrayList<PdfSource<?>>();
        input.add(PdfStreamSource.newInstanceNoPassword(getClass().getClassLoader()
                .getResourceAsStream("pdf/split_in_two_landscape_sample.pdf"), "a.pdf"));
        return input;
    }

    private List<PdfSource<?>> portraitInput() {
        List<PdfSource<?>> input = new ArrayList<PdfSource<?>>();
        input.add(PdfStreamSource.newInstanceNoPassword(getClass().getClassLoader()
                .getResourceAsStream("pdf/split_in_two_portrait_sample.pdf"), "a.pdf"));
        return input;
    }

    private List<PdfSource<?>> lastFirstRepaginationInput() {
        List<PdfSource<?>> input = new ArrayList<PdfSource<?>>();
        input.add(PdfStreamSource.newInstanceNoPassword(getClass().getClassLoader()
                .getResourceAsStream("pdf/split_in_two_last_first_repagination_sample.pdf"), "a.pdf"));
        return input;
    }

    private List<PdfSource<?>> lastFirstRepaginationInputUnevenPagePairs() {
        List<PdfSource<?>> input = new ArrayList<PdfSource<?>>();
        input.add(PdfStreamSource.newInstanceNoPassword(getClass().getClassLoader()
                .getResourceAsStream("pdf/split_in_two_last_first_repagination_uneven_sample.pdf"), "a.pdf"));
        return input;
    }

    @Test
    public void splitLandscapeMode() throws TaskException, IOException {
        setUpParameters(landscapeInput());
        execute();
        assertNumberOfPages(4);
        assertPageText("L1L1", "R1R1", "L2L2", "R2R2");
    }

    @Test
    public void splitPortraitMode() throws TaskException, IOException {
        setUpParameters(portraitInput());
        execute();
        assertNumberOfPages(4);
        assertPageText("L1L1", "R1R1", "L2L2", "R2R2");
    }

    @Test
    public void lastFirstRepagination() throws TaskException, IOException {
        setUpParameters(lastFirstRepaginationInput());
        parameters.setRepagination(Repagination.LAST_FIRST);
        execute();
        assertNumberOfPages(10);
        assertPageText("1", "2", "3", "4", "5", "6", "7", "8", "9", "10");
    }

    @Test
    public void lastFirstRepaginationUnevenPagePairs() throws TaskException, IOException {
        setUpParameters(lastFirstRepaginationInputUnevenPagePairs());
        parameters.setRepagination(Repagination.LAST_FIRST);
        execute();
        assertNumberOfPages(12);
        assertPageText("1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12");
    }

    void execute() throws TaskException, IOException {
        when(context.getTask(parameters)).thenReturn((Task) getTask());
        initializeNewStreamOutput(parameters);
        victim.execute(parameters);
        PdfReader reader = null;
        try {
            reader = getReaderFromResultZipStream();
            assertCreator(reader);
            assertVersion(reader, PdfVersion.VERSION_1_6);
        } finally {
            nullSafeCloseReader(reader);
        }
    }
}
