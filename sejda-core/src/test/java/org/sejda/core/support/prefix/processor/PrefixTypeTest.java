/*
 * Created on 15/ott/2013
 * Copyright 2013 by Andrea Vacondio (andrea.vacondio@gmail.com).
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
package org.sejda.core.support.prefix.processor;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

/**
 * @author Andrea Vacondio
 * 
 */
public class PrefixTypeTest {

    private static final String NO_PREFIX = "NO_PREFIX";
    @Test
    public void isFoundIn() {
        assertTrue(PrefixType.BASENAME.isFoundIn("Chuck_[BASENAME]_Norris"));
        assertTrue(PrefixType.BASENAME.isFoundIn("[BASENAME]_Norris"));
        assertTrue(PrefixType.BASENAME.isFoundIn("Chuck_[BASENAME]"));
        assertTrue(PrefixType.BASENAME.isFoundIn("[BASENAME]"));

        assertTrue(PrefixType.BOOKMARK.isFoundIn("Chuck_[BOOKMARK_NAME]_Norris"));
        assertTrue(PrefixType.BOOKMARK.isFoundIn("Chuck_[BOOKMARK_NAME]"));
        assertTrue(PrefixType.BOOKMARK.isFoundIn("[BOOKMARK_NAME]_Norris"));
        assertTrue(PrefixType.BOOKMARK.isFoundIn("[BOOKMARK_NAME]"));

        assertTrue(PrefixType.BOOKMARK_STRICT.isFoundIn("Chuck_[BOOKMARK_NAME_STRICT]_Norris"));
        assertTrue(PrefixType.BOOKMARK_STRICT.isFoundIn("Chuck_[BOOKMARK_NAME_STRICT]"));
        assertTrue(PrefixType.BOOKMARK_STRICT.isFoundIn("[BOOKMARK_NAME_STRICT]_Norris"));
        assertTrue(PrefixType.BOOKMARK_STRICT.isFoundIn("[BOOKMARK_NAME_STRICT]"));

        assertTrue(PrefixType.CURRENTPAGE.isFoundIn("Chuck_[CURRENTPAGE]_Norris"));
        assertTrue(PrefixType.CURRENTPAGE.isFoundIn("Chuck_[CURRENTPAGE]"));
        assertTrue(PrefixType.CURRENTPAGE.isFoundIn("[CURRENTPAGE]_Norris"));
        assertTrue(PrefixType.CURRENTPAGE.isFoundIn("[CURRENTPAGE]"));
        assertTrue(PrefixType.CURRENTPAGE.isFoundIn("[CURRENTPAGE##]"));
        assertTrue(PrefixType.CURRENTPAGE.isFoundIn("Chuck_[CURRENTPAGE##]_Norris"));
        assertTrue(PrefixType.CURRENTPAGE.isFoundIn("Chuck_[CURRENTPAGE##]"));
        assertTrue(PrefixType.CURRENTPAGE.isFoundIn("[CURRENTPAGE##]_Norris"));

        assertTrue(PrefixType.FILENUMBER.isFoundIn("Chuck_[FILENUMBER]_Norris"));
        assertTrue(PrefixType.FILENUMBER.isFoundIn("Chuck_[FILENUMBER]"));
        assertTrue(PrefixType.FILENUMBER.isFoundIn("[FILENUMBER]_Norris"));
        assertTrue(PrefixType.FILENUMBER.isFoundIn("[FILENUMBER]"));
        assertTrue(PrefixType.FILENUMBER.isFoundIn("[FILENUMBER##]"));
        assertTrue(PrefixType.FILENUMBER.isFoundIn("Chuck_[FILENUMBER##]_Norris"));
        assertTrue(PrefixType.FILENUMBER.isFoundIn("Chuck_[FILENUMBER##]"));
        assertTrue(PrefixType.FILENUMBER.isFoundIn("[FILENUMBER##]_Norris"));
        assertTrue(PrefixType.FILENUMBER.isFoundIn("[FILENUMBER##10]"));
        assertTrue(PrefixType.FILENUMBER.isFoundIn("Chuck_[FILENUMBER##10]_Norris"));
        assertTrue(PrefixType.FILENUMBER.isFoundIn("Chuck_[FILENUMBER##10]"));
        assertTrue(PrefixType.FILENUMBER.isFoundIn("[FILENUMBER##10]_Norris"));

        assertTrue(PrefixType.TIMESTAMP.isFoundIn("Chuck_[TIMESTAMP]_Norris"));
        assertTrue(PrefixType.TIMESTAMP.isFoundIn("Chuck_[TIMESTAMP]"));
        assertTrue(PrefixType.TIMESTAMP.isFoundIn("[TIMESTAMP]_Norris"));
        assertTrue(PrefixType.TIMESTAMP.isFoundIn("[TIMESTAMP]"));

    }

    @Test
    public void isNotFoundIn() {
        for (PrefixType current : PrefixType.values()) {
            assertFalse(current.isFoundIn(NO_PREFIX));
        }
    }

}
