/*
 * Created on 21/set/2010
 *
 * Copyright 2010 by Andrea Vacondio (andrea.vacondio@gmail.com).
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
package org.sejda.core.manipulation.model.parameter;

import static org.sejda.core.support.util.PdfVersionUtility.getMax;

import java.util.Collections;
import java.util.EnumSet;
import java.util.Set;

import javax.validation.constraints.NotNull;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.sejda.core.manipulation.model.parameter.base.MultiplePdfSourceMultipleOutputParameters;
import org.sejda.core.manipulation.model.pdf.MinRequiredVersion;
import org.sejda.core.manipulation.model.pdf.PdfVersion;
import org.sejda.core.manipulation.model.pdf.viewerpreferences.PdfBooleanPreference;
import org.sejda.core.manipulation.model.pdf.viewerpreferences.PdfDirection;
import org.sejda.core.manipulation.model.pdf.viewerpreferences.PdfDuplex;
import org.sejda.core.manipulation.model.pdf.viewerpreferences.PdfNonFullScreenPageMode;
import org.sejda.core.manipulation.model.pdf.viewerpreferences.PdfPageLayout;
import org.sejda.core.manipulation.model.pdf.viewerpreferences.PdfPageMode;
import org.sejda.core.manipulation.model.pdf.viewerpreferences.PdfPrintScaling;

/**
 * Parameter class for the set viewer preferences manipulation. Accepts a list of {@link org.sejda.core.manipulation.model.input.PdfSource} where the view preferences will be
 * applied.
 * 
 * @author Andrea Vacondio
 * 
 */
public class ViewerPreferencesParameters extends MultiplePdfSourceMultipleOutputParameters {

    @NotNull
    private PdfPageMode pageMode = PdfPageMode.USE_NONE;
    @NotNull
    private PdfPageLayout pageLayout = PdfPageLayout.SINGLE_PAGE;
    @NotNull
    private PdfNonFullScreenPageMode nfsMode = PdfNonFullScreenPageMode.USE_NONE;
    private PdfDuplex duplex;
    private PdfDirection direction;
    private PdfPrintScaling printScaling;
    private Set<PdfBooleanPreference> enabledBooleanPreferences = EnumSet.noneOf(PdfBooleanPreference.class);

    public boolean addEnabledPreference(PdfBooleanPreference e) {
        return enabledBooleanPreferences.add(e);
    }

    public void clearEnabledPreferences() {
        enabledBooleanPreferences.clear();
    }

    public PdfPageMode getPageMode() {
        return pageMode;
    }

    public void setPageMode(PdfPageMode pageMode) {
        this.pageMode = pageMode;
    }

    public PdfPageLayout getPageLayout() {
        return pageLayout;
    }

    public void setPageLayout(PdfPageLayout pageLayout) {
        this.pageLayout = pageLayout;
    }

    public PdfNonFullScreenPageMode getNfsMode() {
        return nfsMode;
    }

    public void setNfsMode(PdfNonFullScreenPageMode nfsMode) {
        this.nfsMode = nfsMode;
    }

    public PdfDuplex getDuplex() {
        return duplex;
    }

    public void setDuplex(PdfDuplex duplex) {
        this.duplex = duplex;
    }

    public PdfDirection getDirection() {
        return direction;
    }

    public void setDirection(PdfDirection direction) {
        this.direction = direction;
    }

    public PdfPrintScaling getPrintScaling() {
        return printScaling;
    }

    public void setPrintScaling(PdfPrintScaling printScaling) {
        this.printScaling = printScaling;
    }

    /**
     * @return an unmodifiable view of the enabled boolean preferences
     */
    public Set<PdfBooleanPreference> getEnabledPreferences() {
        return Collections.unmodifiableSet(enabledBooleanPreferences);
    }

    @Override
    public PdfVersion getMinRequiredPdfVersion() {
        return getMax(super.getMinRequiredPdfVersion(), getMax(printScaling, direction, duplex, pageLayout, pageMode),
                getMax(enabledBooleanPreferences.toArray(new MinRequiredVersion[enabledBooleanPreferences.size()])));
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder().appendSuper(super.hashCode()).append(printScaling).append(direction)
                .append(duplex).append(pageLayout).append(pageMode).append(nfsMode).append(enabledBooleanPreferences)
                .toHashCode();
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof ViewerPreferencesParameters)) {
            return false;
        }
        ViewerPreferencesParameters parameter = (ViewerPreferencesParameters) other;
        return new EqualsBuilder().appendSuper(super.equals(other)).append(printScaling, parameter.getPrintScaling())
                .append(direction, parameter.getDirection()).append(duplex, parameter.getDuplex())
                .append(pageLayout, parameter.getPageLayout()).append(pageMode, parameter.getPageMode())
                .append(nfsMode, parameter.getNfsMode())
                .append(enabledBooleanPreferences, parameter.getEnabledPreferences()).isEquals();
    }
}
