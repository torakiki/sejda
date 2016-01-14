/*
 * Copyright 2012 by Eduard Weissmann (edi.weissmann@gmail.com).
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
package org.sejda.impl.sambox;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

import org.sejda.core.service.SetHeaderFooterTaskTest;
import org.sejda.impl.sambox.component.PdfTextExtractorByArea;
import org.sejda.model.exception.TaskIOException;
import org.sejda.model.parameter.SetHeaderFooterParameters;
import org.sejda.model.task.Task;
import org.sejda.sambox.pdmodel.PDPage;

/**
 * @author Eduard Weissmann
 */
public class SetHeaderFooterSamboxTaskTest extends SetHeaderFooterTaskTest {

    @Override
    public Task<SetHeaderFooterParameters> getTask() {
        return new SetHeaderFooterTask();
    }

    @Override
    protected void assertFooterHasText(PDPage page, String expectedText) {
        try {
            assertThat(new PdfTextExtractorByArea().extractFooterText(page).trim(), is(expectedText));
        } catch (TaskIOException e) {
            fail(e.getMessage());
        }
    }

    @Override
    protected void assertHeaderHasText(PDPage page, String expectedText) {
        try {
            assertThat(new PdfTextExtractorByArea().extractHeaderText(page).trim(), is(expectedText));
        } catch (TaskIOException e) {
            fail(e.getMessage());
        }
    }

}
