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
package org.sejda.model.pdf.transition;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

import java.security.InvalidParameterException;

import static org.sejda.commons.util.RequireUtils.requireArg;
import static org.sejda.commons.util.RequireUtils.requireNotNullArg;

/**
 * Model for a page transition. <br>
 * Pdf reference 1.7, Chap. 8.3.3
 *
 * @author Andrea Vacondio
 */
public final class PdfPageTransition {

    @NotNull
    private PdfPageTransitionStyle style;
    @Min(value = 1)
    private int transitionDuration;
    @Min(value = 1)
    private int displayDuration;

    private PdfPageTransition(PdfPageTransitionStyle style, int transitionDuration, int displayDuration) {
        this.style = style;
        this.transitionDuration = transitionDuration;
        this.displayDuration = displayDuration;
    }

    public PdfPageTransitionStyle getStyle() {
        return style;
    }

    /**
     * @return The duration of the transition effect in seconds.
     */
    public int getTransitionDuration() {
        return transitionDuration;
    }

    /**
     * @return the number of seconds a page is displayed before the transition to the next page is triggered.
     */
    public int getDisplayDuration() {
        return displayDuration;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this).append(style).append("transitionDuration", transitionDuration)
                .append("displayDuration", displayDuration).toString();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder().append(style).append(transitionDuration).append(displayDuration).toHashCode();
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof PdfPageTransition)) {
            return false;
        }
        PdfPageTransition transition = (PdfPageTransition) other;
        return new EqualsBuilder().append(style, transition.getStyle())
                .append(transitionDuration, transition.getTransitionDuration())
                .append(displayDuration, transition.getDisplayDuration()).isEquals();
    }

    /**
     * Creates a new {@link PdfPageTransition} instance.
     * 
     * @param style
     * @param transitionDuration
     * @param displayDuration
     * @return the newly created instance.
     * @throws InvalidParameterException
     *             if the input transition or display duration is not positive. if the input style is null.
     */
    public static PdfPageTransition newInstance(PdfPageTransitionStyle style, int transitionDuration,
            int displayDuration) {
        requireArg(displayDuration > 0, "Input display duration must be positive");
        requireArg(transitionDuration > 0, "Input transition duration must be positive");
        requireNotNullArg(style, "Input style cannot be null");
        return new PdfPageTransition(style, transitionDuration, displayDuration);
    }

}
