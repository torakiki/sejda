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

import org.sejda.model.FriendlyNamed;
import org.sejda.model.pdf.MinRequiredVersion;
import org.sejda.model.pdf.PdfVersion;

/**
 * Possible entries in the transition dictionary defining a visual transition when moving from a page to another in presentation mode.<br>
 * Pdf reference 1.7, section 8.3.3, TABLE 8.13 Entries in a transition dictionary
 * 
 * @author Andrea Vacondio
 * 
 */
public enum PdfPageTransitionStyle implements MinRequiredVersion, FriendlyNamed {

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

    @Override
    public String getFriendlyName() {
        return displayName;
    }

    @Override
    public PdfVersion getMinVersion() {
        return minVersion;
    }
}
