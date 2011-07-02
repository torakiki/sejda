package org.sejda.core.manipulation.model.pdf.transition;

import java.security.InvalidParameterException;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

/**
 * Model for a page transition. <br>
 * Pdf reference 1.7, Chap. 8.3.3
 * 
 * @author Andrea Vacondio
 * 
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

        if (transitionDuration < 1) {
            throw new InvalidParameterException("Input transition duration must be positive.");
        }
        if (displayDuration < 1) {
            throw new InvalidParameterException("Input display duration must be positive.");
        }
        if (style == null) {
            throw new InvalidParameterException("Input style cannot be null.");
        }
        return new PdfPageTransition(style, transitionDuration, displayDuration);
    }

}
