/*
 * Created on 29 gen 2016
 * Copyright 2015 by Andrea Vacondio (andrea.vacondio@gmail.com).
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
package org.sejda.impl.sambox.component.optimizaton;

import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.sejda.impl.sambox.component.ReadOnlyFilteredCOSStream;
import org.sejda.sambox.cos.COSDictionary;
import org.sejda.sambox.cos.COSName;
import org.sejda.sambox.cos.COSStream;
import org.sejda.sambox.pdmodel.PDDocument;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Component that walks through the page tree, finds resource dictionaries and removes any image xobject (type xobject, subtype image) that is not wrapped by a
 * {@link ReadOnlyFilteredCOSStream}. This is the step performed after pages content stream have been already parsed and all used image wrapped by a
 * {@link ReadOnlyFilteredCOSStream} and placed back to the resource dictionary. This is done in two steps because dictionaries can be shared/inherited by pages so we can't take a
 * single page, identify used images and remove the remaining because that same resource dictionary can be used by other pages.
 * 
 * @author Andrea Vacondio
 *
 */
public class ResourceDictionaryCleaner implements Consumer<PDDocument> {

    private static final Logger LOG = LoggerFactory.getLogger(ResourceDictionaryCleaner.class);

    @Override
    public void accept(PDDocument p) {
        LOG.debug("Cleaning resource dictionaries from unused external objects");
        Stream<COSDictionary> xobjects = p.getPages().streamNodes().map(d -> d.getDictionaryObject(COSName.RESOURCES))
                .filter(d -> d instanceof COSDictionary).map(d -> (COSDictionary) d)
                .map(d -> d.getDictionaryObject(COSName.XOBJECT)).filter(d -> d instanceof COSDictionary)
                .map(d -> (COSDictionary) d);
        xobjects.forEach(x -> {
            Set<COSName> toRemove = x.entrySet().stream()
                    .filter(e -> !(e.getValue().getCOSObject() instanceof ReadOnlyFilteredCOSStream))
                    .filter(e -> e.getValue().getCOSObject() instanceof COSStream)
                    .filter(e -> ((COSStream) e.getValue().getCOSObject()).getItem(COSName.SUBTYPE)
                            .equals(COSName.IMAGE))
                    .map(e -> e.getKey()).collect(Collectors.toSet());
            toRemove.stream().forEach(x::removeItem);
        });
    }
}
