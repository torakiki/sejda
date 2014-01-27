package org.sejda.conversion;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

import org.junit.Test;
import org.sejda.conversion.exception.ConversionException;
import org.sejda.model.pdf.label.PdfLabelNumberingStyle;

/**
 * Created on 6/16/12 3:17 PM
 * 
 * @author: Edi Weissmann
 */
public class PdfPageLabelAdapterTest {

    @Test
    public void positives() {
        assertThat(new PdfPageLabelAdapter("22:arabic:1").getPageNumber(), is(22));
        assertThat(new PdfPageLabelAdapter("1:arabic:1").getPdfPageLabel().getNumberingStyle(),
                is(PdfLabelNumberingStyle.ARABIC));
        assertThat(new PdfPageLabelAdapter("1:empty:1").getPdfPageLabel().getNumberingStyle(),
                is(PdfLabelNumberingStyle.EMPTY));
        assertThat(new PdfPageLabelAdapter("1:lletter:1").getPdfPageLabel().getNumberingStyle(),
                is(PdfLabelNumberingStyle.LOWERCASE_LETTERS));
        assertThat(new PdfPageLabelAdapter("22:arabic:1:label").getPdfPageLabel().getLabelPrefix(), is("label"));
    }

    @Test(expected = ConversionException.class)
    public void negatives() {
        new PdfPageLabelAdapter("22:arabic");
    }
}
