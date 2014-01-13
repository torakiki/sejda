/*
 * Created on 22/nov/2012
 * Copyright 2011 by Andrea Vacondio (andrea.vacondio@gmail.com).
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
package org.sejda.core.support.util;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.sejda.model.notification.EventListener;
import org.sejda.model.notification.event.AbstractNotificationEvent;
import org.sejda.model.notification.event.PercentageOfWorkDoneChangedEvent;
import org.sejda.model.notification.event.TaskExecutionFailedEvent;

/**
 * @author Andrea Vacondio
 * 
 */
public class ReflectionUtilsTest {

    @Test
    public void testFailingInfer() {
        TestListener<TaskExecutionFailedEvent> victim = new TestListener<TaskExecutionFailedEvent>();
        assertEquals(null, ReflectionUtils.inferParameterClass(victim.getClass(), "onEvent"));
    }

    @Test
    public void testInfer() {
        SecondTestListener victim = new SecondTestListener();
        assertEquals(PercentageOfWorkDoneChangedEvent.class,
                ReflectionUtils.inferParameterClass(victim.getClass(), "onEvent"));
    }

    private class SecondTestListener implements EventListener<PercentageOfWorkDoneChangedEvent> {
        public void onEvent(PercentageOfWorkDoneChangedEvent event) {
            // nothing
        }
    }

    private class TestListener<T extends AbstractNotificationEvent> implements EventListener<T> {
        public void onEvent(T event) {
            // nothing
        }
    }
}
