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

/**
 * Enum to model a page rotation.
 * 
 * @author Andrea Vacondio
 * 
 */
public enum Rotation {
    DEGREES_0(0), DEGREES_90(90), DEGREES_180(180), DEGREES_270(270);

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
