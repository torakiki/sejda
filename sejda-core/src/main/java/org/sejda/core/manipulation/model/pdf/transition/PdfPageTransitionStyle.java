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
