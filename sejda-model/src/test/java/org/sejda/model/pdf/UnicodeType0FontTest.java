package org.sejda.model.pdf;

import org.junit.Test;

import static org.junit.Assert.assertNotNull;

public class UnicodeType0FontTest {

    @Test
    public void allResourcesExist() {
        for (UnicodeType0Font font : UnicodeType0Font.values()) {
            assertNotNull("Resource missing " + font, font.getResourceStream());
        }
    }
}