/*
 * Created on 03 feb 2016
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
package org.sejda.impl.sambox.component.optimizaton;

import static org.sejda.util.RequireUtils.requireNotNullArg;

import java.util.Objects;
import java.util.function.Function;

import org.sejda.model.optimization.OptimizationPolicy;
import org.sejda.sambox.cos.COSDictionary;
import org.sejda.sambox.cos.COSName;
import org.sejda.sambox.pdmodel.PDDocument;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Component in charge to decide if a document will likely generate split results needing optimization
 * 
 * @author Andrea Vacondio
 *
 */
public class SplitOptimizationRuler implements Function<PDDocument, Boolean> {

    private static final Logger LOG = LoggerFactory.getLogger(SplitOptimizationRuler.class);

    private OptimizationPolicy policy;

    public SplitOptimizationRuler(OptimizationPolicy policy) {
        requireNotNullArg(policy, "Optimization policy cannot be null");
        this.policy = policy;
    }

    @Override
    public Boolean apply(PDDocument document) {
        if (policy == OptimizationPolicy.YES) {
            return true;
        }
        if (policy == OptimizationPolicy.AUTO) {
            return willNeedOptimization(document);
        }
        return false;
    }

    private boolean willNeedOptimization(PDDocument document) {
        long inheritedImage = coundInheritedImages(document);
        return inheritedImage > 0;
    }

    private long coundInheritedImages(PDDocument document) {
        // we take all the resource dictionaries in non-leaf nodes (i.e. inherited by pages) and count the xobjects of subtype Image, so basically we try to determine if pages are
        // going to inherit images, potentially unused
        long inheritedImage = document.getPages().streamNodes().filter(SplitOptimizationRuler::isPageTreeNode)
                .map(d -> d.getDictionaryObject(COSName.RESOURCES)).filter(d -> d instanceof COSDictionary)
                .map(d -> (COSDictionary) d).map(d -> d.getDictionaryObject(COSName.XOBJECT))
                .filter(d -> d instanceof COSDictionary).map(d -> (COSDictionary) d)
                .flatMap(d -> d.getValues().stream()).filter(d -> d instanceof COSDictionary)
                .map(d -> (COSDictionary) d).map(d -> d.getNameAsString(COSName.SUBTYPE)).filter(Objects::nonNull)
                .filter(COSName.IMAGE.getName()::equals).count();
        LOG.debug("Found {} inherited images potentially unused", inheritedImage);
        return inheritedImage;
    }

    // TODO replace this once we release SAMBox 1.0.0.M21
    private static boolean isPageTreeNode(COSDictionary node) {
        return node.getCOSName(COSName.TYPE) == COSName.PAGES || node.containsKey(COSName.KIDS);
    }
}
