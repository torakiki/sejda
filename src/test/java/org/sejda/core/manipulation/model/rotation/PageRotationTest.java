/*
 * Created on 29/mag/2010
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
package org.sejda.core.manipulation.model.rotation;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

/**
 * Test unit for the page rotation
 * 
 * @author Andrea Vacondio
 * 
 */
public class PageRotationTest {

    private static final int D_90 = 90;
    private static final int D_540 = 540;
    private static final int D_95 = 95;

    @Test
    public void getRotationTest() {
        assertEquals(Rotation.DEGREES_90, Rotation.getRotation(D_90));
        assertEquals(Rotation.DEGREES_180, Rotation.getRotation(D_540));
        assertEquals(Rotation.DEGREES_0, Rotation.getRotation(D_95));
    }

    @Test
    public void rotateTest() {
        assertEquals(Rotation.DEGREES_180, Rotation.DEGREES_90.rotateClockwise());
        assertEquals(Rotation.DEGREES_180, Rotation.DEGREES_270.rotateAnticlockwise());
        assertEquals(Rotation.DEGREES_270, Rotation.DEGREES_0.rotateAnticlockwise());
    }

}
