package org.sejda.model.split;

import org.junit.jupiter.api.Test;
import org.sejda.model.exception.TaskExecutionException;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class SplitPagesTest {

    @Test
    public void testFailingEnsureIsValid() {
        assertThrows(TaskExecutionException.class, () -> new SplitPages(new ArrayList<Integer>()).ensureIsValid());
    }

    @Test
    public void firstPage() {
        SplitPages victim = new SplitPages(1);
        assertTrue(victim.isOpening(1));
        assertTrue(victim.isClosing(1));
        assertTrue(victim.isOpening(2));
    }

    @Test
    public void firstPageAndAnother() {
        ArrayList<Integer> pages = new ArrayList<Integer>();
        pages.add(1);
        pages.add(3);
        SplitPages victim = new SplitPages(pages);
        assertTrue(victim.isOpening(1));
        assertTrue(victim.isClosing(1));
        assertTrue(victim.isOpening(2));
        assertFalse(victim.isClosing(2));
        assertTrue(victim.isClosing(3));
        assertTrue(victim.isOpening(4));
    }

    @Test
    public void secondPage() {
        SplitPages victim = new SplitPages(2);
        assertTrue(victim.isOpening(1));
        assertFalse(victim.isClosing(1));
        assertTrue(victim.isClosing(2));
        assertTrue(victim.isOpening(3));
    }
}
