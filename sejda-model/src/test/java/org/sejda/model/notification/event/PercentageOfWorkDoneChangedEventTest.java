/*
 * Created on 30/ott/2011
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
package org.sejda.model.notification.event;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.math.BigDecimal;

import org.junit.Test;
import org.sejda.model.task.NotifiableTaskMetadata;

/**
 * @author Andrea Vacondio
 * 
 */
public class PercentageOfWorkDoneChangedEventTest {

    @Test
    public void zeroPercentageConstructor() {
        PercentageOfWorkDoneChangedEvent victim = new PercentageOfWorkDoneChangedEvent(NotifiableTaskMetadata.NULL);
        assertEquals(BigDecimal.ZERO, victim.getPercentage());
        assertFalse(victim.isUndetermined());
    }

    @Test
    public void percentageConstructor() {
        PercentageOfWorkDoneChangedEvent victim = new PercentageOfWorkDoneChangedEvent(BigDecimal.ONE,
                NotifiableTaskMetadata.NULL);
        assertEquals(BigDecimal.ONE, victim.getPercentage());
        assertFalse(victim.isUndetermined());
    }

    @Test
    public void isUndetermined() {
        PercentageOfWorkDoneChangedEvent victim = new PercentageOfWorkDoneChangedEvent(
                PercentageOfWorkDoneChangedEvent.UNDETERMINED, NotifiableTaskMetadata.NULL);
        assertTrue(victim.isUndetermined());
    }
}
