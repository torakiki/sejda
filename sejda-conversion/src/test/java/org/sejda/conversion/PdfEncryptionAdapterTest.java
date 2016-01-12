package org.sejda.conversion;

import org.junit.Test;

/**
 * Created on 6/2/12 12:17 PM
 *
 * @author: Edi Weissmann
 */
public class PdfEncryptionAdapterTest {

    @Test
    public void conversions() {
        new PdfEncryptionAdapter("rc4_40").getEnumValue();
        new PdfEncryptionAdapter("rc4_128").getEnumValue();
        new PdfEncryptionAdapter("aes_128").getEnumValue();
        new PdfEncryptionAdapter("aes_256").getEnumValue();
    }
}
