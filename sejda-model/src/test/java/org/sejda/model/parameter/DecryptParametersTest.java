/*
 * Created on 03/ago/2011
 * Copyright 2010 by Andrea Vacondio (andrea.vacondio@gmail.com).
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
package org.sejda.model.parameter;

import org.junit.Test;
import org.sejda.TestUtils;

/**
 * @author Andrea Vacondio
 * 
 */
public class DecryptParametersTest {

    @Test
    public void testEquals() {
        DecryptParameters eq1 = new DecryptParameters();
        DecryptParameters eq2 = new DecryptParameters();
        DecryptParameters eq3 = new DecryptParameters();
        DecryptParameters diff = new DecryptParameters();
        diff.setOutputPrefix("prefix");
        TestUtils.testEqualsAndHashCodes(eq1, eq2, eq3, diff);
    }
}
