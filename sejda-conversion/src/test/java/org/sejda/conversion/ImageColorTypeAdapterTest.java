package org.sejda.conversion;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

import org.junit.Test;
import org.sejda.model.exception.SejdaRuntimeException;
import org.sejda.model.image.ImageColorType;

/**
 * Created on 6/16/12 3:11 PM
 *
 * @author: Edi Weissmann
 */
public class ImageColorTypeAdapterTest {

    @Test
    public void positives() {
        assertThat(new ImageColorTypeAdapter("black_and_white").getEnumValue(), is(ImageColorType.BLACK_AND_WHITE));
        assertThat(new ImageColorTypeAdapter("color_rgb").getEnumValue(), is(ImageColorType.COLOR_RGB));
        assertThat(new ImageColorTypeAdapter("gray_scale").getEnumValue(), is(ImageColorType.GRAY_SCALE));
    }

    @Test(expected = SejdaRuntimeException.class)
    public void negatives() {
        new ImageColorTypeAdapter("undefined").getEnumValue();
    }
}
