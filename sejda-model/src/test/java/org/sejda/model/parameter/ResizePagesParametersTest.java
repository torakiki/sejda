package org.sejda.model.parameter;

import org.junit.Test;
import org.sejda.TestUtils;
import org.sejda.model.scale.Margins;

public class ResizePagesParametersTest {

    @Test
    public void testValidParameters() {
        ResizePagesParameters victim = new ResizePagesParameters();
        victim.setMargins(new Margins(1, 1, 5.4, 3));
        victim.setPageSize(PageSize.A1);
        TestUtils.assertValidParameters(victim);
    }

    @Test
    public void testInvalidParameters() {
        ResizePagesParameters victim = new ResizePagesParameters();
        victim.setMargins(new Margins(-11, 1, 5.4, 3));
        victim.setPageSize(PageSize.A2);
        TestUtils.assertInvalidParameters(victim);
    }

}