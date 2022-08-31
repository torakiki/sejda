/*
 * Created on 18/set/2011
 * Copyright 2011 by Andrea Vacondio (andrea.vacondio@gmail.com).
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
package org.sejda.model.image;

import java.awt.image.BufferedImage;

import org.sejda.model.FriendlyNamed;

/**
 * The color type for an image.
 * 
 * @author Andrea Vacondio
 * 
 */
public enum ImageColorType implements FriendlyNamed {
    BLACK_AND_WHITE("black_and_white", BufferedImage.TYPE_BYTE_BINARY),
    GRAY_SCALE("gray_scale", BufferedImage.TYPE_BYTE_GRAY),
    COLOR_RGB("color_rgb", BufferedImage.TYPE_INT_RGB);

    private int bufferedImageType;
    private String displayName;

    private ImageColorType(String displayName, int bufferedImageType) {
        this.displayName = displayName;
        this.bufferedImageType = bufferedImageType;
    }

    @Override
    public String getFriendlyName() {
        return displayName;
    }

    /**
     * @return the corresponding {@link BufferedImage} color type.
     */
    public int getBufferedImageType() {
        return bufferedImageType;
    }

    /**
     * @param width
     * @param height
     * @return a {@link BufferedImage} for this color type with the given width and height.
     */
    public BufferedImage createBufferedImage(int width, int height) {
        return new BufferedImage(width, height, getBufferedImageType());
    }
}
