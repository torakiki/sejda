/*
 * Created on 29/mag/2010
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

import org.sejda.model.FriendlyNamed;

/**
 * Enum to model a page rotation.
 * 
 * @author Andrea Vacondio
 * 
 */
public enum Rotation implements FriendlyNamed {
    DEGREES_0(0),
    DEGREES_90(90),
    DEGREES_180(180),
    DEGREES_270(270);

    private static final int D_360 = 360;
    private static final int D_90 = 90;
    private static final int D_270 = 270;

    private int degrees;
    private String displayName;

    Rotation(int degrees) {
        this.displayName = String.valueOf(degrees);
        this.degrees = degrees;
    }

    @Override
    public String getFriendlyName() {
        return displayName;
    }

    /**
     * @return the degrees
     */
    public int getDegrees() {
        return degrees;
    }

    /**
     * @param degrees
     *            rotation degrees
     * @return the rotation corresponding to the input degrees module 360. Only multiple of 90 degrees are recognized as valid rotations otherwise a zero degrees rotation is
     *         returned.
     */
    public static Rotation getRotation(int degrees) {
        int actualRotation = degrees % D_360;
        for (Rotation rotation : Rotation.values()) {
            if (rotation.getDegrees() == actualRotation) {
                return rotation;
            }
        }
        return DEGREES_0;
    }

    /**
     * 
     * @param rotation
     * @return the rotation obtained adding the input rotation to the current rotation.
     */
    public Rotation addRotation(Rotation rotation) {
        return getRotation(getDegrees() + rotation.getDegrees());
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
