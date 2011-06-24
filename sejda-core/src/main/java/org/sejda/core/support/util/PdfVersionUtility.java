/*
 * Created on 28/nov/2010
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
package org.sejda.core.support.util;

import org.sejda.core.manipulation.model.pdf.MinRequiredVersion;
import org.sejda.core.manipulation.model.pdf.PdfVersion;

/**
 * Provides some utility method on the PdfVersion enum
 * 
 * @author Andrea Vacondio
 * 
 */
public final class PdfVersionUtility {

    private PdfVersionUtility() {
        // utility class
    }

    /**
     * @param pdfVersions
     * @return the max version in the give input versions
     */
    public static PdfVersion getMax(PdfVersion... pdfVersions) {
        PdfVersion max = null;
        for (PdfVersion current : pdfVersions) {
            if (max == null || max.compareTo(current) < 0) {
                max = current;
            }
        }
        return max;
    }

    /**
     * @param items
     * @return the max version in the give input MinRequiredVersion array
     */
    public static PdfVersion getMax(MinRequiredVersion... items) {
        PdfVersion max = null;
        for (MinRequiredVersion current : items) {
            if (current != null && (max == null || max.compareTo(current.getMinVersion()) < 0)) {
                max = current.getMinVersion();
            }
        }
        return max;
    }
}
