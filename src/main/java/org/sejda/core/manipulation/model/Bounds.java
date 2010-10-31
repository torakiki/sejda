/*
 * Created on 29/mag/2010
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
package org.sejda.core.manipulation.model;

/**
 * Model for an interval of pages
 * 
 * @author Andrea Vacondio
 * 
 */
public class Bounds {

    private static final long serialVersionUID = 1093984828590806028L;

    private int start;
    private int end;

    public Bounds(int start, int end) {
        this.start = start;
        this.end = end;
    }

    public int getStart() {
        return start;
    }

    public int getEnd() {
        return end;
    }

    public String toString() {
        return start + "-" + end;
    }

    /**
     * @param bounds
     * @return <code>true</code> if the input bounds intersect this {@link Bounds} instance
     */
    public boolean intersects(Bounds bounds) {
        return ((bounds.getStart() >= start && bounds.getStart() <= end) || (bounds.getEnd() >= start && bounds
                .getEnd() <= end));
    }

}
