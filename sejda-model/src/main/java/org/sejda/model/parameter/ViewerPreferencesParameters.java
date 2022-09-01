/*
 * Created on 21/set/2010
 *
 * Copyright 2010 by Andrea Vacondio (andrea.vacondio@gmail.com).
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
package org.sejda.model.parameter;

import java.util.Collections;
import java.util.EnumSet;
import java.util.Set;

import jakarta.validation.constraints.NotNull;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.sejda.model.parameter.base.MultiplePdfSourceMultipleOutputParameters;
import org.sejda.model.pdf.MinRequiredVersion;
import org.sejda.model.pdf.PdfVersion;
import org.sejda.model.pdf.viewerpreference.PdfBooleanPreference;
import org.sejda.model.pdf.viewerpreference.PdfDirection;
import org.sejda.model.pdf.viewerpreference.PdfDuplex;
import org.sejda.model.pdf.viewerpreference.PdfNonFullScreenPageMode;
import org.sejda.model.pdf.viewerpreference.PdfPageLayout;
import org.sejda.model.pdf.viewerpreference.PdfPageMode;
import org.sejda.model.pdf.viewerpreference.PdfPrintScaling;

/**
 * Parameter class for the set viewer preferences manipulation. Accepts a list of {@link org.sejda.model.input.PdfSource} where the view preferences will be applied.
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
        return PdfVersion.getMax(super.getMinRequiredPdfVersion(), PdfVersion.getMax(printScaling, direction, duplex,
                pageLayout, pageMode), PdfVersion.getMax(enabledBooleanPreferences
                .toArray(new MinRequiredVersion[enabledBooleanPreferences.size()])));
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
        if (!(other instanceof ViewerPreferencesParameters parameter)) {
            return false;
        }
        return new EqualsBuilder().appendSuper(super.equals(other)).append(printScaling, parameter.getPrintScaling())
                .append(direction, parameter.getDirection()).append(duplex, parameter.getDuplex())
                .append(pageLayout, parameter.getPageLayout()).append(pageMode, parameter.getPageMode())
                .append(nfsMode, parameter.getNfsMode())
                .append(enabledBooleanPreferences, parameter.getEnabledPreferences()).isEquals();
    }
}
