/*
 * Created on 10/giu/2014
 * Copyright 2014 by Andrea Vacondio (andrea.vacondio@gmail.com).
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); 
 * you may not use this file except in compliance with the License. 
 * You may obtain a copy of the License at 
 * 
 * http://www.apache.org/licenses/LICENSE-2.0 
 * 
 * Unless required by applicable law or agreed to in writing, software 
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. 
 * See the License for the specific language governing permissions and 
 * limitations under the License. 
 */
package org.sejda.model.split;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.sejda.model.exception.TaskExecutionException;
import org.sejda.model.outline.OutlineGoToPageDestinations;

/**
 * @author Andrea Vacondio
 * 
 */
public class GoToPageDestinationsSplitPagesTest {

    private OutlineGoToPageDestinations destinations;

    @Before
    public void setUp() {
        destinations = mock(OutlineGoToPageDestinations.class);
        Set<Integer> pages = new HashSet<Integer>();
        pages.add(3);
        pages.add(4);
        pages.add(10);
        when(destinations.getPages()).thenReturn(pages);
    }

    @Test
    public void firstPageIsOpening() throws TaskExecutionException {
        GoToPageDestinationsSplitPages victim = new GoToPageDestinationsSplitPages(destinations);
        victim.ensureIsValid();
        Assert.assertTrue(victim.isOpening(1));
        Assert.assertFalse(victim.isClosing(1));
    }

    @Test
    public void allPages() {
        GoToPageDestinationsSplitPages victim = new GoToPageDestinationsSplitPages(destinations);
        Assert.assertTrue(victim.isClosing(2));
        Assert.assertTrue(victim.isOpening(3));
        Assert.assertTrue(victim.isClosing(3));
        Assert.assertTrue(victim.isOpening(4));
        Assert.assertFalse(victim.isClosing(4));
        Assert.assertTrue(victim.isOpening(10));
        Assert.assertFalse(victim.isClosing(10));
        Assert.assertTrue(victim.isClosing(9));
    }

    @Test(expected = TaskExecutionException.class)
    public void invalid() throws TaskExecutionException {
        when(destinations.getPages()).thenReturn(Collections.EMPTY_SET);
        GoToPageDestinationsSplitPages victim = new GoToPageDestinationsSplitPages(destinations);
        victim.ensureIsValid();
    }
}
