/*
 * Created on 29/dic/2012
 * Copyright 2011 Sober Lemur S.r.l. and Sejda BV.
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
import org.sejda.model.exception.SejdaRuntimeException;
import org.sejda.model.image.ImageColorType;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Created on 6/16/12 3:11 PM
 *
 * @author: Edi Weissmann
 */
public class ImageColorTypeAdapterTest {

    @Test
    public void positives() {
        assertThat(new ImageColorTypeAdapter("black_and_white").getEnumValue(), is(ImageColorType.BLACK_AND_WHITE));
        assertThat(new ImageColorTypeAdapter("color_rgb").getEnumValue(), is(ImageColorType.COLOR_RGB));
        assertThat(new ImageColorTypeAdapter("gray_scale").getEnumValue(), is(ImageColorType.GRAY_SCALE));
    }

    @Test
    public void negatives() {
        assertThrows(SejdaRuntimeException.class, () -> new ImageColorTypeAdapter("undefined").getEnumValue());
    }
}
