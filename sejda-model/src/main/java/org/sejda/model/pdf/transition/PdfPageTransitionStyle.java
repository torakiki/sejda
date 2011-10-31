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
package org.sejda.model.pdf.transition;

import org.sejda.common.DisplayNamedEnum;
import org.sejda.model.pdf.MinRequiredVersion;
import org.sejda.model.pdf.PdfVersion;

/**
 * Possible entries in the transition dictionary defining a visual transition when moving from a page to another in presentation mode.<br>
 * Pdf reference 1.7, section 8.3.3, TABLE 8.13 Entries in a transition dictionary
 * 
 * @author Andrea Vacondio
 * 
 */
public enum PdfPageTransitionStyle implements MinRequiredVersion, DisplayNamedEnum {

    SPLIT_HORIZONTAL_INWARD("split_horizontal_inward", PdfVersion.VERSION_1_1),
    SPLIT_HORIZONTAL_OUTWARD("split_horizontal_outward", PdfVersion.VERSION_1_1),
    SPLIT_VERTICAL_INWARD("split_vertical_inward", PdfVersion.VERSION_1_1),
    SPLIT_VERTICAL_OUTWARD("split_vertical_outward", PdfVersion.VERSION_1_1),
    BLINDS_HORIZONTAL("blinds_horizontal", PdfVersion.VERSION_1_1),
    BLINDS_VERTICAL("blinds_vertical", PdfVersion.VERSION_1_1),
    BOX_INWARD("box_inward", PdfVersion.VERSION_1_1),
    BOX_OUTWARD("box_outward", PdfVersion.VERSION_1_1),
    DISSOLVE("dissolve", PdfVersion.VERSION_1_1),
    WIPE_LEFT_TO_RIGHT("wipe_left_to_right", PdfVersion.VERSION_1_1),
    WIPE_RIGHT_TO_LEFT("wipe_right_to_left", PdfVersion.VERSION_1_1),
    WIPE_TOP_TO_BOTTOM("wipe_top_to_bottom", PdfVersion.VERSION_1_1),
    WIPE_BOTTOM_TO_TOP("wipe_bottom_to_top", PdfVersion.VERSION_1_1),
    GLITTER_LEFT_TO_RIGHT("glitter_left_to_right", PdfVersion.VERSION_1_1),
    GLITTER_TOP_TO_BOTTOM("glitter_top_to_bottom", PdfVersion.VERSION_1_1),
    GLITTER_DIAGONAL("glitter_diagonal", PdfVersion.VERSION_1_1),
    REPLACE("replace", PdfVersion.VERSION_1_1),
    PUSH_LEFT_TO_RIGHT("push_left_to_right", PdfVersion.VERSION_1_5),
    PUSH_TOP_TO_BOTTOM("push_top_to_bottom", PdfVersion.VERSION_1_5),
    COVER_LEFT_TO_RIGHT("cover_left_to_right", PdfVersion.VERSION_1_5),
    COVER_TOP_TO_BOTTOM("cover_top_to_bottom", PdfVersion.VERSION_1_5),
    UNCOVER_LEFT_TO_RIGHT("uncover_left_to_right", PdfVersion.VERSION_1_5),
    UNCOVER_TOP_TO_BOTTOM("uncover_top_to_bottom", PdfVersion.VERSION_1_5),
    FADE("fade", PdfVersion.VERSION_1_5),
    FLY_LEFT_TO_RIGHT("fly_left_to_right", PdfVersion.VERSION_1_5),
    FLY_TOP_TO_BOTTOM("fly_top_to_bottom", PdfVersion.VERSION_1_5);

    private PdfVersion minVersion;
    private String displayName;

    private PdfPageTransitionStyle(String displayName, PdfVersion minVersion) {
        this.displayName = displayName;
        this.minVersion = minVersion;
    }

    public String getDisplayName() {
        return displayName;
    }

    public PdfVersion getMinVersion() {
        return minVersion;
    }
}
