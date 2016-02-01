/*
 * Created on 01 feb 2016
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

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.util.Arrays;
import java.util.Collections;

import org.junit.Before;
import org.junit.Test;
import org.sejda.model.optimization.Optimization;
import org.sejda.sambox.cos.COSInteger;
import org.sejda.sambox.cos.COSName;
import org.sejda.sambox.pdmodel.PDDocument;
import org.sejda.sambox.pdmodel.common.PDMetadata;
import org.sejda.sambox.pdmodel.documentinterchange.logicalstructure.PDStructureTreeRoot;
import org.sejda.sambox.pdmodel.interactive.documentnavigation.outline.PDDocumentOutline;
import org.sejda.sambox.pdmodel.interactive.pagenavigation.PDThread;

/**
 * @author Andrea Vacondio
 *
 */
public class DocumentOptimizerTest {

    private PDDocument document;

    @Before
    public void setUp() {
        document = new PDDocument();
    }

    @Test
    public void nullContructor() {
        new DocumentOptimizer(null).accept(document);
    }

    @Test
    public void discadMeta() {
        document.getDocumentCatalog().setMetadata(new PDMetadata());
        assertNotNull(document.getDocumentCatalog().getCOSObject().getItem(COSName.METADATA));
        new DocumentOptimizer(Collections.singleton(Optimization.DISCARD_METADATA)).accept(document);
        assertNull(document.getDocumentCatalog().getCOSObject().getItem(COSName.METADATA));
    }

    @Test
    public void discadThreads() {
        document.getDocumentCatalog().setThreads(Arrays.asList(new PDThread()));
        assertNotNull(document.getDocumentCatalog().getCOSObject().getItem(COSName.THREADS));
        new DocumentOptimizer(Collections.singleton(Optimization.DISCARD_THREADS)).accept(document);
        assertNull(document.getDocumentCatalog().getCOSObject().getItem(COSName.THREADS));
    }

    @Test
    public void discadOutline() {
        document.getDocumentCatalog().setDocumentOutline(new PDDocumentOutline());
        assertNotNull(document.getDocumentCatalog().getCOSObject().getItem(COSName.OUTLINES));
        new DocumentOptimizer(Collections.singleton(Optimization.DISCARD_OUTLINE)).accept(document);
        assertNull(document.getDocumentCatalog().getCOSObject().getItem(COSName.OUTLINES));
    }

    @Test
    public void discadSpiderInfo() {
        document.getDocumentCatalog().getCOSObject().setItem(COSName.getPDFName("SpiderInfo"), COSInteger.THREE);
        assertNotNull(document.getDocumentCatalog().getCOSObject().getItem(COSName.getPDFName("SpiderInfo")));
        new DocumentOptimizer(Collections.singleton(Optimization.DISCARD_SPIDER_INFO)).accept(document);
        assertNull(document.getDocumentCatalog().getCOSObject().getItem(COSName.getPDFName("SpiderInfo")));
    }

    @Test
    public void discadPieceInfo() {
        document.getDocumentCatalog().getCOSObject().setItem(COSName.getPDFName("PieceInfo"), COSInteger.THREE);
        assertNotNull(document.getDocumentCatalog().getCOSObject().getItem(COSName.getPDFName("PieceInfo")));
        new DocumentOptimizer(Collections.singleton(Optimization.DISCARD_PIECE_INFO)).accept(document);
        assertNull(document.getDocumentCatalog().getCOSObject().getItem(COSName.getPDFName("PieceInfo")));
    }

    @Test
    public void discadStructTree() {
        document.getDocumentCatalog().setStructureTreeRoot(new PDStructureTreeRoot());
        assertNotNull(document.getDocumentCatalog().getCOSObject().getItem(COSName.STRUCT_TREE_ROOT));
        new DocumentOptimizer(Collections.singleton(Optimization.DISCARD_STRUCTURE_TREE)).accept(document);
        assertNull(document.getDocumentCatalog().getCOSObject().getItem(COSName.STRUCT_TREE_ROOT));
    }

}
