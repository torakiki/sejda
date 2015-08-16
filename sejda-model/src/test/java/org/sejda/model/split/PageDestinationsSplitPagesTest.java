/*
 * Created on 10/giu/2014
 * Copyright 2014 by Andrea Vacondio (andrea.vacondio@gmail.com).
 * 
 * This file is part of the Sejda source code
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
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
import org.sejda.model.outline.OutlinePageDestinations;

/**
 * @author Andrea Vacondio
 * 
 */
public class PageDestinationsSplitPagesTest {

    private OutlinePageDestinations destinations;

    @Before
    public void setUp() {
        destinations = mock(OutlinePageDestinations.class);
        Set<Integer> pages = new HashSet<Integer>();
        pages.add(3);
        pages.add(4);
        pages.add(10);
        when(destinations.getPages()).thenReturn(pages);
    }

    @Test
    public void firstPageIsOpening() throws TaskExecutionException {
        PageDestinationsSplitPages victim = new PageDestinationsSplitPages(destinations);
        victim.ensureIsValid();
        Assert.assertTrue(victim.isOpening(1));
        Assert.assertFalse(victim.isClosing(1));
    }

    @Test
    public void allPages() {
        PageDestinationsSplitPages victim = new PageDestinationsSplitPages(destinations);
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
        PageDestinationsSplitPages victim = new PageDestinationsSplitPages(destinations);
        victim.ensureIsValid();
    }
}
