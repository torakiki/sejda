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

import static org.hamcrest.CoreMatchers.either;
import static org.hamcrest.CoreMatchers.hasItem;
import static org.junit.Assert.assertThat;

import java.util.List;

import org.apache.commons.io.FilenameUtils;
import org.junit.Test;
import org.sejda.conversion.exception.ConversionException;

/**
 * Tests for {@link XmlFileSourceListParser}
 * 
 * @author Eduard Weissmann
 * 
 */
public class XmlFileSourceListParserTest extends BaseFileSourceListParserTest {

    private final XmlFileSourceListParser victim = new XmlFileSourceListParser();

    @Test
    public void parseFileNames() {
        List<String> result = victim.parseFileNames(xmlFile);
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

    @Test(expected = ConversionException.class)
    public void testNegative() {
        victim.parseFileNames(emptyFile);
    }
}
