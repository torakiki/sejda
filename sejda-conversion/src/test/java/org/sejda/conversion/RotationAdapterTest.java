package org.sejda.conversion;

import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.sejda.model.rotation.Rotation.*;

/**
 * Created on 6/7/12 10:02 PM
 *
 * @author: Edi Weissmann
 */
public class RotationAdapterTest {

    @Test
    public void testGetPageRotation() {

        assertThat(new RotationAdapter("90").getEnumValue(), is(DEGREES_90));
        assertThat(new RotationAdapter("180").getEnumValue(), is(DEGREES_180));
        assertThat(new RotationAdapter("270").getEnumValue(), is(DEGREES_270));
    }
}
