/*
 * Created on Sep 12, 2011
 * Copyright 2011 by Eduard Weissmann (edi.weissmann@gmail.com).
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
package org.sejda.cli;

import static org.junit.Assert.fail;

import java.util.Map.Entry;

import org.junit.Test;
import org.sejda.core.manipulation.model.parameter.SetMetadataParameters;
import org.sejda.core.manipulation.model.pdf.PdfMetadataKey;

/**
 * Tests for the ExtractPagesTask command line interface
 * 
 * @author Eduard Weissmann
 * 
 */
public class SetMetadataTaskTest extends AbstractTaskTest {

    public SetMetadataTaskTest() {
        super(TestableTask.SET_METADATA);
    }

    @Test
    public void title_Specified() {
        SetMetadataParameters parameters = defaultCommandLine().without("-t")
                .with("--title", "\"A tale of two tests\"").invokeSejdaConsole();
        assertContains(PdfMetadataKey.TITLE, "A tale of two tests", parameters);
    }

    @Test
    public void title_SpecifiedAsResetEmptyString() {
        SetMetadataParameters parameters = defaultCommandLine().without("-t").with("--title", "\"\"")
                .invokeSejdaConsole();
        assertContains(PdfMetadataKey.TITLE, "", parameters);
    }

    private void assertContains(PdfMetadataKey expectedKey, String expectedValue, SetMetadataParameters parameters) {
        for (Entry<PdfMetadataKey, String> each : parameters.entrySet()) {
            if (each.getKey().equals(expectedKey) && each.getValue().equals(expectedValue)) {
                return;
            }
        }

        fail("Could not find " + expectedKey + " with value " + expectedValue);

    }

    @Test
    public void title_notSpecified() {
        SetMetadataParameters parameters = defaultCommandLine().without("-t").without("--title").with("-s", "subject")
                .invokeSejdaConsole();
        assertDoesntContain(PdfMetadataKey.TITLE, parameters);
    }

    private void assertDoesntContain(PdfMetadataKey unexpectedKey, SetMetadataParameters parameters) {
        for (Entry<PdfMetadataKey, String> each : parameters.entrySet()) {
            if (each.getKey().equals(unexpectedKey)) {
                fail("Found unexpected key " + unexpectedKey + ", having value " + each.getValue());
            }
        }
    }

    @Test
    public void author_Specified() {
        SetMetadataParameters parameters = defaultCommandLine().with("--author", "AUTH").invokeSejdaConsole();
        assertContains(PdfMetadataKey.AUTHOR, "AUTH", parameters);
    }

    @Test
    public void author_Default() {
        SetMetadataParameters parameters = defaultCommandLine().without("-a").without("--author").invokeSejdaConsole();
        assertDoesntContain(PdfMetadataKey.AUTHOR, parameters);
    }

    @Test
    public void subject_Specified() {
        SetMetadataParameters parameters = defaultCommandLine().with("--subject", "SUBJ").invokeSejdaConsole();
        assertContains(PdfMetadataKey.SUBJECT, "SUBJ", parameters);
    }

    @Test
    public void subject_Default() {
        SetMetadataParameters parameters = defaultCommandLine().without("-s").without("--subject").invokeSejdaConsole();
        assertDoesntContain(PdfMetadataKey.SUBJECT, parameters);
    }

    @Test
    public void mandatoryParams() {
        defaultCommandLine().without("-a").without("--author").without("-t").without("--title").without("-k")
                .without("--keywords").without("-s").without("--subject")
                .assertConsoleOutputContains("Please specify at least one metadata option to be set");
    }
}
