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

import java.util.EnumSet;
import java.util.Set;

/**
 * Model for an image type with helper methods related to the ability of the image type to support multiple images written into the same file.
 *
 * @author Andrea Vacondio
 *
 */
public enum ImageType {
    PNG("image/png", "png", false),
    JPEG("image/jpeg", "jpg", false),
    GIF("image/gif", "gif", true),
    TIFF("image/tiff", "tif", true);

    private final String mimeType;
    private final String extension;
    private final boolean supportMultiImage;

    ImageType(String mimeType, String extension, boolean supportMultiImage) {
        this.mimeType = mimeType;
        this.extension = extension;
        this.supportMultiImage = supportMultiImage;
    }

    public String getMimeType() {
        return mimeType;
    }

    /**
     * @return true if the type supports multiple images into one image file.
     */
    public boolean isSupportMultiImage() {
        return supportMultiImage;
    }

    public String getExtension() {
        return extension;
    }

    /**
     * @return a set containing only those {@link ImageType} supporting multiple image.
     */
    public static Set<ImageType> valuesSupportingMultipleImage() {
        Set<ImageType> retSet = EnumSet.noneOf(ImageType.class);
        for (ImageType current : ImageType.values()) {
            if (current.isSupportMultiImage()) {
                retSet.add(current);
            }
        }
        return retSet;
    }
}
