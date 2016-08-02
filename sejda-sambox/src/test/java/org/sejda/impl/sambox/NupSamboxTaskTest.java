/*
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
package org.sejda.impl.sambox;

import org.sejda.core.service.NupTaskTest;
import org.sejda.impl.sambox.component.PdfTextExtractorByArea;
import org.sejda.model.exception.TaskIOException;
import org.sejda.model.parameter.NupParameters;
import org.sejda.model.task.Task;
import org.sejda.sambox.pdmodel.PDPage;

import java.awt.*;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

public class NupSamboxTaskTest extends NupTaskTest {

    @Override
    public Task<NupParameters> getTask() {
        return new NupTask();
    }

    @Override
    protected void assertPageHasText(PDPage page, String expectedText) {
        try {
            Rectangle rectangle = new Rectangle((int)page.getMediaBox().getWidth(), (int)page.getMediaBox().getHeight());
            assertThat(new PdfTextExtractorByArea().extractTextFromArea(page, rectangle).trim().replaceAll("\n", " "), is(expectedText));
        } catch (TaskIOException e) {
            fail(e.getMessage());
        }
    }

}
