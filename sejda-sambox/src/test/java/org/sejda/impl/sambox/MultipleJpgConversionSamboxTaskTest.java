package org.sejda.impl.sambox;

import org.sejda.core.service.MultipleJpegConversionTaskTest;
import org.sejda.model.parameter.image.PdfToJpegParameters;
import org.sejda.model.task.Task;

public class MultipleJpgConversionSamboxTaskTest extends MultipleJpegConversionTaskTest {

    public Task<PdfToJpegParameters> getTask() {
        return new PdfToMultipleImageTask();
    }
}
