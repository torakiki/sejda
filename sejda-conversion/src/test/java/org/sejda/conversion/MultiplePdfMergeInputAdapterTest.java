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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.sejda.model.input.PdfFileSource;
import org.sejda.model.input.PdfMergeInput;

/**
 * @author Andrea Vacondio
 * 
 */
public class MultiplePdfMergeInputAdapterTest {

    @Test
    public void testPositive(){
        File file = mock(File.class);
        when(file.isFile()).thenReturn(true);
        when(file.getName()).thenReturn("test.pdf");
        List<PdfFileSource> inputList = new ArrayList<>();
        inputList.add(PdfFileSource.newInstanceNoPassword(file));
        inputList.add(PdfFileSource.newInstanceNoPassword(file));
        List<PdfMergeInput> result = new MultiplePdfMergeInputAdapter(inputList,
                new MultiplePageRangeSetAdapter("3-5").ranges()).getPdfMergeInputs();
        assertEquals(2, result.size());
        assertFalse(result.get(0).isAllPages());
        assertTrue(result.get(1).isAllPages());
    }
}
