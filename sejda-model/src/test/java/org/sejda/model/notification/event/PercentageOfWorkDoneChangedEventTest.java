/*
 * Created on 30/ott/2011
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
