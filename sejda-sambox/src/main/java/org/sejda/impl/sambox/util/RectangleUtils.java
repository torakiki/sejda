/*
 * Copyright 2016 by Edi Weissmann (edi.weissmann@gmail.com).
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
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.sejda.impl.sambox.util;

import org.sejda.sambox.pdmodel.common.PDRectangle;

public class RectangleUtils {

    public static boolean intersect(PDRectangle r1, PDRectangle r2) {
        return r1.contains(r2.getLowerLeftX(), r2.getLowerLeftY()) ||
                r1.contains(r2.getUpperRightX(), r2.getUpperRightY()) ||
                r1.contains(r2.getLowerLeftX(), r2.getUpperRightY()) ||
                r1.contains(r2.getUpperRightX(), r2.getLowerLeftY());
    }

    public static PDRectangle translate(float xOffset, float yOffset, PDRectangle oldRectangle) {
        return new PDRectangle(oldRectangle.getLowerLeftX() + xOffset,
                oldRectangle.getLowerLeftY() + yOffset, oldRectangle.getWidth(), oldRectangle.getHeight());
    }

    public static PDRectangle rotate(int degrees, PDRectangle oldRectangle, PDRectangle mediaBox) {
        while(degrees < 0) {
            degrees = 360 + degrees;
        }

        switch (degrees) {
            case 0:
                return oldRectangle;
            case 90:
                return new PDRectangle(oldRectangle.getLowerLeftY(),
                        mediaBox.getWidth() - oldRectangle.getLowerLeftX() - oldRectangle.getWidth(),
                        oldRectangle.getHeight(), oldRectangle.getWidth()
                );
            case 180:
                return new PDRectangle(mediaBox.getWidth() - oldRectangle.getLowerLeftX() - oldRectangle.getWidth(),
                        mediaBox.getHeight() - oldRectangle.getLowerLeftY() - oldRectangle.getHeight(),
                        oldRectangle.getWidth(), oldRectangle.getHeight()
                );
            case 270:
                return new PDRectangle(mediaBox.getHeight() - oldRectangle.getLowerLeftY() - oldRectangle.getHeight(),
                        oldRectangle.getLowerLeftX(),
                        oldRectangle.getHeight(), oldRectangle.getWidth()
                );
            default:
                throw new RuntimeException("Cannot rotate rectangle by degrees: " + degrees);
        }
    }
}
