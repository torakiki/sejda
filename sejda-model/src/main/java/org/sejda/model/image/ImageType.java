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

    private String mimeType;
    private String extension;
    private boolean supportMultiImage;

    private ImageType(String mimeType, String extension, boolean supportMultiImage) {
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
