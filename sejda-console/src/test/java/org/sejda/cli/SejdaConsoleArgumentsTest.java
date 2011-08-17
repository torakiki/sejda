/*
 * Created on Jul 6, 2011
 * Copyright 2011 by Eduard Weissmann (edi.weissmann@gmail.com).
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
package org.sejda.cli;

import static org.junit.Assert.assertArrayEquals;

import org.apache.commons.lang.StringUtils;
import org.junit.Test;

/**
 * Tests for {@link RawArguments}
 * 
 * @author Eduard Weissmann
 * 
 */
public class SejdaConsoleArgumentsTest {

    @Test
    public void testGeneralOptionsArguments() {
        assertGeneralOptionsArguments(new String[] {}, new String[] {});
        assertGeneralOptionsArguments(new String[] { "" }, new String[] { "" });
        assertGeneralOptionsArguments(new String[] { "-h" }, new String[] { "-h" });
        assertGeneralOptionsArguments(new String[] { "-h", "command_name" }, new String[] { "-h", "command_name" });
        assertGeneralOptionsArguments(new String[] { "-h", "command_name" }, new String[] { "-h", "command_name",
                "other stuff" });
        assertGeneralOptionsArguments(new String[] { "command_name" },
                new String[] { "command_name", "command_params" });
    }

    @Test
    public void testCommandOptionsArguments() {
        assertCommandOptionsArguments(new String[] { "command_param1", "command_param2", "command_param3" },
                new String[] { "command_name", "command_param1", "command_param2", "command_param3" });
    }

    private void assertGeneralOptionsArguments(String[] expected, String[] input) {
        String[] actual = new RawArguments(input).getGeneralArguments();
        assertArrayEquals("For input: '" + StringUtils.join(input, " ") + "'", expected, actual);
    }

    private void assertCommandOptionsArguments(String[] expected, String[] input) {
        String[] actual = new RawArguments(input).getCommandArguments();
        assertArrayEquals("For input: '" + StringUtils.join(input, " ") + "'", expected, actual);
    }
}
