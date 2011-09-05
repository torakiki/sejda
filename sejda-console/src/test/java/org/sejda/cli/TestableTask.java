/*
 * Created on Aug 25, 2011
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
package org.sejda.cli;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

/**
 * Enumeration of all tasks, configured for testing
 * 
 * @author Eduard Weissmann
 * 
 */
public enum TestableTask {

    DECRYPT,
    ENCRYPT,
    ROTATE,
    SETVIEWERPREFERENCES,
    ALTERNATEMIX(false),
    UNPACK,
    MERGE(false);

    private boolean folderOutput = true;

    private TestableTask() {
        // defaults
    }

    private TestableTask(boolean folderOutput) {
        this.folderOutput = folderOutput;
    }

    public CommandLineTestBuilder getCommandLineTestBuilder() {
        CommandLineTestBuilder commandBuilder = new CommandLineTestBuilder(getTaskName());
        if (folderOutput) {
            commandBuilder.defaultFolderOutput();
        } else {
            commandBuilder.defaultFileOutput();
        }

        return commandBuilder;
    }

    String getTaskName() {
        return name().toLowerCase();
    }

    public static List<Object[]> allTasks() {
        return allTasksExceptFor();
    }

    public static List<Object[]> allTasksExceptFor(TestableTask... exceptFor) {
        Collection<TestableTask> exceptForCollection = Arrays.asList(exceptFor);
        List<Object[]> result = new ArrayList<Object[]>();
        for (TestableTask each : TestableTask.values()) {
            if (!exceptForCollection.contains(each)) {
                result.add(new Object[] { each });
            }
        }

        return result;
    }
}
