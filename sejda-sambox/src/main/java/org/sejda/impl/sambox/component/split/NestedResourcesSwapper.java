/*
 * Created on 18/11/26
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
package org.sejda.impl.sambox.component.split;

import org.sejda.impl.sambox.component.optimization.ResourceDictionaryCleaner;
import org.sejda.sambox.cos.COSBase;
import org.sejda.sambox.cos.COSDictionary;
import org.sejda.sambox.cos.COSName;
import org.sejda.sambox.pdmodel.PDPage;

import java.util.IdentityHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Stream;

import static java.util.Objects.nonNull;
import static java.util.Optional.ofNullable;
import static org.sejda.impl.sambox.component.optimization.NameResourcesDuplicator.duplicateNameDictionaries;

/**
 * Temporarily replaces the resource dictionaries of the forms and soft mask groups found in the page resources with copies, and puts the original
 * ones back on close. Together with the page one, these are the resource dictionaries pruned by the {@link ResourceDictionaryCleaner}, so a page
 * copy can be hit and cleaned without pruning resource dictionaries of the original document that might be shared with other pages. Other resource
 * dictionaries (nested forms, Type3 fonts, patterns...) are not pruned, so they are left untouched, even when they are the same dictionary of one of
 * the swapped ones.
 *
 * @author Andrea Vacondio
 */
class NestedResourcesSwapper implements AutoCloseable {

    private final Map<COSDictionary, COSBase> ownersToOriginalResources = new IdentityHashMap<>();
    private final Map<COSDictionary, COSDictionary> originalResourcesToCopied = new IdentityHashMap<>();

    /**
     * Replaces the resource dictionaries of the forms and soft mask groups found in the page resources with copies having their own XObject, Font,
     * ExtGState and Pattern dictionaries. Owners sharing a resource dictionary share its copy, the same way the PagesExtractor prunes the shared
     * original.
     */
    public void swap(PDPage page) {
        nestedResourcesOwners(page).forEach(
                owner -> ownersToOriginalResources.computeIfAbsent(owner, this::replaceResourcesWithCopy));
    }

    /**
     * Puts the original resource dictionaries back, as they were in their owners
     */
    @Override
    public void close() {
        ownersToOriginalResources.forEach((owner, resources) -> owner.setItem(COSName.RESOURCES, resources));
    }

    /**
     * @return a stream of all the forms and SMasks G streams with a dictionary Resource key
     */
    private static Stream<COSDictionary> nestedResourcesOwners(PDPage page) {
        var resources = page.getResources().getCOSObject();
        // PAGETREE -> Resources -> XObject (Form)
        Stream<COSDictionary> forms = values(
                resources.getDictionaryObject(COSName.XOBJECT, COSDictionary.class)).filter(
                d -> COSName.FORM.equals(d.getCOSName(COSName.SUBTYPE)));
        // PAGETREE -> Resources -> ExtGState -> SMask -> G
        Stream<COSDictionary> softMaskGroups = values(
                resources.getDictionaryObject(COSName.EXT_G_STATE, COSDictionary.class)).map(
                        gs -> gs.getDictionaryObject(COSName.SMASK, COSDictionary.class)).filter(Objects::nonNull)
                .map(softMask -> softMask.getDictionaryObject(COSName.G, COSDictionary.class)).filter(Objects::nonNull);
        return Stream.concat(forms, softMaskGroups)
                .filter(d -> nonNull(d.getDictionaryObject(COSName.RESOURCES, COSDictionary.class)));
    }

    private static Stream<COSDictionary> values(COSDictionary dictionary) {
        return ofNullable(dictionary).stream().flatMap(d -> d.getValues().stream()).map(COSBase::getCOSObject)
                .filter(COSDictionary.class::isInstance).map(COSDictionary.class::cast);
    }

    /**
     * @return the original resources item, as it was in the owner, so that it can be restored
     */
    private COSBase replaceResourcesWithCopy(COSDictionary owner) {
        // we keep the raw item and not the resolved dictionary. When it's an indirect reference, the resources are reloaded untouched once the
        // current output is written, while the resolved dictionary would leave the owner with the resources pruned for the current output
        COSBase original = owner.getItem(COSName.RESOURCES);
        owner.setItem(COSName.RESOURCES,
                originalResourcesToCopied.computeIfAbsent(owner.getDictionaryObject(COSName.RESOURCES, COSDictionary.class),
                        NestedResourcesSwapper::copyWithOwnNameDictionaries));
        return original;
    }

    private static COSDictionary copyWithOwnNameDictionaries(COSDictionary resources) {
        COSDictionary copy = resources.duplicate();
        duplicateNameDictionaries(copy);
        return copy;
    }
}
