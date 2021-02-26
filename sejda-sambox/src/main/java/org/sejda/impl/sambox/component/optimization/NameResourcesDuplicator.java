/*
 * Created on 24 feb 2021
 * Copyright 2019 Sober Lemur S.a.s. di Vacondio Andrea and Sejda BV
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

import static java.util.Optional.ofNullable;

import java.util.function.Consumer;

import org.sejda.sambox.cos.COSDictionary;
import org.sejda.sambox.cos.COSName;
import org.sejda.sambox.pdmodel.PDPage;
import org.sejda.sambox.pdmodel.PDResources;

/**
 * Component that duplicates parts of the page resource dictionary that are relevant for the hit and clean process. The idea is that we want to hit and clean a resource dictionary
 * that is relevant only for the given page, and not potentially shared with other pages (e.g. if page is a copy we don't want to clean the resource dictionary of the original
 * page).
 * 
 * I'm not sure this is needed. Resource dictionaries may come from other places (XForm, ExtGState softmasks..) so we are already hitting/cleaning shared resource dictionaries. The
 * reason why the whole hit/clean process works is because the ExistingIndirectCOSObject holding the resources is unloaded from the LazyIndirectObjectsProvider once the file is
 * written, this way the next cycle reloads the resources getting a brand new resource dictionary, untouched by the previous hit/clean process.
 * 
 * @author Andrea Vacondio
 *
 */
public class NameResourcesDuplicator implements Consumer<PDPage> {

    @Override
    public void accept(PDPage page) {
        // each page must have it's own resource dic and it's own xobject and font name dic
        // so we don't optimize shared resource dic or xobjects/fonts name dictionaries
        COSDictionary resources = ofNullable(page.getResources().getCOSObject()).map(COSDictionary::duplicate)
                .orElseGet(COSDictionary::new);
        // resources are cached in the PDPage so make sure they are replaced
        page.setResources(new PDResources(resources));
        ofNullable(resources.getDictionaryObject(COSName.XOBJECT, COSDictionary.class)).map(COSDictionary::duplicate)
                .ifPresent(d -> resources.setItem(COSName.XOBJECT, d));
        ofNullable(resources.getDictionaryObject(COSName.FONT, COSDictionary.class)).map(COSDictionary::duplicate)
                .ifPresent(d -> resources.setItem(COSName.FONT, d));
        ofNullable(resources.getDictionaryObject(COSName.EXT_G_STATE, COSDictionary.class))
                .map(COSDictionary::duplicate).ifPresent(d -> resources.setItem(COSName.EXT_G_STATE, d));
    }

}
