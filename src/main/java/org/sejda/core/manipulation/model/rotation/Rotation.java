/*
 * Created on 29/mag/2010
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
package org.sejda.core.manipulation.model.rotation;

/**
 * Enum to model a page rotation.
 * 
 * @author Andrea Vacondio
 * 
 */
public enum Rotation {
    DEGREES_0(0),
    DEGREES_90(90),
    DEGREES_180(180),
    DEGREES_270(270);

    private static final int D_360 = 360;
    private static final int D_90 = 90;
    private static final int D_270 = 270;

    private int degrees;

    Rotation(int degrees) {
        this.degrees = degrees;
    }

    /**
     * @return the degrees
     */
    public int getDegrees() {
        return degrees;
    }

    /**
     * Only multiple of 90 degrees are recognized.
     * 
     * @param degrees
     *            rotation degrees
     * @return corresponding rotation. If not a multiple of 90 degrees, a zero degrees rotation is returned.
     */
    public static Rotation getRotation(int degrees) {
        int actualRotation = (degrees % D_360);
        for (Rotation rotation : Rotation.values()) {
            if (rotation.getDegrees() == actualRotation) {
                return rotation;
            }
        }
        return DEGREES_0;
    }

    /**
     * @return a clockwise rotation
     */
    public Rotation rotateClockwise() {
        return getRotation((degrees + D_90) % D_360);
    }

    /**
     * @return an anti clockwise rotation
     */
    public Rotation rotateAnticlockwise() {
        return getRotation((degrees + D_270) % D_360);
    }
}
