/*
 * Created on Oct 12, 2011
 * Copyright 2010 by Eduard Weissmann (edi.weissmann@gmail.com).
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

import static org.hamcrest.CoreMatchers.containsString;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

import org.junit.Test;
import org.sejda.TestUtils;
import org.sejda.model.exception.SejdaRuntimeException;
import org.sejda.model.exception.TaskException;
import org.sejda.model.notification.event.TaskExecutionFailedEvent;
import org.sejda.model.task.NotifiableTaskMetadata;

/**
 * @author Eduard Weissmann
 * 
 */
public class DefaultTaskExecutionFailedEventListenerTest {

    private final DefaultTaskExecutionFailedEventListener victim = new DefaultTaskExecutionFailedEventListener();

    @Test
    public void equalsHashcode() {
        TestUtils.testEqualsAndHashCodes(victim, new DefaultTaskExecutionFailedEventListener(),
                new DefaultTaskExecutionFailedEventListener(), new LoggingPercentageOfWorkDoneChangeEventListener());
    }

    @Test
    public void onEvent() {
        assertOnEvent(new TaskException("Some task related thing failed"), "Some task related thing failed");
        assertOnEvent(new RuntimeException("Some unexpected thing occured"), "Some unexpected thing occured");
        assertOnEvent(new NullPointerException(), "NullPointerException");
        assertOnEvent(null, "Reason was: ");
    }

    public void assertOnEvent(Exception in, String expectedExceptionMessage) {
        try {
            victim.onEvent(new TaskExecutionFailedEvent(in, NotifiableTaskMetadata.NULL));
            fail("Expected an exception to be propagated");
        } catch (SejdaRuntimeException actual) {
            assertThat(actual.getMessage(), containsString(expectedExceptionMessage));
        }
    }
}
