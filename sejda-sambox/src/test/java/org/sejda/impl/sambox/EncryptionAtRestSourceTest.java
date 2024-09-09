package org.sejda.impl.sambox;

import org.junit.jupiter.api.Test;
import org.sejda.impl.sambox.component.DefaultPdfSourceOpener;
import org.sejda.impl.sambox.component.PDDocumentHandler;
import org.sejda.model.exception.TaskIOException;
import org.sejda.model.input.FileSource;
import org.sejda.model.input.PdfFileSource;
import org.sejda.model.input.PdfStreamSource;
import org.sejda.model.input.StreamSource;
import org.sejda.model.parameter.MergeParameters;
import org.sejda.model.task.Task;
import org.sejda.tests.tasks.BaseTaskTest;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.sejda.tests.TestUtils.*;

public class EncryptionAtRestSourceTest extends BaseTaskTest<MergeParameters> {

    @Test
    public void pdfFileSource() throws IOException, TaskIOException {
        PdfFileSource encrypted = encryptedAtRest(customInputAsFileSource("pdf/test-pdf.pdf"));
        PDDocumentHandler doc = new DefaultPdfSourceOpener().open(encrypted);
        assertEquals(doc.getNumberOfPages(), 11);
    }

    @Test
    public void pdfStreamSource() throws IOException, TaskIOException {
        PdfStreamSource encrypted = encryptedAtRest(customInput("pdf/test-pdf.pdf"));
        PDDocumentHandler doc = new DefaultPdfSourceOpener().open(encrypted);
        assertEquals(doc.getNumberOfPages(), 11);
    }

    @Test
    public void streamSource() throws IOException {
        StreamSource encrypted = encryptedAtRest(customNonPdfInputAsStreamSource("image/large.jpg"));

        BufferedImage image = ImageIO.read(encrypted.getSeekableSource().asNewInputStream());
        assertEquals(image.getWidth(), 5760);
    }

    @Test
    public void fileSource() throws IOException {
        FileSource encrypted = encryptedAtRest(customNonPdfInputAsFileSource("image/large.jpg"));

        BufferedImage image = ImageIO.read(encrypted.getSeekableSource().asNewInputStream());
        assertEquals(image.getWidth(), 5760);
    }

    @Override
    public Task<MergeParameters> getTask() {
        return null;
    }
}
