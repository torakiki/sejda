/*
 * Created on 15/nov/2012
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
 * Possible vertical alignments
 * 
 * @author Andrea Vacondio
 * 
 */
public enum VerticalAlign implements FriendlyNamed {

    TOP("top") {
        @Override
        public float position(float pageHight, float margin) {
            return pageHight - margin;
        }
    },
    BOTTOM("bottom") {
        @Override
        public float position(float pageHight, float margin) {
            return margin;
        }
    };
    private String displayName;

    private VerticalAlign(String displayName) {
        this.displayName = displayName;
    }

    public String getFriendlyName() {
        return displayName;
    }

    /**
     * @param pageHight
     * @param margin
     * @return the vertical position of a text in a page of the given height
     */
    public abstract float position(float pageHeight, float margin);
}
