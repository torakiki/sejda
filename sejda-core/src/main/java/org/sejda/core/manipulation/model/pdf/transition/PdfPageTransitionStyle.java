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
package org.sejda.core.manipulation.model.pdf.transition;

import org.sejda.core.manipulation.model.pdf.MinRequiredVersion;
import org.sejda.core.manipulation.model.pdf.PdfVersion;

/**
 * Possible entries in the transition dictionary defining a visual transition when moving from a page to another in presentation mode.<br>
 * Pdf reference 1.7, section 8.3.3, TABLE 8.13 Entries in a transition dictionary
 * 
 * @author Andrea Vacondio
 * 
 */
public enum PdfPageTransitionStyle implements MinRequiredVersion {

    SPLIT_HORIZONTAL_INWARD(PdfVersion.VERSION_1_1),
    SPLIT_HORIZONTAL_OUTWARD(PdfVersion.VERSION_1_1),
    SPLIT_VERTICAL_INWARD(PdfVersion.VERSION_1_1),
    SPLIT_VERTICAL_OUTWARD(PdfVersion.VERSION_1_1),
    BLINDS_HORIZONTAL(PdfVersion.VERSION_1_1),
    BLINDS_VERTICAL(PdfVersion.VERSION_1_1),
    BOX_INWARD(PdfVersion.VERSION_1_1),
    BOX_OUTWARD(PdfVersion.VERSION_1_1),
    DISSOLVE(PdfVersion.VERSION_1_1),
    WIPE_LEFT_TO_RIGHT(PdfVersion.VERSION_1_1),
    WIPE_RIGHT_TO_LEFT(PdfVersion.VERSION_1_1),
    WIPE_TOP_TO_BOTTOM(PdfVersion.VERSION_1_1),
    WIPE_BOTTOM_TO_TOP(PdfVersion.VERSION_1_1),
    GLITTER_LEFT_TO_RIGHT(PdfVersion.VERSION_1_1),
    GLITTER_TOP_TO_BOTTOM(PdfVersion.VERSION_1_1),
    GLITTER_DIAGONAL(PdfVersion.VERSION_1_1),
    REPLACE(PdfVersion.VERSION_1_1),
    PUSH_LEFT_TO_RIGHT(PdfVersion.VERSION_1_5),
    PUSH_TOP_TO_BOTTOM(PdfVersion.VERSION_1_5),
    COVER_LEFT_TO_RIGHT(PdfVersion.VERSION_1_5),
    COVER_TOP_TO_BOTTOM(PdfVersion.VERSION_1_5),
    UNCOVER_LEFT_TO_RIGHT(PdfVersion.VERSION_1_5),
    UNCOVER_TOP_TO_BOTTOM(PdfVersion.VERSION_1_5),
    FADE(PdfVersion.VERSION_1_5),
    FLY_LEFT_TO_RIGHT(PdfVersion.VERSION_1_5),
    FLY_TOP_TO_BOTTOM(PdfVersion.VERSION_1_5);

    private PdfVersion minVersion;

    private PdfPageTransitionStyle(PdfVersion minVersion) {
        this.minVersion = minVersion;
    }

    public PdfVersion getMinVersion() {
        return minVersion;
    }
}
