/*
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

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.sejda.core.support.prefix.model.NameGenerationRequest.nameRequest;

public class TextPrefixProcessorTest extends BasePrefixProcessorTest {

    private TextPrefixProcessor victim = new TextPrefixProcessor();

    @Override
    public PrefixProcessor getProcessor() {
        return victim;
    }

    @Test
    public void producesFilenameFriendlyResults() throws IOException {
        String text = "This is an example\nof\t\f\r\n text;\\// that` is \" '' not filename friendly";
        var context = new PrefixTransformationContext("prefix-[TEXT]-suffix", nameRequest().text(text));
        victim.accept(context);
        assertEquals("prefix-This is an exampleof text that is   not filename friendly-suffix",
                context.currentPrefix());

        Path file = Files.createTempFile(context.currentPrefix(), ".pdf");
        assertTrue(Files.exists(file));
        Files.delete(file);
    }
}
