/*
 * Created on Oct 12, 2011
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
package org.sejda.conversion;

import static org.junit.Assert.assertThat;
import static org.junit.matchers.JUnitMatchers.either;
import static org.junit.matchers.JUnitMatchers.hasItem;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.sejda.model.exception.SejdaRuntimeException;

/**
 * Tests for {@link XmlFileSourceListParser}
 * 
 * @author Eduard Weissmann
 * 
 */
public class XmlFileSourceListParserTest {

    private final XmlFileSourceListParser victim = new XmlFileSourceListParser();
    private File file;

    @Before
    public void setUp() {
        InputStream contents = getClass().getResourceAsStream("/merge-filelist-config.xml");
        file = new File("/tmp/merge-filelist-config.xml");
        file.deleteOnExit();
        try {
            FileUtils.copyInputStreamToFile(contents, file);
        } catch (IOException e) {
            throw new SejdaRuntimeException("Can't create test file. Reason: " + e.getMessage(), e);
        }
    }

    @After
    public void tearDown() {
        if (file != null) {
            file.delete();
        }
    }

    @Test
    public void parseFileNames() {
        List<String> result = victim.parseFileNames(file);
        assertThat(result, hasItem("/tmp/pdf/inputFile.pdf"));
        assertThat(result, hasItem("/tmp/pdf/inputFile2.pdf:test"));
        assertThat(result, either(hasItem("/tmp/inputFile1.pdf")).or(hasItem("C:\\tmp\\inputFile1.pdf")));
        assertThat(result, either(hasItem("/tmp/inputFile2.pdf")).or(hasItem("C:\\tmp\\inputFile2.pdf")));
        assertThat(result, hasItem(FilenameUtils.separatorsToSystem("/tmp/subdir/inputFile1.pdf")));
        assertThat(result, hasItem(FilenameUtils.separatorsToSystem("/tmp/subdir3/inputFile2.pdf"))); // its defined in absolute path mode in the file
        assertThat(result, hasItem(FilenameUtils.separatorsToSystem("/tmp/subdir2/inputFile1.pdf")));
        assertThat(result, hasItem(FilenameUtils.separatorsToSystem("/tmp/subdir2/inputFile2.pdf:secret2")));
        assertThat(result, hasItem(FilenameUtils.separatorsToSystem("/tmp/subdir2/inputFile3.pdf")));
    }
}
