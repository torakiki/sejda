package org.sejda.model.pdfa;
/*
 * Created on 10/07/24
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

/**
 * @author Andrea Vacondio
 */
public class DefaultCMYKOutputIntent implements OutputIntent {
    @Override
    public ICCProfile profile() {
        return new CompactCGATSTR001();
    }

    @Override
    public String info() {
        return "Compact CGATS TR 001";
    }

    @Override
    public String registryName() {
        return "http://www.color.org";
    }

    @Override
    public String outputConditionIdentifier() {
        return "CMYK CGATS TR 001";
    }

    @Override
    public String outputCondition() {
        return "CGATS TR 001";
    }
}
