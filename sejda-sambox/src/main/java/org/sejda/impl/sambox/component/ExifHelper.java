/*
 * Copyright 2017 by Eduard Weissmann (edi.weissmann@gmail.com).
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
package org.sejda.impl.sambox.component;

import java.io.InputStream;

import org.sejda.model.input.Source;

import com.drew.imaging.ImageMetadataReader;
import com.drew.metadata.Directory;
import com.drew.metadata.Metadata;
import com.drew.metadata.MetadataException;
import com.drew.metadata.exif.ExifIFD0Directory;

/**
 * Reads exif orientation of an image and determines if the image should be rotated or not.
 * <p>
 * Based on https://stackoverflow.com/questions/5905868/how-to-rotate-jpeg-images-based-on-the-orientation-metadata
 */
public class ExifHelper {

    public static int getRotationBasedOnExifOrientation(InputStream inputStream) {
        try {
            int orientation = readExifOrientation(ImageMetadataReader.readMetadata(inputStream));
            return getRotation(orientation);
        } catch (Throwable e) {
            return 0;
        }
    }

    public static int getRotationBasedOnExifOrientation(Source<?> source) {
        try {
            return getRotationBasedOnExifOrientation(source.getSeekableSource().asNewInputStream());
        } catch (Throwable e) {
            return 0;
        }
    }

    private static int readExifOrientation(Metadata metadata) throws MetadataException {
        Directory directory = metadata.getFirstDirectoryOfType(ExifIFD0Directory.class);
        return directory.getInt(ExifIFD0Directory.TAG_ORIENTATION);
    }

    /**
     * Returns a rotation that corresponds to the given exif orientation.
     * If the exif orientation involves a flip, thus not being achievable only with a rotation, then 0 is returned (unsupported)
     * <p>
     * See http://sylvana.net/jpegcrop/exif_orientation.html
     */
    private static int getRotation(int orientation) {
        switch (orientation) {
            case 1:
                return 0;
            case 2: // Flip X
                // unsupported
                return 0;
            case 3: // PI rotation
                return 180;
            case 4: // Flip Y
                // unsupported
                return 0;
            case 5: // - PI/2 and Flip X
                // unsupported
                return 0;
            case 6: // -PI/2 and -width
                return 90;
            case 7: // PI/2 and Flip
                // unsupported
                return 0;
            case 8: // PI / 2
                return 270;
        }

        return 0;
    }
}
