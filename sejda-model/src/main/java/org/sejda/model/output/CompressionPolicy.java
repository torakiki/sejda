package org.sejda.model.output;
/*
 * Created on 24/03/26
 * Copyright 2026 Sober Lemur S.r.l. and Sejda BV
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

import org.sejda.model.FriendlyNamed;

/**
 * Compression policy for the output PDF files
 *
 * @author Andrea Vacondio
 */
public enum CompressionPolicy implements FriendlyNamed {
    COMPRESS,
    UNCOMPRESS,
    /**
     * Streams filters are left unchanged
     */
    NEUTRAL;

    @Override
    public String getFriendlyName() {
        return this.name().toLowerCase();
    }
}
