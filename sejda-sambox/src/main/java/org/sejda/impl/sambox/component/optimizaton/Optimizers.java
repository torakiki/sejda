/*
 * Created on 27 gen 2016
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

import java.util.function.Consumer;

import org.sejda.model.optimization.Optimization;
import org.sejda.model.parameter.OptimizeParameters;
import org.sejda.sambox.cos.COSName;
import org.sejda.sambox.pdmodel.PDDocument;
import org.sejda.sambox.pdmodel.PDPage;

/**
 * Factory for document and page optimizers
 * 
 * @author Andrea Vacondio
 *
 */
final class Optimizers {
    private Optimizers() {
        // nothing
    }

    /**
     * Factory method to create a document optimizer based on the given optimization
     * 
     * @param optimization
     * @return the optimizer or null if there is no document level optimization for the given {@link Optimization}
     */
    public static Consumer<PDDocument> documentOptimizer(Optimization optimization) {
        switch (optimization) {
        case DISCARD_METADATA:
            return (d) -> d.getDocumentCatalog().getCOSObject().removeItem(COSName.METADATA);
        case DISCARD_THREADS:
            return (d) -> d.getDocumentCatalog().getCOSObject().removeItem(COSName.THREADS);
        case DISCARD_OUTLINE:
            return (d) -> d.getDocumentCatalog().getCOSObject().removeItem(COSName.OUTLINES);
        case DISCARD_SPIDER_INFO:
            return (d) -> d.getDocumentCatalog().getCOSObject().removeItem(COSName.getPDFName("SpiderInfo"));
        case DISCARD_PIECE_INFO:
            return (d) -> d.getDocumentCatalog().getCOSObject().removeItem(COSName.getPDFName("PieceInfo"));
        case DISCARD_STRUCTURE_TREE:
            return (d) -> d.getDocumentCatalog().getCOSObject().removeItem(COSName.STRUCT_TREE_ROOT);
        default:
            return null;
        }
    }

    public static Consumer<PDPage> pageOptimizer(Optimization optimization, OptimizeParameters parameters) {
        switch (optimization) {
        case DISCARD_PIECE_INFO:
            return (p) -> p.getCOSObject().removeItem(COSName.getPDFName("PieceInfo"));
        case IMAGES:
            return new ImagesOptimizer(parameters);
        default:
            return null;
        }
    }
}
