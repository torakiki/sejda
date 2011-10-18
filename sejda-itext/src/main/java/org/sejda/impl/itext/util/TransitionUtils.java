/*
 * Created on 02/jul/2011
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
package org.sejda.impl.itext.util;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.sejda.core.manipulation.model.pdf.transition.PdfPageTransitionStyle;

import com.lowagie.text.pdf.PdfTransition;

/**
 * Utility class used to deal with transitions mapping.
 * 
 * @author Andrea Vacondio
 * 
 */
public final class TransitionUtils {

    private static final Map<PdfPageTransitionStyle, Integer> TRANSITIONS_STYLES;
    static {
        Map<PdfPageTransitionStyle, Integer> transitionsStyles = new HashMap<PdfPageTransitionStyle, Integer>();
        transitionsStyles.put(PdfPageTransitionStyle.BLINDS_HORIZONTAL, PdfTransition.BLINDH);
        transitionsStyles.put(PdfPageTransitionStyle.BLINDS_VERTICAL, PdfTransition.BLINDV);
        transitionsStyles.put(PdfPageTransitionStyle.SPLIT_HORIZONTAL_INWARD, PdfTransition.SPLITHIN);
        transitionsStyles.put(PdfPageTransitionStyle.SPLIT_HORIZONTAL_OUTWARD, PdfTransition.SPLITHOUT);
        transitionsStyles.put(PdfPageTransitionStyle.SPLIT_VERTICAL_INWARD, PdfTransition.SPLITVIN);
        transitionsStyles.put(PdfPageTransitionStyle.SPLIT_VERTICAL_OUTWARD, PdfTransition.SPLITVOUT);
        transitionsStyles.put(PdfPageTransitionStyle.BOX_INWARD, PdfTransition.INBOX);
        transitionsStyles.put(PdfPageTransitionStyle.BOX_OUTWARD, PdfTransition.OUTBOX);
        transitionsStyles.put(PdfPageTransitionStyle.WIPE_BOTTOM_TO_TOP, PdfTransition.BTWIPE);
        transitionsStyles.put(PdfPageTransitionStyle.WIPE_LEFT_TO_RIGHT, PdfTransition.LRWIPE);
        transitionsStyles.put(PdfPageTransitionStyle.WIPE_RIGHT_TO_LEFT, PdfTransition.RLWIPE);
        transitionsStyles.put(PdfPageTransitionStyle.WIPE_TOP_TO_BOTTOM, PdfTransition.TBWIPE);
        transitionsStyles.put(PdfPageTransitionStyle.DISSOLVE, PdfTransition.DISSOLVE);
        transitionsStyles.put(PdfPageTransitionStyle.GLITTER_DIAGONAL, PdfTransition.DGLITTER);
        transitionsStyles.put(PdfPageTransitionStyle.GLITTER_LEFT_TO_RIGHT, PdfTransition.LRGLITTER);
        transitionsStyles.put(PdfPageTransitionStyle.GLITTER_TOP_TO_BOTTOM, PdfTransition.TBGLITTER);
        TRANSITIONS_STYLES = Collections.unmodifiableMap(transitionsStyles);
    }

    private TransitionUtils() {
        // utility
    }

    /**
     * Mapping between Sejda transition style enum and iText constants.<br>
     * Not all the possible transition styles are available in iText so this method can return null if a mapping is not found.
     * 
     * @param transition
     * @return the iText constant or null of no constant is found.
     */
    public static Integer getTransition(PdfPageTransitionStyle transition) {
        return TRANSITIONS_STYLES.get(transition);
    }
}
