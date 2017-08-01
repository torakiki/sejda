package org.sejda.model.parameter;

import org.junit.Test;
import org.sejda.TestUtils;

public class ResizePagesParametersTest {

    @Test
    public void testValidParameters() {
        ResizePagesParameters victim = new ResizePagesParameters();
        victim.setMargin(0.0);
        victim.setPageSize(PageSize.A1);
        TestUtils.assertValidParameters(victim);
    }

    @Test
    public void testInvalidParameters() {
        ResizePagesParameters victim = new ResizePagesParameters();
        victim.setMargin(-1.0);
        victim.setPageSize(PageSize.A2);
        TestUtils.assertInvalidParameters(victim);
    }

}