/*
 * Created on 14/nov/2012
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
 * Possible horizontal alignments.
 * 
 * @author Andrea Vacondio
 * 
 */
public enum HorizontalAlign implements FriendlyNamed {

    CENTER("center") {
        @Override
        public float position(float pageWidth, float labelWidth, float margin) {
            return (pageWidth - labelWidth) / 2f;
        }
    },
    RIGHT("right") {
        @Override
        public float position(float pageWidth, float labelWidth, float margin) {
            return pageWidth - labelWidth - margin;
        }
    },
    LEFT("left") {
        @Override
        public float position(float pageWidth, float labelWidth, float margin) {
            return margin;
        }
    };

    private String displayName;

    private HorizontalAlign(String displayName) {
        this.displayName = displayName;
    }

    @Override
    public String getFriendlyName() {
        return displayName;
    }

    /**
     * @param pageWidth
     *            page width in pts
     * @param stringWidth
     *            width of the text we are positioning in pts
     * @param margin
     *            margin to use. Relevant only in non central alignment.
     * @return the horizontal position of a label with the given width in a page of the given width
     */
    public abstract float position(float pageWidth, float stringWidth, float margin);
}
