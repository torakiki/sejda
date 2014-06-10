package org.sejda.model.split;

import java.util.ArrayList;

import org.junit.Assert;
import org.junit.Test;
import org.sejda.model.exception.TaskExecutionException;

public class SplitPagesTest {

    @Test(expected = TaskExecutionException.class)
    public void testFailingEnsureIsValid() throws TaskExecutionException {
        new SplitPages(new ArrayList<Integer>()).ensureIsValid();
    }

    @Test
    public void firstPage() {
        SplitPages victim = new SplitPages(1);
        Assert.assertTrue(victim.isOpening(1));
        Assert.assertTrue(victim.isClosing(1));
        Assert.assertTrue(victim.isOpening(2));
    }

    @Test
    public void firstPageAndAnother() {
        ArrayList<Integer> pages = new ArrayList<Integer>();
        pages.add(1);
        pages.add(3);
        SplitPages victim = new SplitPages(pages);
        Assert.assertTrue(victim.isOpening(1));
        Assert.assertTrue(victim.isClosing(1));
        Assert.assertTrue(victim.isOpening(2));
        Assert.assertFalse(victim.isClosing(2));
        Assert.assertTrue(victim.isClosing(3));
        Assert.assertTrue(victim.isOpening(4));
    }

    @Test
    public void secondPage() {
        SplitPages victim = new SplitPages(2);
        Assert.assertTrue(victim.isOpening(1));
        Assert.assertFalse(victim.isClosing(1));
        Assert.assertTrue(victim.isClosing(2));
        Assert.assertTrue(victim.isOpening(3));
    }
}
