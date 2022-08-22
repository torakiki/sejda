/*
 * Created on 02/jul/2011
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

import jakarta.validation.Valid;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.sejda.model.parameter.base.SinglePdfSourceSingleOutputParameters;
import org.sejda.model.pdf.transition.PdfPageTransition;
import org.sejda.model.validation.constraint.HasTransitions;
import org.sejda.model.validation.constraint.SingleOutputAllowedExtensions;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Parameter class for the set pages transition manipulation.
 *
 * @author Andrea Vacondio
 */
@SingleOutputAllowedExtensions
@HasTransitions
public class SetPagesTransitionParameters extends SinglePdfSourceSingleOutputParameters {

    @Valid
    private final Map<Integer, PdfPageTransition> transitions = new HashMap<Integer, PdfPageTransition>();
    @Valid
    private PdfPageTransition defaultTransition;
    private boolean fullScreen = false;

    public SetPagesTransitionParameters() {
        // no default transition
    }

    /**
     * @param defaultTransition
     *            the default transition
     */
    public SetPagesTransitionParameters(PdfPageTransition defaultTransition) {
        this.defaultTransition = defaultTransition;
    }

    public boolean isFullScreen() {
        return fullScreen;
    }

    public void setFullScreen(boolean fullScreen) {
        this.fullScreen = fullScreen;
    }

    /**
     * Associates the given transition to the given page number. If a transition was already associated to the given page, it is replaced with the new one.
     * 
     * @param page
     * @param transition
     * @return the previously associated transition or null.
     */
    public PdfPageTransition putTransition(Integer page, PdfPageTransition transition) {
        return transitions.put(page, transition);
    }

    /**
     * Clears the collection of transitions stored in this parameter instance.
     */
    public void clearTransitions() {
        transitions.clear();
    }

    /**
     * @return an unmodifiable view of the transitions in this parameter.
     */
    public Map<Integer, PdfPageTransition> getTransitions() {
        return Collections.unmodifiableMap(transitions);
    }

    public PdfPageTransition getDefaultTransition() {
        return defaultTransition;
    }

    /**
     * @param page
     * @return the transition for the given page or the default one if no transition is set for that page
     */
    public PdfPageTransition getOrDefault(int page) {
        return transitions.getOrDefault(page, defaultTransition);
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder().appendSuper(super.hashCode()).append(transitions).append(defaultTransition)
                .append(fullScreen).toHashCode();
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof SetPagesTransitionParameters)) {
            return false;
        }
        SetPagesTransitionParameters parameter = (SetPagesTransitionParameters) other;
        return new EqualsBuilder().appendSuper(super.equals(other)).append(transitions, parameter.getTransitions())
                .append(defaultTransition, parameter.getDefaultTransition())
                .append(fullScreen, parameter.isFullScreen()).isEquals();
    }
}
