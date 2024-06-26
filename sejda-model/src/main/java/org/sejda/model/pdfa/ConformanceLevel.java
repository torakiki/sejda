package org.sejda.model.pdfa;
/*
 * Created on 29/05/24
 * Copyright 2024 Sober Lemur S.r.l. and Sejda BV
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

import java.util.Set;

/**
 * Conformance levels for PDF/A
 *
 * @author Andrea Vacondio
 */
public enum ConformanceLevel {

    PDFA_1B {
        @Override
        public Set<String> allowedAnnotationTypes() {
            return Set.of("Text", "Link", "FreeText", "Line", "Square", "Circle", "Highlight", "Underline", "Squiggly",
                    "StrikeOut", "Stamp", "Ink", "Popup", "Widget", "PrinterMark", "TrapNet");
        }

        @Override
        public Set<String> allowedActionTypes() {
            return Set.of("GoTo", "GoToR", "Thread", "URI", "Named");
        }
    };

    /**
     * @return the allowed annotations types for the conformance level
     */
    public abstract Set<String> allowedAnnotationTypes();

    /**
     * @return the allowed action types for the conformance level
     */
    public abstract Set<String> allowedActionTypes();

    /**
     * @return the allowed values for a named action
     */
    public Set<String> allowedNamedActions() {
        return Set.of("NextPage", "PrevPage", "FirstPage", "LastPage");
    }
}
