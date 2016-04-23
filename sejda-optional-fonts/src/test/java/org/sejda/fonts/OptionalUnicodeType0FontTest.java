package org.sejda.fonts;

import static org.junit.Assert.assertNotNull;

import org.junit.Test;

public class OptionalUnicodeType0FontTest {

    @Test
    public void allResourcesExist() {
        for (OptionalUnicodeType0Font font : OptionalUnicodeType0Font.values()) {
            assertNotNull("Resource missing " + font, font.getFontStream());
        }
    }
}