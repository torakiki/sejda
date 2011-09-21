/*
 * Created on 18/set/2011
 * Copyright 2011 by Andrea Vacondio (andrea.vacondio@gmail.com).
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
package org.sejda.core.manipulation.model.image;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Set;

import org.junit.Test;

/**
 * @author Andrea Vacondio
 * 
 */
public class ImageTypeTest {

    @Test
    public void testSupportMultipleImage() {
        Set<ImageType> supporting = ImageType.valuesSupportingMultipleImage();
        for (ImageType current : ImageType.values()) {
            if (current.isSupportMultiImage()) {
                assertTrue(supporting.contains(current));
            } else {
                assertFalse(supporting.contains(current));
            }
        }
    }
}
