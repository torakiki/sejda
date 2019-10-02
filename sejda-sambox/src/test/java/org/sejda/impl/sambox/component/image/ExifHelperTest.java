package org.sejda.impl.sambox.component.image;

import org.junit.Test;
import org.sejda.model.input.Source;
import org.sejda.model.input.StreamSource;

import static org.junit.Assert.*;

public class ExifHelperTest {

    @Test
    public void getRotation() {
        Source source = StreamSource.newInstance(getClass().getResourceAsStream("/image/with_exif_orientation.JPG"), "with_exif_orientation.JPG");
        int rotation = ExifHelper.getRotationBasedOnExifOrientation(source);
        assertEquals(90, rotation);
    }
}