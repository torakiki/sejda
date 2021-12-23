package org.sejda.conversion;

import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;
import com.tngtech.archunit.library.dependencies.SlicesRuleDefinition;

/**
 * Test for cycles.
 * 
 * @author Andrea Vacondio
 * 
 */
@AnalyzeClasses(packages = "org.sejda")
public class TestCycles {

    @ArchTest
    public static final ArchRule myRule = SlicesRuleDefinition.slices().matching("org.sejda.(*)..").should()
            .beFreeOfCycles();
}
