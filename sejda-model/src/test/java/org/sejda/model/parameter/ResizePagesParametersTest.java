package org.sejda.model.parameter;

import org.junit.Test;
import org.sejda.TestUtils;

public class ResizePagesParametersTest {

    @Test
    public void testValidParameters() {
        ResizePagesParameters victim = new ResizePagesParameters();
        victim.setMargin(0.0);
        victim.setPageSizeWidth(0.0);
        TestUtils.assertValidParameters(victim);
    }

    @Test
    public void testInvalidParameters() {
        ResizePagesParameters victim = new ResizePagesParameters();
        victim.setMargin(-1.0);
        victim.setPageSizeWidth(-1.0);
        TestUtils.assertInvalidParameters(victim);
    }

}