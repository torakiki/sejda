/*
 * Created on 31/gen/2013
 * Copyright 2011 by Andrea Vacondio (andrea.vacondio@gmail.com).
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); 
 * you may not use this file except in compliance with the License. 
 * You may obtain a copy of the License at 
 * 
 * http://www.apache.org/licenses/LICENSE-2.0 
 * 
 * Unless required by applicable law or agreed to in writing, software 
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. 
 * See the License for the specific language governing permissions and 
 * limitations under the License. 
 */
package org.sejda.cli;

import static org.junit.Assert.assertEquals;

import java.math.BigDecimal;

import org.junit.Test;
import org.sejda.model.HorizontalAlign;
import org.sejda.model.VerticalAlign;
import org.sejda.model.parameter.SetHeaderFooterParameters;
import org.sejda.model.pdf.StandardType1Font;
import org.sejda.model.pdf.headerfooter.NumberingStyle;

/**
 * @author Andrea Vacondio
 * 
 */
public class SetHeaderFooterTaskTest extends AbstractTaskTest {

    public SetHeaderFooterTaskTest() {
        super(TestableTask.SET_HEADER_FOOTER);
    }

    @Test
    public void testRightHorAlign() {
        SetHeaderFooterParameters parameters = defaultCommandLine().with("-x", "right").invokeSejdaConsole();
        assertEquals(HorizontalAlign.RIGHT, parameters.getHorizontalAlign());
    }

    @Test
    public void testLeftHorAlign() {
        SetHeaderFooterParameters parameters = defaultCommandLine().with("-x", "left").invokeSejdaConsole();
        assertEquals(HorizontalAlign.LEFT, parameters.getHorizontalAlign());
    }

    @Test
    public void testCenterHorAlign() {
        SetHeaderFooterParameters parameters = defaultCommandLine().with("-x", "center").invokeSejdaConsole();
        assertEquals(HorizontalAlign.CENTER, parameters.getHorizontalAlign());
    }

    @Test
    public void testDefaultHorAlign() {
        SetHeaderFooterParameters parameters = defaultCommandLine().invokeSejdaConsole();
        assertEquals(HorizontalAlign.CENTER, parameters.getHorizontalAlign());
    }

    @Test
    public void testTopVertAlign() {
        SetHeaderFooterParameters parameters = defaultCommandLine().with("-y", "top").invokeSejdaConsole();
        assertEquals(VerticalAlign.TOP, parameters.getVerticalAlign());
    }

    @Test
    public void testBottomVertAlign() {
        SetHeaderFooterParameters parameters = defaultCommandLine().with("-y", "bottom").invokeSejdaConsole();
        assertEquals(VerticalAlign.BOTTOM, parameters.getVerticalAlign());
    }

    @Test
    public void testDefaultVertAlign() {
        SetHeaderFooterParameters parameters = defaultCommandLine().invokeSejdaConsole();
        assertEquals(VerticalAlign.BOTTOM, parameters.getVerticalAlign());
    }

    @Test
    public void testKnownFont() {
        SetHeaderFooterParameters parameters = defaultCommandLine().with("-t", "Helvetica-Bold").invokeSejdaConsole();
        assertEquals(StandardType1Font.HELVETICA_BOLD, parameters.getFont());
    }

    @Test
    public void testUnknownFont() {
        defaultCommandLine().with("-t", "Chuck-Norris").assertConsoleOutputContains("Invalid value");
    }

    @Test
    public void testMandatory() {
        defaultCommandLine().without("-l").assertConsoleOutputContains("No header or footer definition");
    }

    @Test
    public void unrecognizedNumberingStyle() {
        defaultCommandLine().with("-n", "Chuck:Norris").assertConsoleOutputContains(
                "Could not parse input: 'Chuck:Norris'. Unrecognized page number: 'Chuck'");
    }

    @Test
    public void numberingStyle() {
        SetHeaderFooterParameters parameters = defaultCommandLine().with("-n", "99:arabic").invokeSejdaConsole();
        assertEquals(NumberingStyle.ARABIC, parameters.getNumbering().getNumberingStyle());
        assertEquals(99, parameters.getNumbering().getLogicalPageNumber());
    }

    @Test
    public void fontSize() {
        SetHeaderFooterParameters parameters = defaultCommandLine().with("-d", "1.0").invokeSejdaConsole();
        assertEquals(new BigDecimal("1.0"), parameters.getFontSize());
    }

    @Test
    public void wrongFontSize() {
        defaultCommandLine().with("-d", "Chuck").assertConsoleOutputContains("Invalid value");
    }
}
