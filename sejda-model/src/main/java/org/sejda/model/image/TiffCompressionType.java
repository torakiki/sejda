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

import org.sejda.model.FriendlyNamed;

/**
 * Types of compression for the TIFF format.
 * 
 * @author Andrea Vacondio
 * 
 */
public enum TiffCompressionType implements FriendlyNamed {
    NONE("none"),   
    CCITT_GROUP_3_1D("ccitt_group_3_1d"),
    CCITT_GROUP_3_2D("ccitt_group_3_2d"),
    CCITT_GROUP_4("ccitt_group_4"),
    LZW("lzw"),
    ZLIB("zlib"),
    JPEG_TTN2("jpeg_ttn2"),
    PACKBITS("packbits"),
    DEFLATE("deflate");

    private String displayName;

    private TiffCompressionType(String displayName) {
        this.displayName = displayName;
    }

    @Override
    public String getFriendlyName() {
        return displayName;
    }
}

