package org.sejda.conversion;

import org.junit.jupiter.api.Test;
import org.sejda.model.exception.SejdaRuntimeException;
import org.sejda.model.scale.PageNormalizationPolicy;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/*
 * Created on 03/01/23
 * Copyright 2023 Sober Lemur S.r.l. and Sejda BV
 * This file is part of Sejda.
 *
 * Sejda is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Sejda is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with Sejda.  If not, see <http://www.gnu.org/licenses/>.
 */
class PageNormalizationPolicyAdapterTest {
    @Test
    public void testAdapt() {
        assertEquals(PageNormalizationPolicy.NONE, new PageNormalizationPolicyAdapter("none").getEnumValue());
        assertEquals(PageNormalizationPolicy.SAME_WIDTH,
                new PageNormalizationPolicyAdapter("same_width").getEnumValue());
        assertEquals(PageNormalizationPolicy.SAME_WIDTH_ORIENTATION_BASED,
                new PageNormalizationPolicyAdapter("same_width_orientation_based").getEnumValue());
    }

    @Test
    public void testInvalid() {
        assertThrows(SejdaRuntimeException.class, () -> new PageNormalizationPolicyAdapter("Chuck").getEnumValue());
    }
}