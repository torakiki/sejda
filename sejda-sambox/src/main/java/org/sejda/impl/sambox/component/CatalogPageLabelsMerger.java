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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.sejda.impl.sambox.util.PageLabelUtils;
import org.sejda.model.outline.CatalogPageLabelsPolicy;
import org.sejda.sambox.pdmodel.PDDocument;
import org.sejda.sambox.pdmodel.common.PDPageLabelRange;
import org.sejda.sambox.pdmodel.common.PDPageLabels;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Merges multiple /Catalog /PageLabels definitions, from multiple docs, into one.
 */
public class CatalogPageLabelsMerger {

    private static final Logger LOG = LoggerFactory.getLogger(CatalogPageLabelsMerger.class);

    private int totalPages = 0;
    private PDPageLabels mergedPageLabels = new PDPageLabels();
    private final CatalogPageLabelsPolicy policy;

    public CatalogPageLabelsMerger(CatalogPageLabelsPolicy policy) {
        this.policy = policy;

        if (policy == CatalogPageLabelsPolicy.DISCARD) {
            mergedPageLabels = null;
        }
    }

    public void add(PDDocument doc, Set<Integer> pagesToImport) {
        if (policy == CatalogPageLabelsPolicy.DISCARD) {
            return;
        }

        try {
            PDPageLabels docLabels = doc.getDocumentCatalog().getPageLabels();
            if (docLabels == null) {
                docLabels = new PDPageLabels();
            }

            if (pagesToImport.size() < doc.getNumberOfPages()) {
                // not all pages are being imported
                // first update doc's page labels and remove pages not being imported
                List<Integer> pagesToRemove = computePagesToRemove(doc, pagesToImport);
                docLabels = PageLabelUtils.removePages(docLabels, pagesToRemove, doc.getNumberOfPages());
            }

            for (Map.Entry<Integer, PDPageLabelRange> entry : docLabels.getLabels().entrySet()) {
                PDPageLabelRange range = entry.getValue();

                // the page index in the original doc
                int pageIndex = entry.getKey();
                // new page index: old page index + number of pages we merged so far (from other docs)
                int newPageIndex = pageIndex + totalPages;

                // Since merge changes logical page numbers we ignore existing start from and don't copy over
                if(range.hasStart()) {
                    range = new PDPageLabelRange(range.getStyle(), range.getPrefix(), null);
                }

                mergedPageLabels.setLabelItem(newPageIndex, range);
            }

        } catch (Exception ex) {
            LOG.warn("An error occurred retrieving /PageLabels of document {}, will not be merged", doc);
        } finally {
            // always advance the total number of pages
            totalPages += pagesToImport.size();
        }
    }

    private static List<Integer> computePagesToRemove(PDDocument doc, Set<Integer> pagesToImport) {
        if (doc.getNumberOfPages() == pagesToImport.size()) {
            return new ArrayList<>();
        }

        List<Integer> pagesToRemove = new ArrayList<>();
        for (int i = 1; i <= doc.getNumberOfPages(); i++) {
            if (!pagesToImport.contains(i)) {
                pagesToRemove.add(i);
            }
        }

        return pagesToRemove;
    }

    public boolean hasPageLabels() {
        return mergedPageLabels != null;
    }

    public PDPageLabels getMergedPageLabels() {
        return mergedPageLabels;
    }
}
