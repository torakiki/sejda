package org.sejda.impl.sambox.util;

import org.sejda.impl.sambox.component.DefaultPdfSourceOpener;
import org.sejda.model.exception.TaskIOException;
import org.sejda.model.input.PdfFileSource;
import org.sejda.model.input.PdfStreamSource;
import org.sejda.sambox.pdmodel.PDDocument;

import java.io.File;

import static org.apache.commons.lang3.RandomStringUtils.randomAlphanumeric;

public class TestUtils {

    public static PDDocument getTestDoc(String name) throws TaskIOException {
        File file = new File(name);
        if(file.exists()) {
            PdfFileSource source = PdfFileSource.newInstanceNoPassword(file);
            return new DefaultPdfSourceOpener().open(source).getUnderlyingPDDocument();
        }

        PdfStreamSource source = PdfStreamSource.newInstanceNoPassword(
                TestUtils.class.getClassLoader().getResourceAsStream(name), randomAlphanumeric(16) + ".pdf");

        return new DefaultPdfSourceOpener().open(source).getUnderlyingPDDocument();
    }
}
