/*
 * Copyright 2016 by Edi Weissmann (edi.weissmann@gmail.com).
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
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.sejda.impl.sambox.util;

import org.junit.Test;
import org.sejda.sambox.pdmodel.common.PDRectangle;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;

public class RectangleUtilsTest {

    @Test
    public void translateRectangle() {
        PDRectangle in = new PDRectangle(0, 0, 396, 612);

        assertThat(RectangleUtils.translate(100, 50, in), is(new PDRectangle(100, 50, 396, 612)));
    }

    @Test
    public void rotateRectangle() {
        PDRectangle in = new PDRectangle(0, 0, 396, 612);
        PDRectangle mediaBox = new PDRectangle(0, 0, 792, 612);

        assertThat(RectangleUtils.rotate(-90, in, mediaBox), is(new PDRectangle(0, 0, 612, 396)));
        assertThat(RectangleUtils.rotate(270, in, mediaBox), is(RectangleUtils.rotate(-90, in, mediaBox)));

        assertThat(RectangleUtils.rotate(90, in, mediaBox), is(new PDRectangle(0, 396, 612, 396)));

        assertThat(RectangleUtils.rotate(180, in, mediaBox), is(new PDRectangle(396, 0, 396, 612)));
    }
}