/*
 * Created on 21/set/2010
 * Copyright (C) 2010 by Andrea Vacondio (andrea.vacondio@gmail.com).
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License.
 * 
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 */
package org.sejda.core.manipulation.model.parameter;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import javax.validation.constraints.NotNull;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
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
public class ViewerPreferencesParameters extends PdfSourceListParameters {

    private static final long serialVersionUID = 7732070350985819758L;

    private String outputPrefix = "";
    @NotNull
    private PdfPageMode pageMode = PdfPageMode.USE_NONE;
    @NotNull
    private PdfPageLayout pageLayout = PdfPageLayout.SINGLE_PAGE;
    @NotNull
    private PdfNonFullScreenPageMode nfsMode = PdfNonFullScreenPageMode.USE_NONE;
    private PdfDuplex duplex;
    private PdfDirection direction;
    private PdfPrintScaling printScaling;
    private Set<PdfBooleanPreference> activeBooleanPreferences = new HashSet<PdfBooleanPreference>();

    public String getOutputPrefix() {
        return outputPrefix;
    }

    public void setOutputPrefix(String outputPrefix) {
        this.outputPrefix = outputPrefix;
    }

    public boolean addActivePreference(PdfBooleanPreference e) {
        return activeBooleanPreferences.add(e);
    }

    public void clearActivePreferences() {
        activeBooleanPreferences.clear();
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
     * @return an unmodifiable view of the active boolean preferences
     */
    public Set<PdfBooleanPreference> getActivePreferences() {
        return Collections.unmodifiableSet(activeBooleanPreferences);
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder().appendSuper(super.hashCode()).append(outputPrefix).append(printScaling).append(
                direction).append(duplex).append(pageLayout).append(pageMode).append(nfsMode).append(
                activeBooleanPreferences).toHashCode();
    }

    @Override
    public boolean equals(Object other) {
        if (!(other instanceof ViewerPreferencesParameters)) {
            return false;
        }
        ViewerPreferencesParameters parameter = (ViewerPreferencesParameters) other;
        return new EqualsBuilder().appendSuper(super.equals(other)).append(outputPrefix, parameter.getOutputPrefix())
                .append(printScaling, parameter.getPrintScaling()).append(direction, parameter.getDirection()).append(
                        duplex, parameter.getDuplex()).append(pageLayout, parameter.getPageLayout()).append(pageMode,
                        parameter.getPageMode()).append(nfsMode, parameter.getNfsMode()).append(
                        activeBooleanPreferences, parameter.getActivePreferences()).isEquals();
    }
}
