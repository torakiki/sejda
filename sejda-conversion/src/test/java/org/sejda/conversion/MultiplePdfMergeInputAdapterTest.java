/*
 * Created on 27/gen/2014
 * Copyright 2014 by Andrea Vacondio (andrea.vacondio@gmail.com).
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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
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
        List<PdfFileSource> inputList = new ArrayList<PdfFileSource>();
        inputList.add(PdfFileSource.newInstanceNoPassword(file));
        inputList.add(PdfFileSource.newInstanceNoPassword(file));
        List<PdfMergeInput> result = new MultiplePdfMergeInputAdapter(inputList,
                new MultiplePageRangeSetAdapter("3-5").ranges()).getPdfMergeInputs();
        assertEquals(2, result.size());
        assertFalse(result.get(0).isAllPages());
        assertTrue(result.get(1).isAllPages());
    }
}
