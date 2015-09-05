/*
 * Created on Jul 1, 2011
 * Copyright 2011 by Eduard Weissmann (edi.weissmann@gmail.com).
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

import org.sejda.cli.model.ViewerPreferencesTaskCliArguments;
import org.sejda.model.parameter.ViewerPreferencesParameters;
import org.sejda.model.pdf.viewerpreference.PdfBooleanPreference;

/**
 * {@link CommandCliArgumentsTransformer} for the ViewerPreferences task command line interface
 * 
 * @author Eduard Weissmann
 * 
 */
public class ViewerPreferencesCliArgumentsTransformer extends BaseCliArgumentsTransformer implements
        CommandCliArgumentsTransformer<ViewerPreferencesTaskCliArguments, ViewerPreferencesParameters> {

    /**
     * Transforms {@link ViewerPreferencesTaskCliArguments} to {@link ViewerPreferencesParameters}
     * 
     * @param taskCliArguments
     * @return populated task parameters
     */
    @Override
    public ViewerPreferencesParameters toTaskParameters(ViewerPreferencesTaskCliArguments taskCliArguments) {
        ViewerPreferencesParameters parameters = new ViewerPreferencesParameters();
        populateAbstractParameters(parameters, taskCliArguments);
        populateOutputTaskParameters(parameters, taskCliArguments);
        populateSourceParameters(parameters, taskCliArguments);

        populateActivePreferences(taskCliArguments, parameters);
        populateOutputPrefix(parameters, taskCliArguments);

        parameters.setNfsMode(taskCliArguments.getNfsMode().getEnumValue());

        parameters.setPageLayout(taskCliArguments.getLayout().getEnumValue());
        parameters.setPageMode(taskCliArguments.getMode().getEnumValue());
        if (taskCliArguments.isPrintScaling()) {
            parameters.setPrintScaling(taskCliArguments.getPrintScaling().getEnumValue());
        }
        if (taskCliArguments.isDirection()) {
            parameters.setDirection(taskCliArguments.getDirection().getEnumValue());
        }
        if (taskCliArguments.isDuplex()) {
            parameters.setDuplex(taskCliArguments.getDuplex().getEnumValue());
        }

        return parameters;
    }

    /**
     * @param taskCliArguments
     * @param parameters
     */
    private void populateActivePreferences(ViewerPreferencesTaskCliArguments taskCliArguments,
            ViewerPreferencesParameters parameters) {
        if (taskCliArguments.isCenterWindow()) {
            parameters.addEnabledPreference(PdfBooleanPreference.CENTER_WINDOW);
        }

        if (taskCliArguments.isDisplayDocTitle()) {
            parameters.addEnabledPreference(PdfBooleanPreference.DISPLAY_DOC_TITLE);
        }

        if (taskCliArguments.isFitWindow()) {
            parameters.addEnabledPreference(PdfBooleanPreference.FIT_WINDOW);
        }

        if (taskCliArguments.isHideMenu()) {
            parameters.addEnabledPreference(PdfBooleanPreference.HIDE_MENUBAR);
        }

        if (taskCliArguments.isHideWindowUI()) {
            parameters.addEnabledPreference(PdfBooleanPreference.HIDE_WINDOW_UI);
        }

        if (taskCliArguments.isHideToolbar()) {
            parameters.addEnabledPreference(PdfBooleanPreference.HIDE_TOOLBAR);
        }
    }
}
