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

import org.junit.Test;

/**
 * @author Andrea Vacondio
 * 
 */
public class PrefixTypeTest {

    private static final String NO_PREFIX = "NO_PREFIX";
    @Test
    public void isFoundIn() {
        PrefixType.BASENAME.isFoundIn("Chuck_[BASENAME]_Norris");
        PrefixType.BASENAME.isFoundIn("[BASENAME]_Norris");
        PrefixType.BASENAME.isFoundIn("Chuck_[BASENAME]");
        PrefixType.BASENAME.isFoundIn("[BASENAME]");

        PrefixType.BOOKMARK.isFoundIn("Chuck_[BOOKMARK_NAME]_Norris");
        PrefixType.BOOKMARK.isFoundIn("Chuck_[BOOKMARK_NAME]");
        PrefixType.BOOKMARK.isFoundIn("[BOOKMARK_NAME]_Norris");
        PrefixType.BOOKMARK.isFoundIn("[BOOKMARK_NAME]");

        PrefixType.BOOKMARK_STRICT.isFoundIn("Chuck_[BOOKMARK_NAME_STRICT]_Norris");
        PrefixType.BOOKMARK_STRICT.isFoundIn("Chuck_[BOOKMARK_NAME_STRICT]");
        PrefixType.BOOKMARK_STRICT.isFoundIn("[BOOKMARK_NAME_STRICT]_Norris");
        PrefixType.BOOKMARK_STRICT.isFoundIn("[BOOKMARK_NAME_STRICT]");

        PrefixType.CURRENTPAGE.isFoundIn("Chuck_[CURRENTPAGE]_Norris");
        PrefixType.CURRENTPAGE.isFoundIn("Chuck_[CURRENTPAGE]");
        PrefixType.CURRENTPAGE.isFoundIn("[CURRENTPAGE]_Norris");
        PrefixType.CURRENTPAGE.isFoundIn("[CURRENTPAGE]");
        PrefixType.CURRENTPAGE.isFoundIn("[CURRENTPAGE##]");
        PrefixType.CURRENTPAGE.isFoundIn("Chuck_[CURRENTPAGE##]_Norris");
        PrefixType.CURRENTPAGE.isFoundIn("Chuck_[CURRENTPAGE##]");
        PrefixType.CURRENTPAGE.isFoundIn("[CURRENTPAGE##]_Norris");

        PrefixType.FILENUMBER.isFoundIn("Chuck_[FILENUMBER]_Norris");
        PrefixType.FILENUMBER.isFoundIn("Chuck_[FILENUMBER]");
        PrefixType.FILENUMBER.isFoundIn("[FILENUMBER]_Norris");
        PrefixType.FILENUMBER.isFoundIn("[FILENUMBER]");
        PrefixType.FILENUMBER.isFoundIn("[FILENUMBER##]");
        PrefixType.FILENUMBER.isFoundIn("Chuck_[FILENUMBER##]_Norris");
        PrefixType.FILENUMBER.isFoundIn("Chuck_[FILENUMBER##]");
        PrefixType.FILENUMBER.isFoundIn("[FILENUMBER##]_Norris");
        PrefixType.FILENUMBER.isFoundIn("[FILENUMBER##10]");
        PrefixType.FILENUMBER.isFoundIn("Chuck_[FILENUMBER##10]_Norris");
        PrefixType.FILENUMBER.isFoundIn("Chuck_[FILENUMBER##10]");
        PrefixType.FILENUMBER.isFoundIn("[FILENUMBER##10]_Norris");

        PrefixType.TIMESTAMP.isFoundIn("Chuck_[TIMESTAMP]_Norris");
        PrefixType.TIMESTAMP.isFoundIn("Chuck_[TIMESTAMP]");
        PrefixType.TIMESTAMP.isFoundIn("[TIMESTAMP]_Norris");
        PrefixType.TIMESTAMP.isFoundIn("[TIMESTAMP]");

    }

    @Test
    public void isNotFoundIn() {
        for (PrefixType current : PrefixType.values()) {
            assertFalse(current.isFoundIn(NO_PREFIX));
        }
    }

}
