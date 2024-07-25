/*
 * Created on 23/07/24
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

import org.sejda.model.exception.TaskException;
import org.sejda.sambox.cos.COSDictionary;
import org.sejda.sambox.cos.COSName;
import org.sejda.sambox.pdmodel.PDPage;

import java.io.IOException;

import static java.util.Objects.nonNull;

/**
 * @author Andrea Vacondio
 */
public class ThumbnailImagePageRule extends BaseRule<PDPage, TaskException> {

    public ThumbnailImagePageRule(ConversionContext conversionContext) {
        super(conversionContext);
    }

    @Override
    public void accept(PDPage page) throws TaskException {
        var thumb = page.getCOSObject().getDictionaryObject(COSName.getPDFName("Thumb"), COSDictionary.class);
        if (nonNull(thumb)) {
            var csResources = page.getResources().getCOSObject()
                    .computeIfAbsent(COSName.COLORSPACE, k -> new COSDictionary(), COSDictionary.class);
            try {
                conversionContext().maybeAddDefaultColorSpaceFor(thumb.getDictionaryObject(COSName.CS), csResources);
            } catch (IOException e) {
                throw new TaskException(e);
            }
        }
    }
}
