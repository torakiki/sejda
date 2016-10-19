/*
 * Created on 27/gen/2014
 * Copyright 2014 by Andrea Vacondio (andrea.vacondio@gmail.com).
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

import static org.junit.Assert.assertNotNull;

import java.io.IOException;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.sejda.conversion.exception.ConversionException;

/**
 * @author Andrea Vacondio
 * 
 */
public class DirectoryOutputAdapterTest {

    @Rule
    public TemporaryFolder folder = new TemporaryFolder();

    @Test(expected = ConversionException.class)
    public void testNegative() {
        new DirectoryOutputAdapter("/I/dont/exist");
    }

    @Test(expected = ConversionException.class)
    public void testFile() throws IOException {
        new DirectoryOutputAdapter(folder.newFile().getAbsolutePath());
    }

    @Test
    public void testPositive() throws IOException {
        assertNotNull(new DirectoryOutputAdapter(folder.newFolder().getAbsolutePath()).getPdfDirectoryOutput());
    }
}
