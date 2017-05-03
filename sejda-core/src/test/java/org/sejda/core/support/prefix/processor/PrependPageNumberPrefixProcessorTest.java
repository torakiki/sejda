/*
 * Created on 03 mag 2017
 * Copyright 2017 by Andrea Vacondio (andrea.vacondio@gmail.com).
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
package org.sejda.core.support.prefix.processor;

import static org.junit.Assert.assertEquals;
import static org.sejda.core.support.prefix.model.NameGenerationRequest.nameRequest;

import org.junit.Test;

/**
 * @author Andrea Vacondio
 *
 */
public class PrependPageNumberPrefixProcessorTest {
    private PrependPageNumberPrefixProcessor victim = new PrependPageNumberPrefixProcessor();

    @Test
    public void positive() {
        assertEquals("34_prefix_", victim.process("prefix_", nameRequest().page(34)));
    }

    @Test
    public void nullRequest() {
        assertEquals("prefix_", victim.process("prefix_", null));
    }

    @Test
    public void noPage() {
        assertEquals("prefix_", victim.process("prefix_", nameRequest()));
    }
}
