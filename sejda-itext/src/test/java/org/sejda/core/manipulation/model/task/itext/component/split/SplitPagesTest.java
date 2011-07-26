package org.sejda.core.manipulation.model.task.itext.component.split;

import org.junit.Assert;
import org.junit.Test;
import org.sejda.core.exception.TaskExecutionException;

public class SplitPagesTest {

    @Test(expected = TaskExecutionException.class)
    public void testFailingEnsureIsValid() throws TaskExecutionException {
        new SplitPages(10).ensureIsValid();
    }

    @Test(expected = IllegalArgumentException.class)
    public void testConstructor() {
        new SplitPages(-0);
    }

    @Test
    public void testStatus() {
        SplitPages victim = new SplitPages(10);
        victim.add(1);
        victim.add(5);
        Assert.assertTrue(victim.isClosing(5));
        Assert.assertFalse(victim.isClosing(6));
        Assert.assertTrue(victim.isOpening(6));
        Assert.assertFalse(victim.isOpening(5));
    }
}
