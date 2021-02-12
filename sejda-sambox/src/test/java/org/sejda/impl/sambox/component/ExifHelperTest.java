package org.sejda.impl.sambox.component;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.sejda.model.input.StreamSource;

public class ExifHelperTest {

    @Test
    public void getRotation() {
        StreamSource source = StreamSource.newInstance(
                getClass().getResourceAsStream("/image/with_exif_orientation.JPG"), "with_exif_orientation.JPG");
        int rotation = ExifHelper.getRotationBasedOnExifOrientation(source);
        assertEquals(90, rotation);
    }
}
