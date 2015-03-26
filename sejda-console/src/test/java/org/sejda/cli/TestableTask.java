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
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.junit.Ignore;
import org.sejda.cli.transformer.CliCommand;

/**
 * Enumeration of all cli tasks, configured for testing
 * 
 * @author Eduard Weissmann
 * 
 */
@Ignore
public enum TestableTask {

    DECRYPT,
    ENCRYPT,
    ROTATE(new RotateDefaultsProvider()),
    SET_VIEWER_PREFERENCES,
    ALTERNATE_MIX(new MultipleInputsAndFileOutputDefaultsProvider()),
    UNPACK,
    MERGE(new MultipleInputsAndFileOutputDefaultsProvider()),
    SPLIT_BY_BOOKMARKS(new SplitByBookmarksDefaultsProvider()),
    SPLIT_BY_SIZE(new SplitBySizeDefaultsProvider()),
    SPLIT_BY_PAGES(new SplitByPagesDefaultsProvider()),
    SPLIT_BY_EVERY(new SplitByEveryXPagesDefaultsProvider()),
    SIMPLE_SPLIT(new SimpleSplitDefaultsProvider()),
    EXTRACT_PAGES(new ExtractPagesDefaultsProvider()),
    EXTRACT_TEXT,
    EXTRACT_TEXT_BY_PAGES(new ExtractTextPagesDefaultsProvider()),
    SET_METADATA(new SetMetadataDefaultsProvider()),
    SET_PAGE_LABELS(new SetPageLabelsDefaultsProvider()),
    SET_PAGE_TRANSITIONS(new SetPageTransitionsDefaultsProvider()),
    CROP(new CropDefaultsProvider()),
    PDF_TO_SINGLE_TIFF(new PdfToSingleTiffDefaultsProvider()),
    PDF_TO_MULTIPLE_TIFF(new PdfToMultipleTiffDefaultsProvider()),
    PDF_TO_JPEG(new SingleInputAndFolderOutputDefaultsProvider()),
    SET_HEADER_FOOTER(new SetHeaderFooterDefaultsProvider());

    private final DefaultsProvider defaultsProvider;

    private TestableTask() {
        // defaults
        this.defaultsProvider = new DefaultDefaultsProvider();
    }

    private TestableTask(DefaultsProvider defaultsProvider) {
        this.defaultsProvider = defaultsProvider;
    }

    public CommandLineTestBuilder getCommandLineDefaults() {
        return defaultsProvider.provideDefaults(getTaskName());
    }

    String getTaskName() {
        return name().toLowerCase().replaceAll("_", "");
    }

    String getExampleUsage() {
        return getCorrespondingCliCommand().getExampleUsage();
    }

    /**
     * @return the {@link CliCommand} matching this {@link TestableTask}
     * 
     */
    public CliCommand getCorrespondingCliCommand() {
        for (CliCommand eachCliCommand : CliCommand.values()) {
            if (StringUtils.equalsIgnoreCase(eachCliCommand.name(), this.name())) {
                return eachCliCommand;
            }
        }

        return null;
    }

    public static TestableTask[] allTasks() {
        return allTasksExceptFor();
    }

    public static TestableTask[] allTasksExceptFor(TestableTask... exceptFor) {
        List<TestableTask> result = new ArrayList<TestableTask>(Arrays.asList(TestableTask.values()));
        result.removeAll(Arrays.asList(exceptFor));

        return result.toArray(new TestableTask[result.size()]);
    }

    public static TestableTask[] getTasksWithMultipleSouceFiles() {
        return new TestableTask[] { TestableTask.DECRYPT, TestableTask.ENCRYPT, TestableTask.ROTATE,
                TestableTask.SET_VIEWER_PREFERENCES, TestableTask.UNPACK, TestableTask.EXTRACT_TEXT,
                TestableTask.ALTERNATE_MIX, TestableTask.MERGE };
    }

    boolean hasFolderOutput() {
        return getCorrespondingCliCommand().hasFolderOutput();
    }

    boolean hasPrefixableOutput() {
        return getCorrespondingCliCommand().hasPrefixableOutput();
    }

    public static TestableTask[] getTasksWithFolderOutput() {
        List<TestableTask> result = new ArrayList<TestableTask>();
        for (TestableTask each : TestableTask.values()) {
            if (each.hasFolderOutput()) {
                result.add(each);
            }
        }
        return result.toArray(new TestableTask[result.size()]);
    }

    public static TestableTask[] getTasksWithPrefixableOutput() {
        List<TestableTask> result = new ArrayList<TestableTask>();
        for (TestableTask each : TestableTask.values()) {
            if (each.hasPrefixableOutput()) {
                result.add(each);
            }
        }
        return result.toArray(new TestableTask[result.size()]);
    }
}

interface DefaultsProvider {

    CommandLineTestBuilder provideDefaults(String taskName);
}

class RotateDefaultsProvider extends DefaultDefaultsProvider {
    @Override
    public CommandLineTestBuilder provideDefaults(String taskName) {
        return super.provideDefaults(taskName).with("-r", "90").with("-m", "all");
    }
}

class DefaultDefaultsProvider implements DefaultsProvider {

    public CommandLineTestBuilder provideDefaults(String taskName) {
        return new CommandLineTestBuilder(taskName).defaultTwoInputs().defaultFolderOutput();
    }

}

class MultipleInputsAndFileOutputDefaultsProvider implements DefaultsProvider {

    public CommandLineTestBuilder provideDefaults(String taskName) {
        return new CommandLineTestBuilder(taskName).defaultTwoInputs().defaultFileOutput();
    }

}

class SingleInputAndFolderOutputDefaultsProvider implements DefaultsProvider {

    public CommandLineTestBuilder provideDefaults(String taskName) {
        return new CommandLineTestBuilder(taskName).defaultSingleInput().defaultFolderOutput();
    }

}

class SingleInputAndFileOutputDefaultsProvider implements DefaultsProvider {

    public CommandLineTestBuilder provideDefaults(String taskName) {
        return new CommandLineTestBuilder(taskName).defaultSingleInput().defaultFileOutput();
    }

}

class SplitByBookmarksDefaultsProvider extends SingleInputAndFolderOutputDefaultsProvider {
    @Override
    public CommandLineTestBuilder provideDefaults(String taskName) {
        return super.provideDefaults(taskName).with("-l", "1");
    }
}

class SplitBySizeDefaultsProvider extends SingleInputAndFolderOutputDefaultsProvider {
    @Override
    public CommandLineTestBuilder provideDefaults(String taskName) {
        return super.provideDefaults(taskName).with("-s", "1234567890123456789");
    }
}

class SplitByPagesDefaultsProvider extends SingleInputAndFolderOutputDefaultsProvider {
    @Override
    public CommandLineTestBuilder provideDefaults(String taskName) {
        return super.provideDefaults(taskName).with("-n", "1 2 3 9 23 78");
    }
}

class SplitByEveryXPagesDefaultsProvider extends SingleInputAndFolderOutputDefaultsProvider {
    @Override
    public CommandLineTestBuilder provideDefaults(String taskName) {
        return super.provideDefaults(taskName).with("-n", "5");
    }
}

class ExtractTextPagesDefaultsProvider extends SingleInputAndFolderOutputDefaultsProvider {
    @Override
    public CommandLineTestBuilder provideDefaults(String taskName) {
        return super.provideDefaults(taskName).with("-s", "4,12-14,8,20-");
    }
}

class SimpleSplitDefaultsProvider extends SingleInputAndFolderOutputDefaultsProvider {
    @Override
    public CommandLineTestBuilder provideDefaults(String taskName) {
        return super.provideDefaults(taskName).with("-s", "all");
    }
}

class ExtractPagesDefaultsProvider extends SingleInputAndFileOutputDefaultsProvider {
    @Override
    public CommandLineTestBuilder provideDefaults(String taskName) {
        return super.provideDefaults(taskName).with("-p", "all");
    }
}

class SetMetadataDefaultsProvider extends SingleInputAndFileOutputDefaultsProvider {
    @Override
    public CommandLineTestBuilder provideDefaults(String taskName) {
        return super.provideDefaults(taskName).with("-t", "\"Tales from a test\"");
    }
}

class SetPageLabelsDefaultsProvider extends SingleInputAndFileOutputDefaultsProvider {
    @Override
    public CommandLineTestBuilder provideDefaults(String taskName) {
        return super.provideDefaults(taskName).with("-l", "99:uroman:1:Chapter");
    }
}

class SetHeaderFooterDefaultsProvider extends SingleInputAndFileOutputDefaultsProvider {
    @Override
    public CommandLineTestBuilder provideDefaults(String taskName) {
        return super.provideDefaults(taskName).with("-l", "label").with("-s", "all");
    }
}

class SetPageTransitionsDefaultsProvider extends SingleInputAndFileOutputDefaultsProvider {
    @Override
    public CommandLineTestBuilder provideDefaults(String taskName) {
        return super.provideDefaults(taskName).with("--transitions", "dissolve:6:9:55");
    }
}

class CropDefaultsProvider extends SingleInputAndFileOutputDefaultsProvider {
    @Override
    public CommandLineTestBuilder provideDefaults(String taskName) {
        return super.provideDefaults(taskName).with("--cropAreas", "[1:2][3:4]");
    }
}

class PdfToSingleTiffDefaultsProvider extends SingleInputAndFileOutputDefaultsProvider {
    @Override
    public CommandLineTestBuilder provideDefaults(String taskName) {
        return super.provideDefaults(taskName).with("--colorType", "gray_scale");
    }
}

class PdfToMultipleTiffDefaultsProvider extends SingleInputAndFolderOutputDefaultsProvider {
    @Override
    public CommandLineTestBuilder provideDefaults(String taskName) {
        return super.provideDefaults(taskName).with("--colorType", "gray_scale");
    }
}