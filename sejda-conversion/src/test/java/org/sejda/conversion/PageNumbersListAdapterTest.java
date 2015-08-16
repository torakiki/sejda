/*
 * Created on 09/giu/2014
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
package org.sejda.conversion;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;

import org.junit.Test;
import org.sejda.conversion.exception.ConversionException;

/**
 * @author Andrea Vacondio
 *
 */
public class PageNumbersListAdapterTest {

    @Test
    public void positives() {
        assertThat(new PageNumbersListAdapter("1").getPageNumbers(), contains(1));
        assertThat(new PageNumbersListAdapter("1,3,6").getPageNumbers(), contains(1, 3, 6));
        assertThat(new PageNumbersListAdapter(" 1, 3, 6 ").getPageNumbers(), contains(1, 3, 6));
    }

    @Test(expected = ConversionException.class)
    public void invalidNumberCollection() {
        new PageNumbersListAdapter("1,3,a");
    }

    @Test(expected = ConversionException.class)
    public void invalidNumber() {
        new PageNumbersListAdapter("chuck");
    }
}
