/*
 * Created on 22/nov/2012
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
package org.sejda.core.support.util;

import org.junit.jupiter.api.Test;
import org.sejda.model.notification.EventListener;
import org.sejda.model.notification.event.AbstractNotificationEvent;
import org.sejda.model.notification.event.PercentageOfWorkDoneChangedEvent;
import org.sejda.model.notification.event.TaskExecutionFailedEvent;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

/**
 * @author Andrea Vacondio
 */
public class ReflectionUtilsTest {

    @Test
    public void testFailingInfer() {
        TestListener<TaskExecutionFailedEvent> victim = new TestListener<>();
        assertNull(ReflectionUtils.inferParameterClass(victim.getClass(), "onEvent"));
    }

    @Test
    public void testInfer() {
        SecondTestListener victim = new SecondTestListener();
        assertEquals(PercentageOfWorkDoneChangedEvent.class,
                ReflectionUtils.inferParameterClass(victim.getClass(), "onEvent"));
    }

    private class SecondTestListener implements EventListener<PercentageOfWorkDoneChangedEvent> {
        @Override
        public void onEvent(PercentageOfWorkDoneChangedEvent event) {
            // nothing
        }
    }

    private class TestListener<T extends AbstractNotificationEvent> implements EventListener<T> {
        @Override
        public void onEvent(T event) {
            // nothing
        }
    }
}
