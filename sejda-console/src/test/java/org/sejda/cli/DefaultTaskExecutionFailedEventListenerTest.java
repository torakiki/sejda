/*
 * Created on Oct 12, 2011
 * Copyright 2010 by Eduard Weissmann (edi.weissmann@gmail.com).
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); 
 * you may not use this file except in compliance with the License. 
 * You may obtain a copy of the License at 
 * 
 * http://www.apache.org/licenses/LICENSE-2.0 
 * 
 * Unless required by applicable law or agreed to in writing, software 
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. 
 * See the License for the specific language governing permissions and 
 * limitations under the License. 
 */
package org.sejda.cli;

import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;
import static org.junit.matchers.JUnitMatchers.containsString;

import org.junit.Test;
import org.sejda.core.TestUtils;
import org.sejda.core.exception.SejdaRuntimeException;
import org.sejda.core.exception.TaskException;
import org.sejda.core.notification.event.TaskExecutionFailedEvent;

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
            victim.onEvent(new TaskExecutionFailedEvent(in));
            fail("Expected an exception to be propagated");
        } catch (SejdaRuntimeException actual) {
            assertThat(actual.getMessage(), containsString(expectedExceptionMessage));
        }
    }
}
