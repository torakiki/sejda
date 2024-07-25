/*
 * Created on 03/lug/2010
 *
 * Copyright 2010 Sober Lemur S.r.l. and Sejda BV.
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
package org.sejda.core.support.prefix.processor;

import org.junit.jupiter.api.Test;
import org.sejda.core.support.prefix.model.PrefixTransformationContext;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.sejda.core.support.prefix.model.NameGenerationRequest.nameRequest;

/**
 * Test unit for the {@link PrependPrefixProcessor}
 *
 * @author Andrea Vacondio
 */
public class PrependPrefixProcessorTest {

    private PrependPrefixProcessor victim = new PrependPrefixProcessor();
    @Test
    public void positive() {
        var context = new PrefixTransformationContext("prefix_", nameRequest().originalName("name"));
        victim.accept(context);
        assertEquals("prefix_name", context.currentPrefix());
    }

    @Test
    public void nullRequest() {
        var context = new PrefixTransformationContext("prefix_", null);
        victim.accept(context);
        assertEquals("prefix_", context.currentPrefix());
    }

    @Test
    public void noName() {
        var context = new PrefixTransformationContext("prefix_", nameRequest());
        victim.accept(context);
        assertEquals("prefix_", context.currentPrefix());
    }

    @Test
    public void alreadyUnique() {
        var context = new PrefixTransformationContext("prefix_", nameRequest().originalName("name"));
        context.currentPrefix("transformed_prefix");
        victim.accept(context);
        assertEquals("transformed_prefix", context.currentPrefix());
    }

}
