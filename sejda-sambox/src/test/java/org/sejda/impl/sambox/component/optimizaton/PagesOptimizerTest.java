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

import org.junit.Before;
import org.junit.Test;
import org.sejda.model.optimization.Optimization;
import org.sejda.model.parameter.OptimizeParameters;
import org.sejda.sambox.cos.COSDictionary;
import org.sejda.sambox.cos.COSInteger;
import org.sejda.sambox.cos.COSName;
import org.sejda.sambox.pdmodel.PDPage;
import org.sejda.sambox.pdmodel.PDResources;
import org.sejda.sambox.pdmodel.documentinterchange.markedcontent.PDPropertyList;

public class PagesOptimizerTest {
    private PDPage page;
    private OptimizeParameters parameters;

    @Before
    public void setUp() {
        parameters = new OptimizeParameters();
        page = new PDPage();
    }

    @Test
    public void nullContructor() {
        new PagesOptimizer(null).accept(page);
    }

    @Test
    public void discadPieceInfo() {
        page.getCOSObject().setItem(COSName.getPDFName("PieceInfo"), COSInteger.THREE);
        assertNotNull(page.getCOSObject().getItem(COSName.getPDFName("PieceInfo")));
        parameters.addOptimization(Optimization.DISCARD_PIECE_INFO);
        new PagesOptimizer(parameters).accept(page);
        assertNull(page.getCOSObject().getItem(COSName.getPDFName("PieceInfo")));
    }

    @Test
    public void discadThumbs() {
        page.getCOSObject().setItem(COSName.getPDFName("Thumb"), COSInteger.THREE);
        assertNotNull(page.getCOSObject().getItem(COSName.getPDFName("Thumb")));
        parameters.addOptimization(Optimization.DISCARD_THUMBNAILS);
        new PagesOptimizer(parameters).accept(page);
        assertNull(page.getCOSObject().getItem(COSName.getPDFName("Thumb")));
    }

    @Test
    public void discadProperties() {
        COSDictionary props = new COSDictionary();
        props.setItem(COSName.A, new COSDictionary());
        PDResources resources = new PDResources();
        resources.add(PDPropertyList.create(props));
        page.setResources(resources);
        assertNotNull(page.getResources().getProperties(COSName.getPDFName("Prop1")));
        parameters.addOptimization(Optimization.DISCARD_MC_PROPERTIES);
        new PagesOptimizer(parameters).accept(page);
        assertNull(page.getResources().getCOSObject().getItem(COSName.PROPERTIES));
    }

}
