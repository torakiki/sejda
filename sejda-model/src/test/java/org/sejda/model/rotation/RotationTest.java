/*
 * Created on 18/set/2010
 *
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
package org.sejda.model.rotation;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

/**
 * Test unit for a rotation
 * 
 * @author Andrea Vacondio
 * 
 */
public class RotationTest {

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

    @Test
    public void addRotationTest() {
        assertEquals(Rotation.DEGREES_90, Rotation.DEGREES_270.addRotation(Rotation.DEGREES_180));
    }
}
