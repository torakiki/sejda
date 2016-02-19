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

import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.sejda.model.optimization.OptimizationPolicy;
import org.sejda.sambox.cos.COSBase;
import org.sejda.sambox.cos.COSDictionary;
import org.sejda.sambox.cos.COSName;
import org.sejda.sambox.pdmodel.PDDocument;
import org.sejda.sambox.pdmodel.PDPage;
import org.sejda.sambox.pdmodel.PDPageTree;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Component in charge to decide if a document will likely generate split/extract results needing optimization
 * 
 * @author Andrea Vacondio
 *
 */
public class OptimizationRuler implements Function<PDDocument, Boolean> {

    private static final Logger LOG = LoggerFactory.getLogger(OptimizationRuler.class);

    private OptimizationPolicy policy;

    public OptimizationRuler(OptimizationPolicy policy) {
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
        return hasSharedXObjectDictionaries(document) || hasInheritedImages(document);
    }

    private boolean hasInheritedImages(PDDocument document) {
        // we take all the resource dictionaries in non-leaf nodes (i.e. inherited by pages) and count the xobjects of subtype Image, so basically we try to determine if pages are
        // going to inherit images, potentially unused in which case we want to optimize
        long inheritedImage = document.getPages().streamNodes().filter(PDPageTree::isPageTreeNode)
                .map(d -> d.getDictionaryObject(COSName.RESOURCES)).filter(d -> d instanceof COSDictionary)
                .map(d -> (COSDictionary) d).map(d -> d.getDictionaryObject(COSName.XOBJECT))
                .filter(d -> d instanceof COSDictionary).map(d -> (COSDictionary) d)
                .flatMap(d -> d.getValues().stream()).map(COSBase::getCOSObject).filter(d -> d instanceof COSDictionary)
                .map(d -> (COSDictionary) d).map(d -> d.getNameAsString(COSName.SUBTYPE)).filter(Objects::nonNull)
                .filter(COSName.IMAGE.getName()::equals).count();
        LOG.debug("Found {} inherited images potentially unused", inheritedImage);
        return inheritedImage > 0;
    }

    private boolean hasSharedXObjectDictionaries(PDDocument document) {
        // we get from all the pages resource dictionaries, all the xobject name dictionaries containing images
        List<COSDictionary> xobjectsDictionaries = document.getPages().stream().map(PDPage::getCOSObject)
                .filter(Objects::nonNull).map(d -> d.getDictionaryObject(COSName.RESOURCES))
                .filter(d -> d instanceof COSDictionary).map(d -> (COSDictionary) d)
                .map(d -> d.getDictionaryObject(COSName.XOBJECT)).filter(d -> d instanceof COSDictionary)
                .map(d -> (COSDictionary) d).filter(x -> {
                    return x.getValues().stream().map(COSBase::getCOSObject).filter(v -> v instanceof COSDictionary)
                            .map(v -> (COSDictionary) v).map(v -> v.getNameAsString(COSName.SUBTYPE))
                            .filter(Objects::nonNull).filter(COSName.IMAGE.getName()::equals).count() > 0;
                }).collect(Collectors.toList());
        long distinctXobjectsDictionaries = xobjectsDictionaries.stream().distinct().count();
        if (xobjectsDictionaries.size() > distinctXobjectsDictionaries) {
            // if the distinct count is different it means one or more xobject name dictionary is shared among some pages so it likely contains images used by multiple pages so we
            // optimize
            LOG.debug("Found shared XObject dictionary containing image resouces");
            return true;
        }
        return false;
    }
}
