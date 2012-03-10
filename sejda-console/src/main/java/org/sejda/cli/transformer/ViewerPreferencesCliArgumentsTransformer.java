/*
 * Created on Jul 1, 2011
 * Copyright 2011 by Eduard Weissmann (edi.weissmann@gmail.com).
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
