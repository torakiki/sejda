package org.sejda.model.pdfa;
/*
 * Created on 08/07/24
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

import java.awt.color.ICC_ColorSpace;
import java.awt.color.ICC_Profile;
import java.io.IOException;
import java.io.InputStream;
import java.util.function.Supplier;

import static java.util.Objects.isNull;

/**
 * A minimal representation of an ICC profile file
 */
public class ICCProfile {

    private final int components;
    private final Supplier<InputStream> profileDataSupplier;
    private ICC_ColorSpace colorSpace;

    public ICCProfile(int components, Supplier<InputStream> profileDataSupplier) {
        this.components = components;
        this.profileDataSupplier = profileDataSupplier;
    }

    public InputStream profileData() {
        return profileDataSupplier.get();
    }

    public int components() {
        return components;
    }

    /**
     * @return The ICC color space based on the ICC profile data.
     * @throws IOException If there is an error reading the profile data.
     */
    public ICC_ColorSpace colorSpace() throws IOException {
        if (isNull(colorSpace)) {
            colorSpace = new ICC_ColorSpace(ICC_Profile.getInstance(profileDataSupplier.get()));
        }
        return colorSpace;
    }

    public static ICCProfile fromSupplier(int components, Supplier<InputStream> profileDataSupplier) {
        return new ICCProfile(components, profileDataSupplier);
    }

    public static ICCProfile fromInputStream(int components, InputStream profileData) {
        return new ICCProfile(components, () -> profileData);
    }
}
