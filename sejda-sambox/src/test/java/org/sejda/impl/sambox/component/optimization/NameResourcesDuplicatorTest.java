/*
 * Created on 24 feb 2021
 * Copyright 2019 Sober Lemur S.a.s. di Vacondio Andrea and Sejda BV
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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

import org.junit.Test;
import org.sejda.sambox.cos.COSDictionary;
import org.sejda.sambox.cos.COSName;
import org.sejda.sambox.pdmodel.PDPage;
import org.sejda.sambox.pdmodel.PDResources;

/**
 * @author Andrea Vacondio
 *
 */
public class NameResourcesDuplicatorTest {

    @Test
    public void duplicateResourceDictionary() {
        NameResourcesDuplicator victim = new NameResourcesDuplicator();
        PDPage page = new PDPage();
        page.setResources(new PDResources());
        PDPage copy = new PDPage(page.getCOSObject().duplicate());
        assertEquals(copy.getCOSObject().getDictionaryObject(COSName.RESOURCES, COSDictionary.class),
                page.getCOSObject().getDictionaryObject(COSName.RESOURCES, COSDictionary.class));
        victim.accept(copy);
        assertNotEquals(copy.getCOSObject().getDictionaryObject(COSName.RESOURCES, COSDictionary.class),
                page.getCOSObject().getDictionaryObject(COSName.RESOURCES, COSDictionary.class));

    }

    @Test
    public void duplicateNameDictionaries() {
        NameResourcesDuplicator victim = new NameResourcesDuplicator();
        PDPage page = new PDPage();
        page.setResources(new PDResources());
        COSDictionary resources = page.getCOSObject().getDictionaryObject(COSName.RESOURCES, COSDictionary.class);
        resources.setItem(COSName.FONT, new COSDictionary());
        resources.setItem(COSName.XOBJECT, new COSDictionary());
        resources.setItem(COSName.EXT_G_STATE, new COSDictionary());
        PDPage copy = new PDPage(page.getCOSObject().duplicate());
        assertEquals(
                copy.getCOSObject().getDictionaryObject(COSName.RESOURCES, COSDictionary.class).getItem(COSName.FONT),
                page.getCOSObject().getDictionaryObject(COSName.RESOURCES, COSDictionary.class).getItem(COSName.FONT));
        assertEquals(
                copy.getCOSObject().getDictionaryObject(COSName.RESOURCES, COSDictionary.class)
                        .getItem(COSName.XOBJECT),
                page.getCOSObject().getDictionaryObject(COSName.RESOURCES, COSDictionary.class)
                        .getItem(COSName.XOBJECT));
        assertEquals(
                copy.getCOSObject().getDictionaryObject(COSName.RESOURCES, COSDictionary.class)
                        .getItem(COSName.EXT_G_STATE),
                page.getCOSObject().getDictionaryObject(COSName.RESOURCES, COSDictionary.class)
                        .getItem(COSName.EXT_G_STATE));
        victim.accept(copy);
        assertNotEquals(
                copy.getCOSObject().getDictionaryObject(COSName.RESOURCES, COSDictionary.class).getItem(COSName.FONT),
                page.getCOSObject().getDictionaryObject(COSName.RESOURCES, COSDictionary.class).getItem(COSName.FONT));
        assertNotEquals(
                copy.getCOSObject().getDictionaryObject(COSName.RESOURCES, COSDictionary.class)
                        .getItem(COSName.XOBJECT),
                page.getCOSObject().getDictionaryObject(COSName.RESOURCES, COSDictionary.class)
                        .getItem(COSName.XOBJECT));
        assertNotEquals(
                copy.getCOSObject().getDictionaryObject(COSName.RESOURCES, COSDictionary.class)
                        .getItem(COSName.EXT_G_STATE),
                page.getCOSObject().getDictionaryObject(COSName.RESOURCES, COSDictionary.class)
                        .getItem(COSName.EXT_G_STATE));

    }
}
