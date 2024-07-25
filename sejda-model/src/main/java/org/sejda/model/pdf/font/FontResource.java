/*
 * Created on 20 feb 2016
 * Copyright 2015 Sober Lemur S.r.l. and Sejda BV.
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
package org.sejda.model.pdf.font;

import java.io.InputStream;

/**
 * @author Andrea Vacondio
 */
public interface FontResource {
    /**
     * @return Unique identifier
     */
    String getResource();

    /**
     * @return the stream where the font is read from
     */
    InputStream getFontStream();

    /**
     * @return a priority telling which font to load first. Negatives have higher priority.
     */
    default Integer priority() {
        return 0;
    }
}
