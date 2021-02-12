package org.sejda.impl.sambox.component.image;

import org.junit.Test;
import org.sejda.core.service.BaseTaskTest;
import org.sejda.impl.sambox.component.PDDocumentHandler;
import org.sejda.impl.sambox.util.PixelCompareUtils;
import org.sejda.model.exception.TaskException;
import org.sejda.model.input.FileSource;

public class ImagesToPdfDocumentConverterTest {

    @Test
    public void imageWithExifRotation() throws TaskException {
        FileSource source = BaseTaskTest.customNonPdfInputAsFileSource("image/with_exif_orientation.JPG");
        ImagesToPdfDocumentConverter converter = new ImagesToPdfDocumentConverter();
        PDDocumentHandler d = converter.addPage(source);
        PixelCompareUtils.assertSimilar(d, "pdf/with_exif_orientation.pdf");
    }
}
