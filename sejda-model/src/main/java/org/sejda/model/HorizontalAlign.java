/*
 * Created on 14/nov/2012
 * Copyright 2011 by Andrea Vacondio (andrea.vacondio@gmail.com).
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
package org.sejda.model;

import org.sejda.common.FriendlyNamed;

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
