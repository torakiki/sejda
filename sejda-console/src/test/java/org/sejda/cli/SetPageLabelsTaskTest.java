/*
 * Created on Sep 20, 2011
 * Copyright 2010 by Eduard Weissmann (edi.weissmann@gmail.com).
 * 
 * This file is part of the Sejda source code
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.sejda.cli;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.sejda.cli.command.StandardTestableTask;
import org.sejda.model.parameter.SetPagesLabelParameters;
import org.sejda.model.pdf.label.PdfLabelNumberingStyle;
import org.sejda.model.pdf.label.PdfPageLabel;

/**
 * Tests for SetPageLabels task cli
 * 
 * @author Eduard Weissmann
 * 
 */
public class SetPageLabelsTaskTest extends AbstractTaskTest {

    public SetPageLabelsTaskTest() {
        super(StandardTestableTask.SET_PAGE_LABELS);
    }

    @Test
    public void unrecognizedPageNumber() {
        defaultCommandLine().with("-l", "99unparseable:uroman:1:Chapter").assertConsoleOutputContains(
                "Could not parse input: '99unparseable:uroman:1:Chapter'. Unrecognized page number: '99unparseable'");
    }

    @Test
    public void unrecognizedNumberingStyle() {
        defaultCommandLine().with("-l", "99:klingon:1:Chapter").assertConsoleOutputContains(
                "Could not parse input: '99:klingon:1:Chapter'. Invalid value 'klingon' for numbering style");
    }

    @Test
    public void unrecognizedSuffixStartNumber() {
        defaultCommandLine().with("-l", "99:uroman:1abc:Chapter").assertConsoleOutputContains(
                "Could not parse input: '99:uroman:1abc:Chapter'. Unrecognized label suffix start number: '1abc'");
    }

    @Test
    public void optionalLabelPrefix() {
        SetPagesLabelParameters parameters = defaultCommandLine().with("-l", "99:uroman:1").invokeSejdaConsole();
        assertEquals("", parameters.getLabels().get(99).getLabelPrefix());
    }

    @Test
    public void multipleLabels() {
        SetPagesLabelParameters parameters = defaultCommandLine().with("-l", "\"98:arabic:6:Preface \" 99:uroman:1")
                .invokeSejdaConsole();
        assertEquals(2, parameters.getLabels().size());

        assertPageLabel(parameters, 98, "Preface ", PdfLabelNumberingStyle.ARABIC, 6);
        assertPageLabel(parameters, 99, "", PdfLabelNumberingStyle.UPPERCASE_ROMANS, 1);
    }

    @Test
    public void tooFewTokensInLabelInput() {
        defaultCommandLine().with("-l", "99:klingon").assertConsoleOutputContains(
                "Could not parse input: '99:klingon'. Format expected is: ");
    }

    @Test
    public void mandatoryParams() {
        defaultCommandLine().without("-l").assertConsoleOutputContains("Option is mandatory: --labels");
    }

    /**
     * @param parameters
     */
    private void assertPageLabel(SetPagesLabelParameters parameters, int pageNumber, String expectedLabelPrefix,
            PdfLabelNumberingStyle expectedNumberingStyle, int logicalPageNumber) {
        PdfPageLabel page98Label = parameters.getLabels().get(pageNumber);
        assertEquals(expectedLabelPrefix, page98Label.getLabelPrefix());
        assertEquals(expectedNumberingStyle, page98Label.getNumberingStyle());
        assertEquals(logicalPageNumber, page98Label.getLogicalPageNumber());
    }
}
