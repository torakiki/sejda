package org.sejda.core.manipulation.model.task.itext.component.split;

import java.util.ArrayList;

import org.junit.Assert;
import org.junit.Test;
import org.sejda.core.exception.TaskExecutionException;

public class SplitPagesTest {

    @Test(expected = TaskExecutionException.class)
    public void testFailingEnsureIsValid() throws TaskExecutionException {
        new SplitPages(new ArrayList<Integer>()).ensureIsValid();
    }

    @Test
    public void testStatus() {
        ArrayList<Integer> pages = new ArrayList<Integer>();
        pages.add(1);
        pages.add(5);
        SplitPages victim = new SplitPages(pages);
        Assert.assertTrue(victim.isClosing(4));
        Assert.assertFalse(victim.isClosing(5));
        Assert.assertTrue(victim.isOpening(5));
    }
}
