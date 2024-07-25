/*
 * Copyright 2010 Sober Lemur S.r.l. and Sejda BV
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
package org.sejda.core.support.util;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Locale;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public class HumanReadableSizeTest {

    @BeforeEach
    public void setUp() {
        Locale.setDefault(Locale.UK);
    }

    @Test
    public void bytes() {
        assertThat(HumanReadableSize.toString(123L), is("123 bytes"));
        assertThat(HumanReadableSize.toString(123L, true), is("123 bytes"));
    }

    @Test
    public void kilo() {
        assertThat(HumanReadableSize.toString(12345L), is("12.35 KB"));
        assertThat(HumanReadableSize.toString(12345L, true), is("12 KB"));
    }

    @Test
    public void mega() {
        assertThat(HumanReadableSize.toString(123456789L), is("123.46 MB"));
        assertThat(HumanReadableSize.toString(123456789L, true), is("123 MB"));
    }
}
