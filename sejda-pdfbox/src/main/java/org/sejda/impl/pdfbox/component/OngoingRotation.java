/*
 * Created on Jul 2, 2011
 * Copyright 2010 by Nero Couvalli (angelthepunisher@gmail.com).
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
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.sejda.impl.pdfbox.component;

import org.apache.pdfbox.pdmodel.PDDocument;

/**
 * Identifies an ongoing rotation exposing methods to finalize it.
 * 
 * @author Nero Couvalli
 * 
 */

public interface OngoingRotation {

    /**
     * applies the rotation to the input document
     * 
     * @param document
     */
    void to(PDDocument document);
}