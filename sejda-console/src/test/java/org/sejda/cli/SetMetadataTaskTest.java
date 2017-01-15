/*
 * Created on Sep 12, 2011
 * Copyright 2011 by Eduard Weissmann (edi.weissmann@gmail.com).
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
package org.sejda.cli;

import static org.junit.Assert.fail;

import java.util.Map.Entry;

import org.junit.Test;
import org.sejda.cli.command.StandardTestableTask;
import org.sejda.model.parameter.SetMetadataParameters;
import org.sejda.model.pdf.PdfMetadataKey;

/**
 * Tests for the ExtractPagesTask command line interface
 * 
 * @author Eduard Weissmann
 * 
 */
public class SetMetadataTaskTest extends AbstractTaskTest {

    public SetMetadataTaskTest() {
        super(StandardTestableTask.SET_METADATA);
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
