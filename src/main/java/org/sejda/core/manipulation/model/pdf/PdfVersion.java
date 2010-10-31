/*
 * Created on 30/mag/2010
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
package org.sejda.core.manipulation.model.pdf;

/**
 * pdf versions
 * @author Andrea Vacondio
 * 
 */
public enum PdfVersion {

    VERSION_1_2(2), VERSION_1_3(3), VERSION_1_4(4), VERSION_1_5(5), VERSION_1_6(6), VERSION_1_7(7);
    
    private int version;

    private PdfVersion(int version) {
        this.version = version;
    }

    /**
     * @return an int representation of the version
     */
    public int getVersion() {
        return version;
    }

    /**
     * @return a String representation of the int version
     */
    public String getVersionAsString() {
        return String.valueOf(version);
    }

    /**
     * @return a char representation of the int version
     */
    public char getVersionAsCharacter() {
        return getVersionAsString().charAt(0);
    }
}
