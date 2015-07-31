package org.sejda.model.pdf.numbering;

import org.junit.Test;
import static org.junit.Assert.*;

public class BatesSequenceTest {

    @Test
    public void checkIncrement() {
        BatesSequence s = new BatesSequence(1, 5, 6);
        assertEquals("000001", s.next());
        assertEquals("000006", s.next());
    }

    @Test
    public void checkDigits() {
        BatesSequence s = new BatesSequence(1, 1, 3);
        assertEquals("001", s.next());
        assertEquals("002", s.next());
    }

    @Test
    public void checkStartFrom() {
        BatesSequence s = new BatesSequence(100, 1, 3);
        assertEquals("100", s.next());
        assertEquals("101", s.next());
    }
}
