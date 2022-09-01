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
package org.sejda.impl.sambox.component.optimization;

import static org.sejda.commons.util.RequireUtils.requireNotNullArg;

import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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
        return hasSharedNameDictionaries(document) || hasInheritedResources(document);
    }

    /**
     * @param document
     * @return true if the document page tree has non leaf nodes with fonts or images resources, inherited by page leaves.
     */
    private boolean hasInheritedResources(PDDocument document) {
        // we take all the resource dictionaries in non-leaf nodes (i.e. inherited by pages) and count fonts and the xobjects of subtype Image, so basically we try to determine if
        // pages are going to inherit images or fonts, potentially unused in which case we want to optimize
        List<COSDictionary> resources = document.getPages().streamNodes().filter(PDPageTree::isPageTreeNode)
                .map(d -> d.getDictionaryObject(COSName.RESOURCES, COSDictionary.class)).filter(Objects::nonNull)
                .distinct().toList();

        long inheritedImage = resources.stream().map(d -> d.getDictionaryObject(COSName.XOBJECT, COSDictionary.class))
                .filter(Objects::nonNull).flatMap(d -> d.getValues().stream()).map(COSBase::getCOSObject)
                .filter(d -> d instanceof COSDictionary).map(d -> (COSDictionary) d)
                .map(d -> d.getNameAsString(COSName.SUBTYPE)).filter(Objects::nonNull)
                .filter(COSName.IMAGE.getName()::equals).count();
        long inheritedFonts = resources.stream().map(d -> d.getDictionaryObject(COSName.FONT, COSDictionary.class))
                .filter(Objects::nonNull).flatMap(d -> d.getValues().stream()).map(COSBase::getCOSObject)
                .filter(d -> d instanceof COSDictionary).count();
        long inheritedExtGState = resources.stream()
                .map(d -> d.getDictionaryObject(COSName.EXT_G_STATE, COSDictionary.class)).filter(Objects::nonNull)
                .flatMap(d -> d.getValues().stream()).map(COSBase::getCOSObject).filter(d -> d instanceof COSDictionary)
                .count();
        LOG.debug("Found {} inherited images, {} inherited fonts and {} inherited graphic states potentially unused",
                inheritedImage, inheritedFonts, inheritedExtGState);
        return (inheritedImage + inheritedFonts + inheritedExtGState) > 0;
    }

    /**
     * 
     * @param document
     * @return true if we detect optimizable name resource dictionaries that are shared among pages. Being shared they likely contains resources used by multiple pages so we need
     *         to optimize in case of split/extract to avoid carrying unused resources to the extracted pages
     */
    private boolean hasSharedNameDictionaries(PDDocument document) {
        // we get from all the pages resource dictionaries, all the optimizable resources name dictionaries (fonts, xobject, extgstate)
        List<COSDictionary> optimizableDictionaries = document.getPages().stream().map(PDPage::getCOSObject)
                .filter(Objects::nonNull).map(d -> d.getDictionaryObject(COSName.RESOURCES, COSDictionary.class))
                .filter(Objects::nonNull).flatMap(
                        d -> Stream.of(d.getDictionaryObject(COSName.EXT_G_STATE, COSDictionary.class),
                                d.getDictionaryObject(COSName.XOBJECT, COSDictionary.class),
                                d.getDictionaryObject(COSName.FONT, COSDictionary.class))).filter(Objects::nonNull)
                .filter(x -> x.size() > 0).toList();
        long distinctFontDictionaries = optimizableDictionaries.stream().distinct().count();
        if (optimizableDictionaries.size() > distinctFontDictionaries) {
            // if the distinct count is different it means one or more name dictionaries is shared among pages
            LOG.debug("Found shared named dictionaries");
            return true;
        }
        return false;
    }
}
