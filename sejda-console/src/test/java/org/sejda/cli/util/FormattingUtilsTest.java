/*
 * Created on Oct 3, 2011
 * Copyright 2010 by Eduard Weissmann (edi.weissmann@gmail.com).
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
package org.sejda.cli.util;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import org.junit.Test;

/**
 * Tests for {@link FormattingUtils}
 * 
 * @author Eduard Weissmann
 * 
 */
public class FormattingUtilsTest {

    @Test
    public void justifyLeft() {
        assertThat(FormattingUtils.justifyLeft(9, "this is line two"), is("this is\nline two"));
    }

    @Test
    public void leftPadMultiline() {
        assertThat(FormattingUtils.leftPadMultiline("this is\nline two", ' ', 3), is("this is\n   line two"));
    }

    @Test
    public void repeatedChar() {
        assertThat(FormattingUtils.repeatedChar(' ', 3), is("   "));
    }
}
