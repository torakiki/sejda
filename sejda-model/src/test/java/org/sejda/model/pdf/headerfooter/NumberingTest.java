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
package org.sejda.model.pdf.headerfooter;

import org.junit.jupiter.api.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * @author Andrea Vacondio
 */
public class NumberingTest {
    @Test
    public void testFormatForLabelArabic() {
        Numbering victim = new Numbering(NumberingStyle.ARABIC, 100);
        assertThat(victim.styledLabelFor(110), is("110"));
    }

    @Test
    public void testFormatForLabelRoman() {
        Numbering victim = new Numbering(NumberingStyle.ROMAN, 100);
        assertThat(victim.styledLabelFor(110), is("CX"));
    }

    @Test
    public void testRequiredStyle() {
        assertThrows(IllegalArgumentException.class, () -> new Numbering(null, 100));
    }
}
