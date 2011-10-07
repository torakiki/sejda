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

import org.sejda.core.DisplayNamedEnum;

/**
 * Types of compression for the TIFF format.
 * 
 * @author Andrea Vacondio
 * 
 */
public enum TiffCompressionType implements DisplayNamedEnum {
    NONE("none"),
    CCITT_GROUP_3_1D("ccitt_group_3_1d"),
    CCITT_GROUP_3_2D("ccitt_group_3_2d"),
    CCITT_GROUP_4("ccitt_group_4"),
    LZW("lzw"),
    JPEG_TTN2("jpeg_ttn2"),
    PACKBITS("packbits"),
    DEFLATE("deflate");

    private String displayName;

    private TiffCompressionType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
