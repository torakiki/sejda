package org.sejda.impl.sambox.component;

import org.junit.jupiter.api.Test;
import org.sejda.model.input.StreamSource;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ExifHelperTest {

    @Test
    public void getRotation() {
        StreamSource source = StreamSource.newInstance(
                getClass().getClassLoader().getResourceAsStream("image/with_exif_orientation.JPG"),
                "with_exif_orientation.JPG");
        int rotation = ExifHelper.getRotationBasedOnExifOrientation(source);
        assertEquals(90, rotation);
    }
}
