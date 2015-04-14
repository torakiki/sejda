/*
 * Copyright 2015 by Edi Weissmann (edi.weissmann@gmail.com)
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

import org.junit.Test;
import static org.junit.Assert.*;

public class PrefixUtilsTest {

    @Test
    public void testSafeFilename() {
        assertEquals("1_Invoice#0001.pdf", PrefixUtils.toSafeFilename("1_Invoice#0001:*<>/\\.pdf"));
        assertEquals("..test.pdf", PrefixUtils.toSafeFilename("../test.pdf"));
        assertEquals("..test.pdf", PrefixUtils.toSafeFilename("..\\test.pdf"));
        assertEquals(".test.pdf", PrefixUtils.toSafeFilename("./test.pdf"));
    }
}
