/*
 * Created on 24/ago/2011
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
package org.sejda.core.support.prefix.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.sejda.core.support.prefix.model.NameGenerationRequest.nameRequest;

/**
 * @author Andrea Vacondio
 * 
 */
public class NameGenerationRequestTest {

    @Test
    public void blankExtension() {
        assertThrows(IllegalArgumentException.class, () -> nameRequest(""));
    }

    @Test
    public void nullExtension() {
        assertThrows(IllegalArgumentException.class, () -> nameRequest(null));
    }

    @Test
    public void testGetValueReturnsNullWhenKeyNotFound() {
        assertNull(nameRequest().getValue("nonexistent_key", Integer.class));
    }

    @Test
    public void testGetValueReturnsNullWhenValueIsNotOfGivenType() {
        NameGenerationRequest victim = nameRequest();
        victim.setValue("key", "value");
        assertNull(victim.getValue("key", Integer.class));
    }

    @Test
    public void testGetValueReturnsValueWhenKeyAndTypeMatch() {
        NameGenerationRequest victim = nameRequest();
        victim.setValue("key", 42);
        assertEquals(42, victim.getValue("key", Integer.class));
    }

    @Test
    public void testSetValueAllowsNullValues() {
        NameGenerationRequest victim = nameRequest();
        victim.setValue("key", null);
        assertNull(victim.getValue("key", Object.class));
    }

    @Test
    public void testSetValueThrowsIllegalArgumentExceptionWhenKeyIsNull() {
        assertThrows(IllegalArgumentException.class, () -> nameRequest().setValue(null, "value"));
    }
}
