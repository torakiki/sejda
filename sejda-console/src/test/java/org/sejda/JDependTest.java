/*
 * Created on Jul 8, 2011
 * Copyright 2010 by Eduard Weissmann (edi.weissmann@gmail.com).
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
package org.sejda;

import static org.junit.Assert.assertFalse;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;

import jdepend.framework.JDepend;
import jdepend.framework.JavaPackage;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

/**
 * Unit test that checks for dependency cycles between packages
 * 
 * @author Eduard Weissmann
 * @author http://www.softwarepoets.org/2009/04/unit-tests-to-check-against-cyclic.html
 * 
 */
@RunWith(Parameterized.class)
public class JDependTest {

    private static JDepend jdepend = null;
    private static Collection packages = null;
    private JavaPackage pack1 = null;

    @Parameterized.Parameters
    public static Collection data() throws IOException {
        Collection result = new ArrayList();
        jdepend = new JDepend();
        jdepend.addDirectory("target/classes");
        packages = jdepend.analyze();
        for (Object p : packages) {
            result.add(new Object[] { p });
        }
        return result;
    }

    public JDependTest(JavaPackage pack) {
        pack1 = pack;
    }

    @Test
    public void cycleTest() {
        assertFalse(pack1.getName() + " failed, has cycles", pack1.containsCycle());
    }
}
