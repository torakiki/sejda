package org.sejda.core.manipulation.model.task.itext.component.input;

import org.sejda.core.manipulation.model.input.PdfSourceOpener;

import com.lowagie.text.pdf.PdfReader;

/**
 * This class contains only static factory methods to create {@link PdfSourceOpener} implementations.
 * 
 * @author Andrea Vacondio
 * 
 */
public final class PdfSourceOpeners {

    private PdfSourceOpeners() {
        // hide
    }

    /**
     * Factory method returning a {@link PdfSourceOpener} that performs a full read on the opened source.
     * 
     * @return the newly created {@link PdfSourceOpener}
     */
    public static PdfSourceOpener<PdfReader> newFullReadOpener() {
        return new FullReadPdfSourceOpener();
    }

    /**
     * Factory method returning a {@link PdfSourceOpener} that performs a partial read on the opened source.
     * 
     * @return the newly created {@link PdfSourceOpener}
     */
    public static PdfSourceOpener<PdfReader> newPartialReadOpener() {
        return new PartialReadPdfSourceOpener();
    }

}
