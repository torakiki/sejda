/*
 * Copyright 2018 by Eduard Weissmann (edi.weissmann@gmail.com).
 *
 * This file is part of the Sejda source code
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.sejda.impl.sambox.component;

import java.io.IOException;

import org.sejda.sambox.pdmodel.PDDocument;
import org.sejda.sambox.pdmodel.PDPage;
import org.sejda.sambox.pdmodel.common.PDPageLabelRange;
import org.sejda.sambox.pdmodel.common.PDPageLabels;

/**
 * Build a doc on the fly (and test with it)
 */
public class DocBuilder {
    private PDDocument doc = new PDDocument();

    public PDDocument get() {
        return doc;
    }

    public DocBuilder withPages(int numOfPages) {
        for(int i = 0; i < numOfPages; i++) {
            PDPage page = new PDPage();
            doc.addPage(page);
        }

        return this;
    }

    public DocBuilder withPageLabelRange(int index, String style) {
        return withPageLabelRange(index, style, null, null);
    }

    public DocBuilder withPageLabelRange(int index, String style, String prefix) {
        return withPageLabelRange(index, style, prefix, null);
    }


    public DocBuilder withPageLabelRange(int index, String style, String prefix, Integer start) {
        try {
            PDPageLabels labels = doc.getDocumentCatalog().getPageLabels();
            if (labels == null) {
                labels = new PDPageLabels();
            }

            labels.setLabelItem(index, new PDPageLabelRange(style, prefix, start));
            doc.getDocumentCatalog().setPageLabels(labels);
            return this;

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
