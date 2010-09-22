/*
 * Created on 17/set/2010
 * Copyright (C) 2010 by Andrea Vacondio (andrea.vacondio@gmail.com).
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License.
 * 
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 */
package org.sejda.core.manipulation.model.parameter;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.sejda.core.manipulation.model.pdf.PdfAccessPermission;

/**
 * @author Andrea Vacondio
 *
 */
public class EncryptParametersTest {

    @Test
    public void testAdd() {
        EncryptParameters victim = new EncryptParameters();
        victim.addPermission(PdfAccessPermission.ANNOTATION);
        assertEquals(1, victim.getPermissions().size());
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testUnmodifiableList() {
        EncryptParameters victim = new EncryptParameters();
        victim.addPermission(PdfAccessPermission.ANNOTATION);
        victim.getPermissions().clear();
    }

    @Test
    public void testClear() {
        EncryptParameters victim = new EncryptParameters();
        victim.addPermission(PdfAccessPermission.ANNOTATION);
        assertEquals(1, victim.getPermissions().size());
        victim.clearPermissions();
        assertEquals(0, victim.getPermissions().size());
    }

    @Test
    public void testEqual() {
        EncryptParameters victim = new EncryptParameters();
        victim.addPermission(PdfAccessPermission.COPY);
        EncryptParameters other = new EncryptParameters();
        other.addPermission(PdfAccessPermission.COPY);
        assertTrue(victim.equals(other));
    }
}
