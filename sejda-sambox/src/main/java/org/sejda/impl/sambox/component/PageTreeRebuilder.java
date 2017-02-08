/*
 * Created on 05 feb 2017
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

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

import java.io.IOException;
import java.util.Iterator;

import org.sejda.sambox.cos.COSArray;
import org.sejda.sambox.cos.COSDictionary;
import org.sejda.sambox.cos.COSName;
import org.sejda.sambox.cos.COSStream;
import org.sejda.sambox.pdmodel.PDDocument;
import org.sejda.sambox.pdmodel.PDPage;
import org.sejda.sambox.pdmodel.PDPageTree;
import org.sejda.sambox.pdmodel.common.PDStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Components that rebuilds a PageTree trying to fix nodes issues (missing or wrong Count, missing Type..) and removing invalid pages from the tree
 * 
 * @author Andrea Vacondio
 *
 */
public class PageTreeRebuilder {

    private static final Logger LOG = LoggerFactory.getLogger(PageTreeRebuilder.class);

    private PDDocument document;

    public PageTreeRebuilder(PDDocument document) {
        this.document = document;
    }

    public void rebuild() {
        LOG.info("Rebuilding page tree");
        document.getPages().streamNodes().forEach(n -> {
            if (PDPageTree.isPageTreeNode(n)) {
                // we reset non leaf nodes so Count and Kids are filled later. This will fix possibly broken Count and Kids.
                n.setItem(COSName.TYPE, COSName.PAGES);
                n.setInt(COSName.COUNT, 0);
                n.setItem(COSName.KIDS, new COSArray());
                COSDictionary parent = getAndRepairParent(n);
                if (nonNull(parent)) {
                    parent.getDictionaryObject(COSName.KIDS, COSArray.class).add(n);
                }
            } else {
                if (isPage(n) && canDecodeContents(n)) {
                    n.setItem(COSName.TYPE, COSName.PAGE);
                    COSDictionary parent = getAndRepairParent(n);
                    parent.getDictionaryObject(COSName.KIDS, COSArray.class).add(n);
                    // update ancestor counts
                    while (nonNull(parent)) {
                        parent.setInt(COSName.COUNT, parent.getInt(COSName.COUNT) + 1);
                        parent = getAndRepairParent(parent);
                    }
                }
            }
        });
    }

    static boolean isPage(COSDictionary page) {
        if (isNull(page.getCOSName(COSName.TYPE))) {
            LOG.warn("Missing required 'Page' type for page");
            // it's missing the type, we assume it's a page if it has the required Parent item
            return nonNull(getAndRepairParent(page));
        }
        return COSName.PAGE.equals(page.getCOSName(COSName.TYPE));
    }

    static boolean canDecodeContents(COSDictionary page) {
        try {
            Iterator<PDStream> iter = new PDPage(page).getContentStreams();
            while (iter.hasNext()) {
                COSStream stream = iter.next().getCOSObject();
                stream.getUnfilteredStream();
                stream.unDecode();
            }
            return true;
        } catch (IOException e) {
            LOG.warn("Cannot decode page stream, skipping page.", e);
        }
        return false;
    }

    static COSDictionary getAndRepairParent(COSDictionary child) {
        COSDictionary parent = child.getDictionaryObject(COSName.PARENT, COSDictionary.class);
        if (isNull(parent)) {
            parent = child.getDictionaryObject(COSName.P, COSDictionary.class);
            if (nonNull(parent)) {
                child.removeItem(COSName.P);
                child.setItem(COSName.PARENT, parent);
            }
        }
        return parent;
    }

}
