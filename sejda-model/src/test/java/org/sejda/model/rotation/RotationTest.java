/*
 * Created on 18/set/2010
 *
 * Copyright 2010 by Andrea Vacondio (andrea.vacondio@gmail.com).
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); 
 * you may not use this file except in compliance with the License. 
 * You may obtain a copy of the License at 
 * 
 * http://www.apache.org/licenses/LICENSE-2.0 
 * 
 * Unless required by applicable law or agreed to in writing, software 
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. 
 * See the License for the specific language governing permissions and 
 * limitations under the License. 
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
