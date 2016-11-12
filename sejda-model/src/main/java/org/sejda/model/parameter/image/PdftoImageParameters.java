package org.sejda.model.parameter.image;

import org.sejda.model.image.ImageColorType;
import org.sejda.model.parameter.base.TaskParameters;

public interface PdfToImageParameters extends TaskParameters {

    ImageColorType getOutputImageColorType();

    void setOutputImageColorType(ImageColorType outputImageColorType);

    float getUserZoom();

    void setUserZoom(float userZoom);

    int getResolutionInDpi();

    void setResolutionInDpi(int resolutionInDpi);
}
