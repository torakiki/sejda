/*
 * Created on 18 dic 2015
 * Copyright 2015 by Andrea Vacondio (andrea.vacondio@gmail.com).
 * This file is part of Sejda.
 *
 * Sejda is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Sejda is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with Sejda.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.sejda.impl.sambox.util;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.sejda.model.pdf.transition.PdfPageTransition;
import org.sejda.model.pdf.transition.PdfPageTransitionStyle;
import org.sejda.sambox.pdmodel.interactive.pagenavigation.PDTransition;
import org.sejda.sambox.pdmodel.interactive.pagenavigation.PDTransitionDimension;
import org.sejda.sambox.pdmodel.interactive.pagenavigation.PDTransitionDirection;
import org.sejda.sambox.pdmodel.interactive.pagenavigation.PDTransitionMotion;
import org.sejda.sambox.pdmodel.interactive.pagenavigation.PDTransitionStyle;

/**
 * Utility class used to deal with transitions mapping.
 * 
 * @author Andrea Vacondio
 * 
 */
public final class TransitionUtils {

    private static final Map<PdfPageTransitionStyle, PDTransitionStyle> TRANSITIONS_STYLES;

    static {
        Map<PdfPageTransitionStyle, PDTransitionStyle> transitionsStyles = new HashMap<>();
        transitionsStyles.put(PdfPageTransitionStyle.BLINDS_HORIZONTAL, PDTransitionStyle.Blinds);
        transitionsStyles.put(PdfPageTransitionStyle.BLINDS_VERTICAL, PDTransitionStyle.Blinds);
        transitionsStyles.put(PdfPageTransitionStyle.SPLIT_HORIZONTAL_INWARD, PDTransitionStyle.Split);
        transitionsStyles.put(PdfPageTransitionStyle.SPLIT_HORIZONTAL_OUTWARD, PDTransitionStyle.Split);
        transitionsStyles.put(PdfPageTransitionStyle.SPLIT_VERTICAL_INWARD, PDTransitionStyle.Split);
        transitionsStyles.put(PdfPageTransitionStyle.SPLIT_VERTICAL_OUTWARD, PDTransitionStyle.Split);
        transitionsStyles.put(PdfPageTransitionStyle.BOX_INWARD, PDTransitionStyle.Box);
        transitionsStyles.put(PdfPageTransitionStyle.BOX_OUTWARD, PDTransitionStyle.Box);
        transitionsStyles.put(PdfPageTransitionStyle.WIPE_BOTTOM_TO_TOP, PDTransitionStyle.Wipe);
        transitionsStyles.put(PdfPageTransitionStyle.WIPE_LEFT_TO_RIGHT, PDTransitionStyle.Wipe);
        transitionsStyles.put(PdfPageTransitionStyle.WIPE_RIGHT_TO_LEFT, PDTransitionStyle.Wipe);
        transitionsStyles.put(PdfPageTransitionStyle.WIPE_TOP_TO_BOTTOM, PDTransitionStyle.Wipe);
        transitionsStyles.put(PdfPageTransitionStyle.DISSOLVE, PDTransitionStyle.Dissolve);
        transitionsStyles.put(PdfPageTransitionStyle.GLITTER_DIAGONAL, PDTransitionStyle.Glitter);
        transitionsStyles.put(PdfPageTransitionStyle.GLITTER_LEFT_TO_RIGHT, PDTransitionStyle.Glitter);
        transitionsStyles.put(PdfPageTransitionStyle.GLITTER_TOP_TO_BOTTOM, PDTransitionStyle.Glitter);
        transitionsStyles.put(PdfPageTransitionStyle.REPLACE, PDTransitionStyle.R);
        transitionsStyles.put(PdfPageTransitionStyle.FLY_LEFT_TO_RIGHT, PDTransitionStyle.Fly);
        transitionsStyles.put(PdfPageTransitionStyle.FLY_LEFT_TO_RIGHT, PDTransitionStyle.Fly);
        transitionsStyles.put(PdfPageTransitionStyle.FADE, PDTransitionStyle.Fade);
        transitionsStyles.put(PdfPageTransitionStyle.COVER_LEFT_TO_RIGHT, PDTransitionStyle.Cover);
        transitionsStyles.put(PdfPageTransitionStyle.COVER_TOP_TO_BOTTOM, PDTransitionStyle.Cover);
        transitionsStyles.put(PdfPageTransitionStyle.UNCOVER_LEFT_TO_RIGHT, PDTransitionStyle.Uncover);
        transitionsStyles.put(PdfPageTransitionStyle.UNCOVER_TOP_TO_BOTTOM, PDTransitionStyle.Uncover);
        transitionsStyles.put(PdfPageTransitionStyle.PUSH_LEFT_TO_RIGHT, PDTransitionStyle.Push);
        transitionsStyles.put(PdfPageTransitionStyle.PUSH_TOP_TO_BOTTOM, PDTransitionStyle.Push);
        TRANSITIONS_STYLES = Collections.unmodifiableMap(transitionsStyles);
    }

    private TransitionUtils() {
        // utility
    }

    /**
     * Mapping between Sejda transition style enum and SAMBox constants.<br>
     * 
     * @param transition
     * @return the SAMBox constant or null of no constant is found.
     */
    public static PDTransitionStyle getTransition(PdfPageTransitionStyle transition) {
        return TRANSITIONS_STYLES.get(transition);
    }

    /**
     * Initialize the transition dimension if the style supports it
     * 
     * @param from
     * @param to
     */
    public static void initTransitionDimension(PdfPageTransition from, PDTransition to) {
        switch (from.getStyle()) {
        case BLINDS_HORIZONTAL:
        case SPLIT_HORIZONTAL_INWARD:
        case SPLIT_HORIZONTAL_OUTWARD:
            to.setDimension(PDTransitionDimension.H);
            break;
        case BLINDS_VERTICAL:
        case SPLIT_VERTICAL_INWARD:
        case SPLIT_VERTICAL_OUTWARD:
            to.setDimension(PDTransitionDimension.V);
            break;
        }
    }

    /**
     * Initialize the transition motion if the style supports it
     * 
     * @param from
     * @param to
     */
    public static void initTransitionMotion(PdfPageTransition from, PDTransition to) {
        switch (from.getStyle()) {
        case BOX_INWARD:
        case SPLIT_HORIZONTAL_INWARD:
        case SPLIT_VERTICAL_INWARD:
            to.setMotion(PDTransitionMotion.I);
            break;
        case BOX_OUTWARD:
        case SPLIT_HORIZONTAL_OUTWARD:
        case SPLIT_VERTICAL_OUTWARD:
            to.setMotion(PDTransitionMotion.O);
            break;
        }
    }

    /**
     * Initialize the transition motion if the style supports it
     * 
     * @param from
     * @param to
     */
    public static void initTransitionDirection(PdfPageTransition from, PDTransition to) {
        switch (from.getStyle()) {
        case WIPE_BOTTOM_TO_TOP:
            to.setDirection(PDTransitionDirection.BOTTOM_TO_TOP);
            break;
        case WIPE_TOP_TO_BOTTOM:
        case GLITTER_TOP_TO_BOTTOM:
        case FLY_TOP_TO_BOTTOM:
        case PUSH_TOP_TO_BOTTOM:
        case COVER_TOP_TO_BOTTOM:
        case UNCOVER_TOP_TO_BOTTOM:
            to.setDirection(PDTransitionDirection.TOP_TO_BOTTOM);
            break;
        case WIPE_LEFT_TO_RIGHT:
        case GLITTER_LEFT_TO_RIGHT:
        case FLY_LEFT_TO_RIGHT:
        case PUSH_LEFT_TO_RIGHT:
        case COVER_LEFT_TO_RIGHT:
        case UNCOVER_LEFT_TO_RIGHT:
            to.setDirection(PDTransitionDirection.LEFT_TO_RIGHT);
            break;
        case WIPE_RIGHT_TO_LEFT:
            to.setDirection(PDTransitionDirection.RIGHT_TO_LEFT);
            break;
        case GLITTER_DIAGONAL:
            to.setDirection(PDTransitionDirection.TOP_LEFT_TO_BOTTOM_RIGHT);
            break;
        }
    }
}
