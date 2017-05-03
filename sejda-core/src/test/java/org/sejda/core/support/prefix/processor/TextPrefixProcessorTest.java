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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;

import org.junit.Test;
import org.sejda.core.support.prefix.model.NameGenerationRequest;

public class TextPrefixProcessorTest {

    @Test
    public void producesFilenameFriendlyResults() throws IOException {
        String text = "This is an example\nof\t\f\r\n text;\\// that` is \" '' not filename friendly";
        NameGenerationRequest req = NameGenerationRequest.nameRequest().text(text);
        String actual = new TextPrefixProcessor().process("prefix-[TEXT]-suffix", req);
        assertEquals("prefix-This is an exampleof text that is   not filename friendly-suffix", actual);

        File file = File.createTempFile(actual, ".pdf");
        assertTrue(file.exists());
        file.delete();
    }
}
