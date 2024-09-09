package org.sejda.impl.sambox.component.image;

import org.junit.jupiter.api.Test;
import org.sejda.impl.sambox.component.PDDocumentHandler;
import org.sejda.tests.PixelCompareUtils;
import org.sejda.model.exception.TaskException;
import org.sejda.model.input.FileSource;

import static org.sejda.tests.TestUtils.customNonPdfInputAsFileSource;

public class ImagesToPdfDocumentConverterTest {

    @Test
    public void imageWithExifRotation() throws TaskException {
        FileSource source = customNonPdfInputAsFileSource("image/with_exif_orientation.JPG");
        ImagesToPdfDocumentConverter converter = new ImagesToPdfDocumentConverter();
        converter.addPages(source);
        PDDocumentHandler d = converter.getDocumentHandler();
        new PixelCompareUtils().assertSimilar(d.getUnderlyingPDDocument(), "pdf/with_exif_orientation.pdf");
    }
}
