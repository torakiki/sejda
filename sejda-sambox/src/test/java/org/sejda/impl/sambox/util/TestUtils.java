package org.sejda.impl.sambox.util;

import org.sejda.impl.sambox.component.DefaultPdfSourceOpener;
import org.sejda.model.exception.TaskIOException;
import org.sejda.model.input.PdfStreamSource;
import org.sejda.sambox.pdmodel.PDDocument;

import static org.apache.commons.lang3.RandomStringUtils.randomAlphanumeric;

public class TestUtils {

    public static PDDocument getTestDoc(String name) throws TaskIOException {
        PdfStreamSource source = PdfStreamSource.newInstanceNoPassword(
                TestUtils.class.getClassLoader().getResourceAsStream(name), randomAlphanumeric(16) + ".pdf");

        return new DefaultPdfSourceOpener().open(source).getUnderlyingPDDocument();
    }
}
