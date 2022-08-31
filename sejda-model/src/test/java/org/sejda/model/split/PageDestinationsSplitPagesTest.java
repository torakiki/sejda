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

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.sejda.model.exception.TaskExecutionException;
import org.sejda.model.outline.OutlinePageDestinations;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * @author Andrea Vacondio
 */
public class PageDestinationsSplitPagesTest {

    private OutlinePageDestinations destinations;

    @BeforeEach
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
        assertTrue(victim.isOpening(1));
        assertFalse(victim.isClosing(1));
    }

    @Test
    public void allPages() {
        PageDestinationsSplitPages victim = new PageDestinationsSplitPages(destinations);
        assertTrue(victim.isClosing(2));
        assertTrue(victim.isOpening(3));
        assertTrue(victim.isClosing(3));
        assertTrue(victim.isOpening(4));
        assertFalse(victim.isClosing(4));
        assertTrue(victim.isOpening(10));
        assertFalse(victim.isClosing(10));
        assertTrue(victim.isClosing(9));
    }

    @Test
    public void invalid() {
        when(destinations.getPages()).thenReturn(Collections.emptySet());
        PageDestinationsSplitPages victim = new PageDestinationsSplitPages(destinations);
        assertThrows(TaskExecutionException.class, victim::ensureIsValid);
    }
}
