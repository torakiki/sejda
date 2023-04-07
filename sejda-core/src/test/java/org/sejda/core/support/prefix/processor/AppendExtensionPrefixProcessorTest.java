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
package org.sejda.core.support.prefix.processor;

import org.junit.jupiter.api.Test;
import org.sejda.core.support.prefix.model.PrefixTransformationContext;
import org.sejda.model.SejdaFileExtensions;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.sejda.core.support.prefix.model.NameGenerationRequest.nameRequest;

/**
 * @author Andrea Vacondio
 * 
 */
public class AppendExtensionPrefixProcessorTest {

    private AppendExtensionPrefixProcessor victim = new AppendExtensionPrefixProcessor();

    @Test
    public void testProcess() {
        var context = new PrefixTransformationContext("blabla", nameRequest());
        victim.accept(context);
        assertEquals("blabla.pdf", context.currentPrefix());
    }

    @Test
    public void testProcessNonDefaultExtension() {
        var context = new PrefixTransformationContext("blabla", nameRequest(SejdaFileExtensions.TXT_EXTENSION));
        victim.accept(context);
        assertEquals("blabla.txt", context.currentPrefix());
    }

    @Test
    public void testProcessNullRequest() {
        var context = new PrefixTransformationContext("blabla", null);
        victim.accept(context);
        assertEquals("blabla.pdf", context.currentPrefix());
    }

}
