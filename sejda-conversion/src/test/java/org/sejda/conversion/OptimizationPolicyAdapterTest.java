/*
 * Created on 16 feb 2016
 * Copyright 2015 by Andrea Vacondio (andrea.vacondio@gmail.com).
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
package org.sejda.conversion;

import org.junit.jupiter.api.Test;
import org.sejda.model.exception.SejdaRuntimeException;
import org.sejda.model.optimization.OptimizationPolicy;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * @author Andrea Vacondio
 */
public class OptimizationPolicyAdapterTest {
    @Test
    public void testAdapt() {
        assertEquals(OptimizationPolicy.YES, new OptimizationPolicyAdapter("yes").getEnumValue());
        assertEquals(OptimizationPolicy.NO, new OptimizationPolicyAdapter("no").getEnumValue());
        assertEquals(OptimizationPolicy.AUTO, new OptimizationPolicyAdapter("auto").getEnumValue());
    }

    @Test
    public void testInvalid() {
        assertThrows(SejdaRuntimeException.class, () -> new OptimizationPolicyAdapter("Chuck").getEnumValue());
    }
}
