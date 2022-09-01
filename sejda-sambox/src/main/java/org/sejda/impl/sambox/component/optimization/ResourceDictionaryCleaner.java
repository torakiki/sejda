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

import static java.util.stream.Collectors.toSet;

import java.util.Map.Entry;
import java.util.Objects;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Supplier;
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
 * {@link ReadOnlyFilteredCOSStream} and any font or extgstate that is not wrapped by a {@link InUseDictionary}. This is the step performed after pages content streams have been
 * already parsed, every used image wrapped by a {@link ReadOnlyFilteredCOSStream} and every used fonts or extgstete wrapped by a {@link InUseDictionary} and placed back to the
 * resources dictionary. This is done in two steps because dictionaries can be shared/inherited by pages so we can't take a single page, identify used images and remove the
 * remaining because that same resource dictionary can be used by other pages.
 * 
 * @author Andrea Vacondio
 *
 */
public class ResourceDictionaryCleaner implements Consumer<PDDocument> {

    private static final Logger LOG = LoggerFactory.getLogger(ResourceDictionaryCleaner.class);

    @Override
    public void accept(PDDocument p) {
        LOG.debug("Cleaning resource dictionaries from unused resources");
        clean(() -> p.getPages().streamNodes());
    }

    public void clean(PDPage page) {
        clean(() -> Stream.of(page.getCOSObject()));
    }

    private void clean(Supplier<Stream<COSDictionary>> nodes) {
        // all the resource dictionaries found at any level in the page tree
        // PAGETREE -> Resources
        Supplier<Stream<COSDictionary>> resources = () -> nodes.get()
                .map(d -> d.getDictionaryObject(COSName.RESOURCES, COSDictionary.class)).filter(Objects::nonNull);

        cleanResources(resources.get().collect(toSet()));

        // all the resource dictionaries found in any xobject form
        // PAGETREE -> Resources -> XObject (Form) -> Resources
        Stream<COSDictionary> formsResources = resources.get()
                .map(d -> d.getDictionaryObject(COSName.XOBJECT, COSDictionary.class)).filter(Objects::nonNull)
                .flatMap(d -> d.getValues().stream()).filter(d -> d.getCOSObject() instanceof COSDictionary)
                .map(d -> (COSDictionary) d.getCOSObject())
                .filter(d -> COSName.FORM.equals(d.getCOSName(COSName.SUBTYPE)))
                .map(d -> d.getDictionaryObject(COSName.RESOURCES, COSDictionary.class)).filter(Objects::nonNull);

        // all the resource dictionaries found in any softmask dictionary stored in any ExtGState (ref. ISO 32000-2:2017 chap. 11.6.5.1 Soft-mask dictionaries)
        // PAGETREE -> Resources -> ExtGState -> SMask -> G -> Resources
        Stream<COSDictionary> softmaskResources = resources.get()
                .map(d -> d.getDictionaryObject(COSName.EXT_G_STATE, COSDictionary.class)).filter(Objects::nonNull)
                .flatMap(d -> d.getValues().stream()).filter(d -> d.getCOSObject() instanceof COSDictionary)
                .map(d -> ((COSDictionary) d.getCOSObject()).getDictionaryObject(COSName.SMASK, COSDictionary.class))
                .filter(Objects::nonNull).map(d -> d.getDictionaryObject(COSName.G, COSDictionary.class))
                .filter(Objects::nonNull).map(d -> d.getDictionaryObject(COSName.RESOURCES, COSDictionary.class))
                .filter(Objects::nonNull);

        // NOTE: we currently don't clean type3 fonts and tiling patterns resources. We hit them so we shouldn't have data loss in case of shared resource dictionaries but we don't
        // clean them atm
        cleanResources(Stream.of(formsResources, softmaskResources).flatMap(s -> s).collect(toSet()));
    }

    private void cleanResources(Set<COSDictionary> resources) {
        LOG.trace("Found {} distinct resource dictionaries to clean", resources.size());
        cleanXObject(resources.stream().map(d -> d.getDictionaryObject(COSName.XOBJECT, COSDictionary.class))
                .filter(Objects::nonNull));
        cleanUnused(resources.stream().map(d -> d.getDictionaryObject(COSName.FONT, COSDictionary.class))
                .filter(Objects::nonNull), COSName.FONT);
        cleanUnused(resources.stream().map(d -> d.getDictionaryObject(COSName.EXT_G_STATE, COSDictionary.class))
                .filter(Objects::nonNull), COSName.EXT_G_STATE);
    }

    private void cleanXObject(Stream<COSDictionary> xobjects) {
        xobjects.forEach(x -> {
            Set<COSName> toRemove = x.entrySet().stream()
                    .filter(e -> !(e.getValue().getCOSObject() instanceof ReadOnlyFilteredCOSStream))
                    .filter(e -> e.getValue().getCOSObject() instanceof COSStream).map(Entry::getKey).collect(toSet());
            LOG.trace("Removing {} unused {} from {}", toRemove.size(), COSName.XOBJECT.getName(), x);
            toRemove.forEach(x::removeItem);
        });
    }

    private void cleanUnused(Stream<COSDictionary> resources, COSName type) {
        resources.forEach(f -> {
            Set<COSName> toRemove = f.entrySet().stream()
                    .filter(e -> !(e.getValue().getCOSObject() instanceof InUseDictionary)).map(Entry::getKey)
                    .collect(toSet());
            LOG.trace("Removing {} unused {}", toRemove.size(), type.getName());
            toRemove.forEach(f::removeItem);
        });
    }

}
