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

import org.junit.jupiter.api.Test;
import org.sejda.core.support.prefix.model.PrefixTransformationContext;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.sejda.core.support.prefix.model.NameGenerationRequest.nameRequest;

/**
 * @author Andrea Vacondio
 */
public class PrependPageNumberPrefixProcessorTest {
    private PrependPageNumberPrefixProcessor victim = new PrependPageNumberPrefixProcessor();

    @Test
    public void positive() {
        var context = new PrefixTransformationContext("prefix_", nameRequest().page(34));
        victim.accept(context);
        assertEquals("34_prefix_", context.currentPrefix());
    }

    @Test
    public void nullRequest() {
        var context = new PrefixTransformationContext("prefix_", null);
        victim.accept(context);
        assertEquals("prefix_", context.currentPrefix());
    }

    @Test
    public void noPage() {
        var context = new PrefixTransformationContext("prefix_", nameRequest());
        victim.accept(context);
        assertEquals("prefix_", context.currentPrefix());
    }

    @Test
    public void alreadyUnique() {
        var context = new PrefixTransformationContext("prefix_", nameRequest().page(34));
        context.uniqueNames(true);
        victim.accept(context);
        assertEquals("prefix_", context.currentPrefix());
    }
}
