/*
 * Created on 22/07/24
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

import org.sejda.sambox.contentstream.operator.Operator;
import org.sejda.sambox.cos.COSBase;
import org.sejda.sambox.cos.COSDictionary;
import org.sejda.sambox.cos.COSName;

import java.io.IOException;
import java.util.List;

import static org.sejda.sambox.contentstream.operator.OperatorName.NON_STROKING_CMYK;
import static org.sejda.sambox.contentstream.operator.OperatorName.NON_STROKING_RGB;
import static org.sejda.sambox.contentstream.operator.OperatorName.STROKING_COLOR_CMYK;
import static org.sejda.sambox.contentstream.operator.OperatorName.STROKING_COLOR_RGB;

/**
 * Operator that sets a default color space if needed
 *
 * @author Andrea Vacondio
 */
class BaseSetDeviceColorSpace extends PdfAContentStreamOperator {
    private final String operator;
    private final COSName cs;

    public BaseSetDeviceColorSpace(COSName cs, String operator) {
        this.cs = cs;
        this.operator = operator;
    }

    @Override
    public void process(Operator operator, List<COSBase> operands) throws IOException {
        conversionContext().maybeAddDefaultColorSpaceFor(cs, csResources());
    }

    private COSDictionary csResources() {
        return getContext().getResources().getCOSObject()
                .computeIfAbsent(COSName.COLORSPACE, k -> new COSDictionary(), COSDictionary.class);
    }

    @Override
    public String getName() {
        return operator;
    }

    static PdfAContentStreamOperator strokingRGB() {
        return new BaseSetDeviceColorSpace(COSName.DEVICERGB, STROKING_COLOR_RGB);
    }

    static PdfAContentStreamOperator nonStrokingRGB() {
        return new BaseSetDeviceColorSpace(COSName.DEVICERGB, NON_STROKING_RGB);
    }

    static PdfAContentStreamOperator strokingCMYK() {
        return new BaseSetDeviceColorSpace(COSName.DEVICECMYK, STROKING_COLOR_CMYK);
    }

    static PdfAContentStreamOperator nonStrokingCMYK() {
        return new BaseSetDeviceColorSpace(COSName.DEVICECMYK, NON_STROKING_CMYK);
    }
}
