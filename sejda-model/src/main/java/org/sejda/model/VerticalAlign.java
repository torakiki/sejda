/*
 * Created on 15/nov/2012
 * Copyright 2011 by Andrea Vacondio (andrea.vacondio@gmail.com).
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
package org.sejda.model;

/**
 * Possible vertical alignments
 * 
 * @author Andrea Vacondio
 * 
 */
public enum VerticalAlign implements FriendlyNamed {

    TOP("top") {
        @Override
        public float position(float pageHight, float margin, float fontSize) {
            return pageHight - margin - fontSize / 2;
        }
    },
    BOTTOM("bottom") {
        @Override
        public float position(float pageHight, float margin, float fontSize) {
            return margin;
        }
    };
    private String displayName;

    private VerticalAlign(String displayName) {
        this.displayName = displayName;
    }

    @Override
    public String getFriendlyName() {
        return displayName;
    }

    /**
     * @param pageHeight
     * @param margin
     * @return the vertical position of a text in a page of the given height
     */
    public abstract float position(float pageHeight, float margin, float fontSize);
}
