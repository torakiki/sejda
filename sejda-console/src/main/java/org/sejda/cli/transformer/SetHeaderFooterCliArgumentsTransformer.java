/*
 * Created on 30/dic/2012
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
package org.sejda.cli.transformer;

import java.awt.Color;
import java.util.Set;

import org.sejda.cli.model.SetHeaderFooterTaskCliArguments;
import org.sejda.model.exception.SejdaRuntimeException;
import org.sejda.model.parameter.SetHeaderFooterParameters;
import org.sejda.model.pdf.numbering.BatesSequence;
import org.sejda.model.pdf.page.PageRange;
import org.sejda.model.pdf.page.PredefinedSetOfPages;

/**
 * {@link CommandCliArgumentsTransformer} for the SetHeaderFooter task command line interface
 * 
 * @author Andrea Vacondio
 * 
 */
public class SetHeaderFooterCliArgumentsTransformer extends BaseCliArgumentsTransformer implements
        CommandCliArgumentsTransformer<SetHeaderFooterTaskCliArguments, SetHeaderFooterParameters> {

    @Override
    public SetHeaderFooterParameters toTaskParameters(SetHeaderFooterTaskCliArguments taskCliArguments) {
        if (taskCliArguments.getLabel() == null) {
            throw new SejdaRuntimeException("Please specify the text label to apply");
        }
        SetHeaderFooterParameters parameters = new SetHeaderFooterParameters();

        PredefinedSetOfPages predefinedSetOfPages = taskCliArguments.getPageRange().getPredefinedSetOfPages();
        if(predefinedSetOfPages != null) {
            parameters.setPredefinedSetOfPages(predefinedSetOfPages);
        } else {
            Set<PageRange> pageRanges = taskCliArguments.getPageRange().getPageRanges();
            parameters.addAllPageRanges(pageRanges);
        }

        parameters.setPattern(taskCliArguments.getLabel());
        populateAlignment(taskCliArguments, parameters);
        populateFont(taskCliArguments, parameters);
        populateSourceParameters(parameters, taskCliArguments);
        populateOutputTaskParameters(parameters, taskCliArguments);
        populateAbstractParameters(parameters, taskCliArguments);
        populateOutputPrefix(parameters, taskCliArguments);

        Long startFrom = 1L;
        Integer increment = 1;
        if(taskCliArguments.isBatesIncrement()) {
            increment = taskCliArguments.getBatesIncrement();
        }
        if(taskCliArguments.isBatesStartFrom()){
            startFrom = taskCliArguments.getBatesStartFrom();
        }

        parameters.setBatesSequence(new BatesSequence(startFrom, increment, 6));

        if(taskCliArguments.isPageCountStartFrom()) {
            parameters.setPageCountStartFrom(taskCliArguments.getPageCountStartFrom());
        }

        if(taskCliArguments.isFontColor()) {
            String color = taskCliArguments.getFontColor();
            if (!color.startsWith("#")) {
                color = "#" + color;
            }
            parameters.setColor(hex2Rgb(color));
        }

        if(taskCliArguments.isFileCountStartFrom()) {
            parameters.setFileCountStartFrom(taskCliArguments.getFileCountStartFrom());
        }

        return parameters;
    }

    private void populateAlignment(SetHeaderFooterTaskCliArguments taskCliArguments,
            SetHeaderFooterParameters parameters) {
        if (taskCliArguments.isHorizontalAlign()) {
            parameters.setHorizontalAlign(taskCliArguments.getHorizontalAlign().getEnumValue());
        }
        if (taskCliArguments.isVerticalAlign()) {
            parameters.setVerticalAlign(taskCliArguments.getVerticalAlign().getEnumValue());
        }
    }

    private void populateFont(SetHeaderFooterTaskCliArguments taskCliArguments, SetHeaderFooterParameters parameters) {
        if (taskCliArguments.isFontSize()) {
            parameters.setFontSize(taskCliArguments.getFontSize());
        }
        if (taskCliArguments.isFont()) {
            parameters.setFont(taskCliArguments.getFont().getEnumValue());
        }
    }

    private Color hex2Rgb(String s) {
        return new Color(
                Integer.valueOf(s.substring(1, 3), 16),
                Integer.valueOf(s.substring(3, 5), 16),
                Integer.valueOf(s.substring(5, 7), 16));
    }

}
