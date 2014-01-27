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

import static org.hamcrest.CoreMatchers.hasItem;
import static org.junit.Assert.assertThat;

import java.util.List;

import org.junit.Test;

/**
 * @author Andrea Vacondio
 * 
 */
public class CsvFileSourceListParserTest extends BaseFileSourceListParserTest {
    private final CsvFileSourceListParser victim = new CsvFileSourceListParser();

    @Test
    public void parseFileNames() {
        List<String> result = victim.parseFileNames(csvFile);
        assertThat(result, hasItem("/another/second.pdf"));
        assertThat(result, hasItem("/my/path/first.pdf"));
    }
}
