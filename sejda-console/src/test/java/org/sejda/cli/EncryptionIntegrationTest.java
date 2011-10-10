/*
 * Created on Oct 10, 2011
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
package org.sejda.cli;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;

import java.util.Collection;

import org.apache.commons.lang.StringUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runners.Parameterized.Parameters;

/**
 * Test encrypted files across all pdf implementations (itext, pdfbox and icepdf) - due to collisions between the libraries that each pdf implementation uses for encryption, there
 * might be different behaviour at runtime
 * 
 * @author Eduard Weissmann
 * 
 */
public class EncryptionIntegrationTest extends AbstractTaskTraitTest {

    @Override
    @Before
    public void setUp() {
        super.setUp();
        createTestEncryptedPdfFile("/tmp/file1encrypted.pdf");
    }

    public EncryptionIntegrationTest(TestableTask testableTask) {
        super(testableTask);
    }

    @Parameters
    public final static Collection<Object[]> testParameters() {
        return TestableTask.allTasks();
    }

    @Test
    public void executeExampleUsageWithEncryptedFileAsInput() {
        String exampleUsage = testableTask.getExampleUsage();
        // use an encrypted file as input instead of the regular input file
        exampleUsage = StringUtils.replace(exampleUsage, "/tmp/file1.pdf:secret123", "/tmp/file1encrypted.pdf:test");
        exampleUsage = StringUtils.replace(exampleUsage, "/tmp/file1.pdf", "/tmp/file1encrypted.pdf:test");

        assertThat("Task " + getTaskName() + " doesnt provide example usage", exampleUsage, is(notNullValue()));

        assertTaskCompletes(exampleUsage + " --overwrite");
    }
}
