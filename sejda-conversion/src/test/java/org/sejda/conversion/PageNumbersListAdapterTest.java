/*
 * Created on 09/giu/2014
 * Copyright 2014 Sober Lemur S.r.l. and Sejda BV.
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

import org.junit.jupiter.api.Test;
import org.sejda.conversion.exception.ConversionException;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.junit.jupiter.api.Assertions.assertThrows;

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

    @Test
    public void invalidNumberCollection() {
        assertThrows(ConversionException.class, () -> new PageNumbersListAdapter("1,3,a"));
    }

    @Test
    public void invalidNumber() {
        assertThrows(ConversionException.class, () -> new PageNumbersListAdapter("chuck"));
    }
}
