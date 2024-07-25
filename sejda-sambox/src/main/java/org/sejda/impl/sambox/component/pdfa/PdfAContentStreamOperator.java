/*
 * Created on 16 Jul 2024
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
package org.sejda.impl.sambox.component.pdfa;

import org.sejda.sambox.contentstream.operator.OperatorProcessor;

/**
 * An operator aware of the conversion context
 *
 * @author Andrea Vacondio
 */
abstract class PdfAContentStreamOperator extends OperatorProcessor {

    private ConversionContext conversionContext;

    public ConversionContext conversionContext() {
        return conversionContext;
    }

    public void setConversionContext(ConversionContext conversionContext) {
        this.conversionContext = conversionContext;
    }

}
