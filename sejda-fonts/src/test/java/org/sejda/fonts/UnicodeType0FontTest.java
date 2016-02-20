package org.sejda.fonts;

import static org.junit.Assert.assertNotNull;

import org.junit.Test;

public class UnicodeType0FontTest {

    @Test
    public void allResourcesExist() {
        for (UnicodeType0Font font : UnicodeType0Font.values()) {
            assertNotNull("Resource missing " + font, font.getFontStream());
        }
    }
}