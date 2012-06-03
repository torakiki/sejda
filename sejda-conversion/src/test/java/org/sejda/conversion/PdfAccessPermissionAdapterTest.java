package org.sejda.conversion;

import org.junit.Test;

/**
 * Created on 6/2/12 12:24 PM
 *
 * @author: Edi Weissmann
 */
public class PdfAccessPermissionAdapterTest {

    @Test
    public void conversions() {
        new PdfAccessPermissionAdapter("print").getEnumValue();
        new PdfAccessPermissionAdapter("copy").getEnumValue();
        new PdfAccessPermissionAdapter("modify").getEnumValue();
        new PdfAccessPermissionAdapter("screenreaders").getEnumValue();
        new PdfAccessPermissionAdapter("fill").getEnumValue();
        new PdfAccessPermissionAdapter("assembly").getEnumValue();
        new PdfAccessPermissionAdapter("degradedprinting").getEnumValue();
    }
}
