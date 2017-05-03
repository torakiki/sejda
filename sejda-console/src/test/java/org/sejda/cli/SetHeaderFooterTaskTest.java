/*
 * Created on 31/gen/2013
 * Copyright 2011 by Andrea Vacondio (andrea.vacondio@gmail.com).
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

import static org.hamcrest.core.IsCollectionContaining.hasItems;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

import java.awt.Color;

import org.junit.Test;
import org.sejda.cli.command.StandardTestableTask;
import org.sejda.model.HorizontalAlign;
import org.sejda.model.VerticalAlign;
import org.sejda.model.parameter.SetHeaderFooterParameters;
import org.sejda.model.pdf.StandardType1Font;
import org.sejda.model.pdf.page.PageRange;
import org.sejda.model.pdf.page.PredefinedSetOfPages;

/**
 * @author Andrea Vacondio
 * 
 */
public class SetHeaderFooterTaskTest extends AbstractTaskTest {

    public SetHeaderFooterTaskTest() {
        super(StandardTestableTask.SET_HEADER_FOOTER);
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
        defaultCommandLine().without("-l").assertConsoleOutputContains("Option is mandatory: --label -l");
    }

    @Test
    public void labelPattern() {
        SetHeaderFooterParameters parameters = defaultCommandLine().with("-l", "\"Page [PAGE_ROMAN]\"")
                .invokeSejdaConsole();
        assertEquals("Page [PAGE_ROMAN]", parameters.getPattern());
    }

    @Test
    public void fontSize() {
        SetHeaderFooterParameters parameters = defaultCommandLine().with("-d", "1.0").invokeSejdaConsole();
        assertEquals(1d, parameters.getFontSize(), 0.0);
    }

    @Test
    public void wrongFontSize() {
        defaultCommandLine().with("-d", "Chuck").assertConsoleOutputContains("Invalid value");
    }

    @Test
    public void batesStartFrom() {
        SetHeaderFooterParameters parameters = defaultCommandLine().with("--batesStartFrom", "123456")
                .invokeSejdaConsole();
        assertEquals("123456", parameters.getBatesSequence().next());
    }

    @Test
    public void batesIncrement() {
        SetHeaderFooterParameters parameters = defaultCommandLine().with("--batesIncrement", "5").invokeSejdaConsole();
        parameters.getBatesSequence().next();
        assertEquals("000006", parameters.getBatesSequence().next());
        assertEquals("000011", parameters.getBatesSequence().next());
    }

    @Test
    public void fontColor() {
        SetHeaderFooterParameters parameters = defaultCommandLine().with("--fontColor", "#FFFFFF").invokeSejdaConsole();
        assertEquals(new Color(255, 255, 255), parameters.getColor());
    }

    @Test
    public void pageCountStartFrom() {
        SetHeaderFooterParameters parameters = defaultCommandLine().with("--pageCountStartFrom", "5")
                .invokeSejdaConsole();
        assertEquals(5, parameters.getPageCountStartFrom().intValue());
    }

    @Test
    public void fileCountStartFrom() {
        SetHeaderFooterParameters parameters = defaultCommandLine().with("--fileCountStartFrom", "10")
                .invokeSejdaConsole();
        assertEquals(10, parameters.getFileCountStartFrom().intValue());
    }

    @Test
    public void testOutputPrefix_Specified() {
        SetHeaderFooterParameters parameters = defaultCommandLine().with("-p", "fooPrefix").invokeSejdaConsole();
        assertEquals("fooPrefix", parameters.getOutputPrefix());
    }

    @Test
    public void testOutputPrefix_Default() {
        SetHeaderFooterParameters parameters = defaultCommandLine().invokeSejdaConsole();
        assertEquals("", parameters.getOutputPrefix());
    }

    @Test
    public void defaults() {
        SetHeaderFooterParameters parameters = defaultCommandLine().invokeSejdaConsole();
        assertEquals(1, parameters.getFileCountStartFrom().intValue());
        assertEquals(PredefinedSetOfPages.ALL_PAGES, parameters.getPredefinedSetOfPages());
    }

    @Test
    public void predefinedSetOfPages_All() {
        SetHeaderFooterParameters parameters = defaultCommandLine().with("-s", "all")
                .invokeSejdaConsole();
        assertEquals(PredefinedSetOfPages.ALL_PAGES, parameters.getPredefinedSetOfPages());
    }

    @Test
    public void predefinedSetOfPages_Even() {
        SetHeaderFooterParameters parameters = defaultCommandLine().with("-s", "even")
                .invokeSejdaConsole();
        assertEquals(PredefinedSetOfPages.EVEN_PAGES, parameters.getPredefinedSetOfPages());
    }

    @Test
    public void predefinedSetOfPages_Odd() {
        SetHeaderFooterParameters parameters = defaultCommandLine().with("-s", "odd")
                .invokeSejdaConsole();
        assertEquals(PredefinedSetOfPages.ODD_PAGES, parameters.getPredefinedSetOfPages());
    }

    @Test
    public void pageRanges() {
        SetHeaderFooterParameters parameters = defaultCommandLine().with("-s", "1,2,8-10,19-")
                .invokeSejdaConsole();
        assertThat(parameters.getPageRanges(), hasItems(new PageRange(1, 1), new PageRange(8, 10), new PageRange(19)));
    }
}
