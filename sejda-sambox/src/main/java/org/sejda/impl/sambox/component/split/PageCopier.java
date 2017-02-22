/*
 * Created on 27 giu 2016
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
package org.sejda.impl.sambox.component.split;

import static java.util.Objects.nonNull;
import static java.util.Optional.ofNullable;

import java.util.Objects;

import org.sejda.impl.sambox.component.optimization.ResourceDictionaryCleaner;
import org.sejda.impl.sambox.component.optimization.ResourcesHitter;
import org.sejda.sambox.cos.COSArray;
import org.sejda.sambox.cos.COSBase;
import org.sejda.sambox.cos.COSDictionary;
import org.sejda.sambox.cos.COSName;
import org.sejda.sambox.pdmodel.PDPage;
import org.sejda.sambox.pdmodel.PDResources;

/**
 * Component providing copies of pages that can be fed to the ExistingPagesSizePredictor
 * 
 * @author Andrea Vacondio
 */
class PageCopier {
    private boolean optimize;
    private ResourcesHitter hitter = new ResourcesHitter();
    private ResourceDictionaryCleaner cleaner = new ResourceDictionaryCleaner();

    public PageCopier(boolean optimize) {
        this.optimize = optimize;
    }

    public PDPage copyOf(PDPage page) {
        PDPage copy = new PDPage(page.getCOSObject().duplicate());
        copy.setCropBox(page.getCropBox());
        copy.setMediaBox(page.getMediaBox());
        copy.setResources(page.getResources());
        copy.setRotation(page.getRotation());
        // we remove thread beads possibly leaking into page tree
        copy.getCOSObject().removeItem(COSName.B);
        COSArray annots = page.getCOSObject().getDictionaryObject(COSName.ANNOTS, COSArray.class);
        if (nonNull(annots)) {
            // we create an array where annotations are a copy of the original but without /P or /Dest possibly leaking into the page tree
            COSArray cleanedAnnotationsCopy = new COSArray();
            annots.stream().map(COSBase::getCOSObject).filter(d -> d instanceof COSDictionary)
                    .map(d -> (COSDictionary) d).map(COSDictionary::duplicate).forEach(a -> {
                        a.removeItem(COSName.P);
                        a.removeItem(COSName.DEST);
                        // Popup parent can leak into the page tree
                        a.removeItem(COSName.getPDFName("Popup"));
                        a.removeItem(COSName.PARENT);
                        // remove the action if it has a destination (potentially a GoTo page destination leaking into the page tree)
                        if (ofNullable(a.getDictionaryObject(COSName.A, COSDictionary.class))
                                .map(d -> d.containsKey(COSName.D)).orElse(false)) {
                            a.removeItem(COSName.A);
                        }
                        cleanedAnnotationsCopy.add(a);
                    });
            copy.getCOSObject().setItem(COSName.ANNOTS, cleanedAnnotationsCopy);
        }
        if (optimize) {
            // each page must have it's own resource dic and it's own xobject and font name dic
            // so we don't optimize shared resource dic or xobjects/fonts name dictionaries
            COSDictionary resources = ofNullable(copy.getResources().getCOSObject()).map(COSDictionary::duplicate)
                    .orElseGet(COSDictionary::new);
            // resources are cached in the PDPage so make sure they are replaced
            copy.setResources(new PDResources(resources));
            ofNullable(resources.getDictionaryObject(COSName.XOBJECT, COSDictionary.class)).filter(Objects::nonNull)
                    .map(COSDictionary::duplicate).ifPresent(d -> resources.setItem(COSName.XOBJECT, d));
            ofNullable(resources.getDictionaryObject(COSName.FONT, COSDictionary.class)).filter(Objects::nonNull)
                    .map(COSDictionary::duplicate).ifPresent(d -> resources.setItem(COSName.FONT, d));
            hitter.accept(copy);
            cleaner.clean(copy);
        }
        return copy;
    }
}
