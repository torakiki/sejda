/*
 * Created on 19/ott/2011
 * Copyright 2011 by Andrea Vacondio (andrea.vacondio@gmail.com).
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
package org.sejda.model.validation.validator;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

/**
 * @author Andrea Vacondio
 * 
 */
public class DirectoryValidatorTest {

    @Rule
    public TemporaryFolder folder = new TemporaryFolder();
    private DirectoryValidator victim = new DirectoryValidator();



    @Test
    public void testNull() {
        assertTrue(victim.isValid(null, null));
    }

    @Test
    public void testPositiveExisting() throws IOException {
        assertTrue(victim.isValid(folder.newFolder(), null));
    }

    @Test
    public void testPositiveNonExisting() {
        assertTrue(victim.isValid(new File("I will be created"), null));
    }

    @Test
    public void testNegative() throws IOException {
        assertFalse(victim.isValid(folder.newFile(), null));
    }
}
