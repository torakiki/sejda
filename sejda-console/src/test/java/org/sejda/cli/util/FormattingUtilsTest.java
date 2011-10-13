/*
 * Created on Oct 3, 2011
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
package org.sejda.cli.util;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import org.junit.Test;

/**
 * Tests for {@link FormattingUtils}
 * 
 * @author Eduard Weissmann
 * 
 */
public class FormattingUtilsTest {

    @Test
    public void justifyLeft() {
        assertThat(FormattingUtils.justifyLeft(9, "this is line two"), is("this is\nline two"));
    }

    @Test
    public void leftPadMultiline() {
        assertThat(FormattingUtils.leftPadMultiline("this is\nline two", ' ', 3), is("this is\n   line two"));
    }

    @Test
    public void repeatedChar() {
        assertThat(FormattingUtils.repeatedChar(' ', 3), is("   "));
    }
}
