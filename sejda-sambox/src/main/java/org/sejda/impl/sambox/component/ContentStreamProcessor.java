/*
 * Created on 27 dic 2017
 * Copyright 2017 by Andrea Vacondio (andrea.vacondio@gmail.com).
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

import static java.util.Optional.ofNullable;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import org.sejda.sambox.contentstream.PDFStreamEngine;
import org.sejda.sambox.cos.COSNull;
import org.sejda.sambox.pdmodel.PDPage;
import org.sejda.sambox.pdmodel.interactive.annotation.PDAnnotation;
import org.sejda.sambox.pdmodel.interactive.annotation.PDAppearanceEntry;
import org.sejda.sambox.pdmodel.interactive.annotation.PDAppearanceStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Component that parses the page content steam and the page annotations appearance streams
 *
 * @author Andrea Vacondio
 *
 */
public class ContentStreamProcessor extends PDFStreamEngine implements Consumer<PDPage> {

    private static final Logger LOG = LoggerFactory.getLogger(ContentStreamProcessor.class);

    private void processAnnotation(PDAnnotation annotation) throws IOException {
        // we want to parse all states for all the appearance streams (N, D, R), not just the normal
        List<PDAppearanceEntry> appreaceEntries = ofNullable(annotation.getAppearance())
                .map(d -> d.getCOSObject().getValues()).filter(Objects::nonNull).orElse(Collections.emptyList())
                .stream().map(a -> a.getCOSObject()).filter(a -> !(a instanceof COSNull)).map(PDAppearanceEntry::new)
                .collect(Collectors.toList());
        for (PDAppearanceEntry entry : appreaceEntries) {
            if (entry.isStream()) {
                processStream(entry.getAppearanceStream());
            } else {
                for (PDAppearanceStream stream : entry.getSubDictionary().values()) {
                    // TODO investigate this case with named dictionary
                    if(stream != null) {
                        processStream(stream);
                    }
                }
            }
        }
    }

    @Override
    public void accept(PDPage page) {
        try {
            this.processPage(page);
            for (PDAnnotation annotation : page.getAnnotations()) {
                processAnnotation(annotation);
            }
        } catch (IOException e) {
            LOG.warn("Failed parse page, skipping and continuing with next.", e);
        }
    }
}
