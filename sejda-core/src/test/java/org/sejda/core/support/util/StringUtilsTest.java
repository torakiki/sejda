package org.sejda.core.support.util;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class StringUtilsTest {

    @Test
    public void compareNormalizedJapanese() {
        assertTrue(StringUtils.equalsNormalized("⽇本語", "日本語"));
    }

    @Test
    public void compareNormalizedLatin() {
        assertTrue(StringUtils.equalsNormalized("blabla", "blabla"));
        assertFalse(StringUtils.equalsNormalized("blabla", "blablà"));
    }
}
