package org.sejda.core;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;

import jdepend.framework.JDepend;
import jdepend.framework.JavaPackage;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * Unit test to test against cycles.
 * 
 * @author Andrea Vacondio
 * 
 */
public class TestCycles {

    private JDepend jdepend = new JDepend();
    private Collection<? extends Object> packages = new ArrayList<Object>();

    @Before
    public void setUp() throws IOException {
        jdepend.addDirectory("target/classes");
        packages = jdepend.analyze();
    }

    @Test
    public void cycleTest() {
        for (Object p : packages) {
            JavaPackage pack1 = (JavaPackage) p;
            Assert.assertFalse(pack1.getName() + " failed.", pack1.containsCycle());
        }
    }
}
