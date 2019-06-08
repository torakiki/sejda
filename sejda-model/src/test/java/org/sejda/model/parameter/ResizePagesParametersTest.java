/*
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
package org.sejda.model.parameter;

import org.junit.Test;
import org.sejda.TestUtils;
import org.sejda.model.PageSize;
import org.sejda.model.scale.Margins;

public class ResizePagesParametersTest {

    @Test
    public void testValidParameters() {
        ResizePagesParameters victim = new ResizePagesParameters();
        victim.setMargins(new Margins(1, 1, 5.4, 3));
        victim.setPageSize(PageSize.A1);
        TestUtils.assertValidParameters(victim);
    }

    @Test
    public void testInvalidParameters() {
        ResizePagesParameters victim = new ResizePagesParameters();
        victim.setMargins(new Margins(-11, 1, 5.4, 3));
        victim.setPageSize(PageSize.A2);
        TestUtils.assertInvalidParameters(victim);
    }

}