/*
 * Created on 09/giu/2014
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

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;

import org.junit.Test;
import org.sejda.conversion.exception.ConversionException;

/**
 * @author Andrea Vacondio
 *
 */
public class PageNumbersListAdapterTest {

    @Test
    public void positives() {
        assertThat(new PageNumbersListAdapter("1").getPageNumbers(), contains(1));
        assertThat(new PageNumbersListAdapter("1,3,6").getPageNumbers(), contains(1, 3, 6));
        assertThat(new PageNumbersListAdapter(" 1, 3, 6 ").getPageNumbers(), contains(1, 3, 6));
    }

    @Test(expected = ConversionException.class)
    public void invalidNumberCollection() {
        new PageNumbersListAdapter("1,3,a");
    }

    @Test(expected = ConversionException.class)
    public void invalidNumber() {
        new PageNumbersListAdapter("chuck");
    }
}
