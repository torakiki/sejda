package org.sejda.core.support.prefix.processor;

import org.junit.jupiter.api.Test;
import org.sejda.core.support.prefix.model.PrefixTransformationContext;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.sejda.core.support.prefix.model.NameGenerationRequest.nameRequest;

/*
 * Created on 07/04/23
 * Copyright 2023 Sober Lemur S.r.l. and Sejda BV
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
class ToSafeFilenamePrefixProcessorTest {

    private ToSafeFilenamePrefixProcessor victim = new ToSafeFilenamePrefixProcessor();

    @Test
    public void testProcess() {
        var context = new PrefixTransformationContext("blabla\"<>|", nameRequest());
        victim.accept(context);
        assertEquals("blabla", context.currentPrefix());
    }

    @Test
    public void testPassThrough() {
        var context = new PrefixTransformationContext("blabla", nameRequest());
        victim.accept(context);
        assertEquals("blabla", context.currentPrefix());
    }

}