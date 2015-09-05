/*
 * Created on 30/mag/2010
 *
 * Copyright 2010 by Andrea Vacondio (andrea.vacondio@gmail.com).
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
package org.sejda.model.pdf;

import org.sejda.common.FriendlyNamed;

/**
 * pdf versions
 * 
 * @author Andrea Vacondio
 * 
 */
public enum PdfVersion implements FriendlyNamed {

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
    private String displayName;

    private PdfVersion(int version, double versionDouble, String versionHeader) {
        this.displayName = String.valueOf(version);
        this.version = version;
        this.versionDouble = versionDouble;
        this.versionHeader = versionHeader;
    }

    @Override
    public String getFriendlyName() {
        return displayName;
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
     * @return a double representation of the version
     */
    public String getVersionAsDoubleString() {
        return String.valueOf(versionDouble);
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
