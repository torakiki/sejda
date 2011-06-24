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
 * 
 * @author Andrea Vacondio
 * 
 */
public enum PdfVersion {

    VERSION_1_0(0, 1.0d, "%PDF-1.0"),
    VERSION_1_1(1, 1.1d, "%PDF-1.1"),
    VERSION_1_2(2, 1.2d, "%PDF-1.2"),
    VERSION_1_3(3, 1.3d, "%PDF-1.3"),
    VERSION_1_4(4, 1.4d, "%PDF-1.4"),
    VERSION_1_5(5, 1.5d, "%PDF-1.5"),
    VERSION_1_6(6, 1.6d, "%PDF-1.6"),
    VERSION_1_7(7, 1.7d, "%PDF-1.7");

    private int version;
    private double versionDouble;
    private String versionHeader;

    private PdfVersion(int version, double versionDouble, String versionHeader) {
        this.version = version;
        this.versionDouble = versionDouble;
        this.versionHeader = versionHeader;
    }

    /**
     * @return an int representation of the version
     */
    public int getVersion() {
        return version;
    }

    /**
     * @return a double representation of the version
     */
    public double getVersionAsDouble() {
        return versionDouble;
    }

    /**
     * @return a char representation of the int version
     */
    public char getVersionAsCharacter() {
        return String.valueOf(version).charAt(0);
    }

    /**
     * @return the PDF header for this version<br>
     *         Pdf reference 1.7, 3.4.1 File Header
     */
    public String getVersionHeader() {
        return versionHeader;
    }

}
