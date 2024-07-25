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
package org.sejda.impl.sambox.component;

import org.apache.commons.lang3.function.FailableConsumer;
import org.sejda.sambox.contentstream.PDFStreamEngine;
import org.sejda.sambox.cos.COSBase;
import org.sejda.sambox.cos.COSNull;
import org.sejda.sambox.pdmodel.PDPage;
import org.sejda.sambox.pdmodel.interactive.annotation.PDAnnotation;
import org.sejda.sambox.pdmodel.interactive.annotation.PDAppearanceEntry;
import org.sejda.sambox.pdmodel.interactive.annotation.PDAppearanceStream;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

import static java.util.Optional.ofNullable;

/**
 * @author Andrea Vacondio
 */
public class FailableContentStreamProcessor extends PDFStreamEngine implements FailableConsumer<PDPage, IOException> {

    private void processAnnotation(PDAnnotation annotation) throws IOException {
        // we want to parse all states for all the appearance streams (N, D, R), not just the normal
        List<PDAppearanceEntry> appreaceEntries = ofNullable(annotation.getAppearance()).map(
                        d -> d.getCOSObject().getValues()).orElse(Collections.emptyList()).stream().map(COSBase::getCOSObject)
                .filter(a -> !(a instanceof COSNull)).map(PDAppearanceEntry::new).toList();
        for (PDAppearanceEntry entry : appreaceEntries) {
            if (entry.isStream()) {
                processStream(entry.getAppearanceStream());
            } else {
                for (PDAppearanceStream stream : entry.getSubDictionary().values()) {
                    if (stream != null) {
                        processStream(stream);
                    }
                }
            }
        }
    }

    @Override
    public void accept(PDPage page) throws IOException {
        this.processPage(page);
        for (PDAnnotation annotation : page.getAnnotations()) {
            processAnnotation(annotation);
        }
    }
}