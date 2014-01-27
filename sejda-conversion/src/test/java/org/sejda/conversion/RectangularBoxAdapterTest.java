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

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

import org.junit.Test;
import org.sejda.conversion.exception.ConversionException;
import org.sejda.model.RectangularBox;

/**
 * @author Andrea Vacondio
 *
 */
public class RectangularBoxAdapterTest {
    @Test
    public void testPositive() {
        assertThat(new RectangularBoxAdapter("[2:3][10:20]").getRectangularBox(),
                is(RectangularBox.newInstance(2, 3, 10, 20)));
    }

    @Test(expected = ConversionException.class)
    public void missingPoint() {
        assertThat(new RectangularBoxAdapter("[2:3][10:]").getRectangularBox(),
                is(RectangularBox.newInstance(2, 3, 10, 20)));
    }

    @Test(expected = ConversionException.class)
    public void missingPointAgain() {
        assertThat(new RectangularBoxAdapter("[2:3][10]").getRectangularBox(),
                is(RectangularBox.newInstance(2, 3, 10, 20)));
    }
}
