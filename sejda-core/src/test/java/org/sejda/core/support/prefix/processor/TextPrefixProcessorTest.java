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
