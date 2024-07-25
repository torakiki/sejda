/*
 * Created on 27/gen/2014
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
import org.sejda.model.RectangularBox;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * @author Andrea Vacondio
 */
public class RectangularBoxAdapterTest {
    @Test
    public void testPositive() {
        assertThat(new RectangularBoxAdapter("[2:3][10:20]").getRectangularBox(),
                is(RectangularBox.newInstance(2, 3, 10, 20)));
    }

    @Test
    public void missingPoint() {
        assertThrows(ConversionException.class, () -> new RectangularBoxAdapter("[2:3][10:]").getRectangularBox());
    }

    @Test
    public void missingPointAgain() {
        assertThrows(ConversionException.class, () -> new RectangularBoxAdapter("[2:3][10]").getRectangularBox());
    }
}
