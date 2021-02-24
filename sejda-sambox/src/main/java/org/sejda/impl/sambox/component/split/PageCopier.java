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

import java.io.IOException;
import java.util.function.Consumer;

import org.sejda.impl.sambox.component.optimization.NameResourcesDuplicator;
import org.sejda.impl.sambox.component.optimization.ResourceDictionaryCleaner;
import org.sejda.impl.sambox.component.optimization.ResourcesHitter;
import org.sejda.sambox.cos.COSArray;
import org.sejda.sambox.cos.COSBase;
import org.sejda.sambox.cos.COSDictionary;
import org.sejda.sambox.cos.COSName;
import org.sejda.sambox.cos.COSStream;
import org.sejda.sambox.output.ExistingPagesSizePredictor;
import org.sejda.sambox.pdmodel.PDPage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Component providing copies of pages that can be fed to the ExistingPagesSizePredictor
 * 
 * @author Andrea Vacondio
 */
class PageCopier {

    private static final Logger LOG = LoggerFactory.getLogger(PageCopier.class);

    private boolean optimize;
    private Consumer<PDPage> hitAndClean = new NameResourcesDuplicator().andThen(new ResourcesHitter())
            .andThen(new ResourceDictionaryCleaner()::clean);

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
            hitAndClean.accept(copy);
        }
        duplicatePageStreams(page, copy);
        copy.sanitizeDictionary();
        return copy;
    }

    private void duplicatePageStreams(PDPage page, PDPage copy) {
        // we duplicate the streams so we can sanitize them, to replicate the task behavior
        COSStream stream = page.getCOSObject().getDictionaryObject(COSName.CONTENTS, COSStream.class);
        if (nonNull(stream)) {
            copy.getCOSObject().setItem(COSName.CONTENTS, new MockPageStream(stream));
        } else {
            COSArray streams = page.getCOSObject().getDictionaryObject(COSName.CONTENTS, COSArray.class);
            if (nonNull(streams)) {
                COSArray streamsCopy = new COSArray();
                streams.stream().filter(s -> s instanceof COSStream).map(COSStream.class::cast).map(MockPageStream::new)
                        .forEach(streamsCopy::add);
                copy.getCOSObject().setItem(COSName.CONTENTS, streams);
            }
        }
    }

    /**
     * Mock stream that retains the dictionary and the stream length info to be consumed by the {@link ExistingPagesSizePredictor}
     * 
     * @author Andrea Vacondio
     *
     */
    private class MockPageStream extends COSStream {

        private long length = 0;

        private MockPageStream(COSStream original) {
            super(original.duplicate());
            try {
                length = original.getFilteredLength();
            } catch (IOException e) {
                LOG.error("An error occurred while calculating the COSStream length", e);
            }
        }

        @Override
        public long getFilteredLength() throws IOException {
            return length;
        }

    }
}
