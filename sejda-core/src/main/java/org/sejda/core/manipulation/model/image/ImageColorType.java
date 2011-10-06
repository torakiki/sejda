/*
 * Created on 18/set/2011
 * Copyright 2011 by Andrea Vacondio (andrea.vacondio@gmail.com).
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
package org.sejda.core.manipulation.model.image;

import java.awt.image.BufferedImage;

import org.sejda.core.DisplayNamedEnum;

/**
 * The color type of an image.
 * 
 * @author Andrea Vacondio
 * 
 */
public enum ImageColorType implements DisplayNamedEnum {
    BLACK_AND_WHITE("black_and_white", BufferedImage.TYPE_BYTE_BINARY),
    GRAY_SCALE("gray_scale", BufferedImage.TYPE_BYTE_GRAY),
    COLOR_RGB("color_rgb", BufferedImage.TYPE_INT_RGB);

    private int bufferedImageType;
    private String displayName;

    private ImageColorType(String displayName, int bufferedImageType) {
        this.displayName = displayName;
        this.bufferedImageType = bufferedImageType;
    }

    public String getDisplayName() {
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
