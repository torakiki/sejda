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
package org.sejda.impl.sambox.component.optimization;

import java.util.Objects;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.sejda.impl.sambox.component.ReadOnlyFilteredCOSStream;
import org.sejda.sambox.cos.COSDictionary;
import org.sejda.sambox.cos.COSName;
import org.sejda.sambox.cos.COSStream;
import org.sejda.sambox.pdmodel.PDDocument;
import org.sejda.sambox.pdmodel.PDPage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Component that walks through the page tree, finds resource dictionaries and removes any image xobject (type xobject, subtype image) that is not wrapped by a
 * {@link ReadOnlyFilteredCOSStream} and any font that is not wrapped by a {@link InUseFontDictionary}. This is the step performed after pages content stream have been already
 * parsed, all used image wrapped by a {@link ReadOnlyFilteredCOSStream} and all used fonts wrapped by a {@link InUseFontDictionary} and placed back to the resource dictionary.
 * This is done in two steps because dictionaries can be shared/inherited by pages so we can't take a single page, identify used images and remove the remaining because that same
 * resource dictionary can be used by other pages.
 * 
 * @author Andrea Vacondio
 *
 */
public class ResourceDictionaryCleaner implements Consumer<PDDocument> {

    private static final Logger LOG = LoggerFactory.getLogger(ResourceDictionaryCleaner.class);

    @Override
    public void accept(PDDocument p) {
        LOG.debug("Cleaning resource dictionaries from unused resources");
        clean(p.getPages().streamNodes());
    }

    public void clean(PDPage page) {
        clean(Stream.of(page.getCOSObject()));
    }

    private void clean(Stream<COSDictionary> nodes) {
        // clean all the resource dictionaries found at any level in the page tree
        Set<COSDictionary> resources = nodes.map(d -> d.getDictionaryObject(COSName.RESOURCES, COSDictionary.class))
                .filter(Objects::nonNull).collect(Collectors.toSet());
        cleanResources(resources);
        // clean all the resource dictionaries found in any xobject form
        Set<COSDictionary> formsResources = resources.stream()
                .map(d -> d.getDictionaryObject(COSName.XOBJECT, COSDictionary.class)).filter(Objects::nonNull)
                .flatMap(d -> d.getValues().stream()).filter(d -> d.getCOSObject() instanceof COSDictionary)
                .map(d -> (COSDictionary) d.getCOSObject())
                .filter(d -> COSName.FORM.equals(d.getCOSName(COSName.SUBTYPE)))
                .map(d -> d.getDictionaryObject(COSName.RESOURCES, COSDictionary.class)).filter(Objects::nonNull)
                .collect(Collectors.toSet());
        cleanResources(formsResources);
    }

    private void cleanResources(Set<COSDictionary> resources) {
        cleanXObject(resources.stream().map(d -> d.getDictionaryObject(COSName.XOBJECT, COSDictionary.class))
                .filter(Objects::nonNull));
        cleanFonts(resources.stream().map(d -> d.getDictionaryObject(COSName.FONT, COSDictionary.class))
                .filter(Objects::nonNull));
    }

    private void cleanXObject(Stream<COSDictionary> xobjects) {
        xobjects.forEach(x -> {
            Set<COSName> toRemove = x.entrySet().stream()
                    .filter(e -> !(e.getValue().getCOSObject() instanceof ReadOnlyFilteredCOSStream))
                    .filter(e -> e.getValue().getCOSObject() instanceof COSStream).map(e -> e.getKey())
                    .collect(Collectors.toSet());
            LOG.trace("Removing {} xobjects from {}", toRemove.size(), x);
            toRemove.stream().forEach(x::removeItem);
        });
    }

    private void cleanFonts(Stream<COSDictionary> fonts) {
        fonts.forEach(f -> {
            Set<COSName> toRemove = f.entrySet().stream()
                    .filter(e -> !(e.getValue().getCOSObject() instanceof InUseFontDictionary)).map(e -> e.getKey())
                    .collect(Collectors.toSet());
            LOG.trace("Removing {} fonts from {}", toRemove.size(), f);
            toRemove.stream().forEach(f::removeItem);
        });
    }
}
