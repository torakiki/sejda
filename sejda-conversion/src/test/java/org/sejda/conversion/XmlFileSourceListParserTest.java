/*
 * Created on Oct 12, 2011
 * Copyright 2010 by Eduard Weissmann (edi.weissmann@gmail.com).
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
package org.sejda.conversion;

import static org.apache.commons.io.FilenameUtils.separatorsToSystem;
import static org.hamcrest.CoreMatchers.hasItem;
import static org.junit.Assert.assertThat;

import java.io.File;
import java.util.List;

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
        assertThat(result, hasItem(new File(xmlFile.getParent(), "inputFile1.pdf").getAbsolutePath()));
        assertThat(result, hasItem(new File(xmlFile.getParent(), "inputFile2.pdf").getAbsolutePath()));
        assertThat(result, hasItem(separatorsToSystem("/tmp/subdir/inputFile1.pdf")));
        assertThat(result, hasItem(separatorsToSystem("/tmp/subdir3/inputFile2.pdf"))); // its defined in absolute path mode in the file
        assertThat(result, hasItem(separatorsToSystem("/tmp/subdir2/inputFile1.pdf")));
        assertThat(result, hasItem(separatorsToSystem("/tmp/subdir2/inputFile2.pdf:secret2")));
        assertThat(result, hasItem(separatorsToSystem("/tmp/subdir2/inputFile3.pdf")));
    }

    @Test(expected = ConversionException.class)
    public void testNegative() {
        victim.parseFileNames(emptyFile);
    }
}
