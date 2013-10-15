/*
 * Created on 15/ott/2013
 * Copyright 2013 by Andrea Vacondio (andrea.vacondio@gmail.com).
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); 
 * you may not use this file except in compliance with the License. 
 * You may obtain a copy of the License at 
 * 
 * http://www.apache.org/licenses/LICENSE-2.0 
 * 
 * Unless required by applicable law or agreed to in writing, software 
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. 
 * See the License for the specific language governing permissions and 
 * limitations under the License. 
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
