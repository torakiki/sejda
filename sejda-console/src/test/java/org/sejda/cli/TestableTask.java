/*
 * Created on Aug 25, 2011
 * Copyright 2010 by Eduard Weissmann (edi.weissmann@gmail.com).
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
package org.sejda.cli;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import org.junit.Ignore;
import org.sejda.cli.command.CliCommand;
import org.sejda.cli.command.ProCliCommand;
import org.sejda.cli.command.StandardCliCommand;
import org.sejda.cli.model.CliArgumentsWithDirectoryOutput;
import org.sejda.cli.model.CliArgumentsWithPrefixableOutput;
import org.sejda.cli.model.MultipleOptionalPdfSourceTaskCliArguments;
import org.sejda.cli.model.MultiplePdfSourceTaskCliArguments;
import org.sejda.cli.model.MultipleSourceTaskCliArguments;
import org.sejda.cli.model.SinglePdfSourceTaskCliArguments;
/**
 * Enumeration of all cli tasks, configured for testing
 * 
 * @author Eduard Weissmann
 * 
 */
@Ignore
public enum TestableTask {

    DECRYPT(StandardCliCommand.DECRYPT),
    ENCRYPT(StandardCliCommand.ENCRYPT),
    ROTATE(StandardCliCommand.ROTATE, new RotateDefaultsProvider()),
    SET_VIEWER_PREFERENCES(StandardCliCommand.SET_VIEWER_PREFERENCES),
    ALTERNATE_MIX(StandardCliCommand.ALTERNATE_MIX, new MultipleInputsAndFileOutputDefaultsProvider()),
    UNPACK(StandardCliCommand.UNPACK),
    MERGE(StandardCliCommand.MERGE, new MultipleInputsAndFileOutputDefaultsProvider()),
    SPLIT_BY_BOOKMARKS(StandardCliCommand.SPLIT_BY_BOOKMARKS, new SplitByBookmarksDefaultsProvider()),
    SPLIT_BY_SIZE(StandardCliCommand.SPLIT_BY_SIZE, new SplitBySizeDefaultsProvider()),
    SPLIT_BY_PAGES(StandardCliCommand.SPLIT_BY_PAGES, new SplitByPagesDefaultsProvider()),
    SPLIT_BY_EVERY(StandardCliCommand.SPLIT_BY_EVERY, new SplitByEveryXPagesDefaultsProvider()),
    SIMPLE_SPLIT(StandardCliCommand.SIMPLE_SPLIT, new SimpleSplitDefaultsProvider()),
    EXTRACT_BY_BOOKMARKS(StandardCliCommand.EXTRACT_BY_BOOKMARKS, new SplitByBookmarksDefaultsProvider()),
    EXTRACT_PAGES(StandardCliCommand.EXTRACT_PAGES, new ExtractPagesDefaultsProvider()),
    EXTRACT_TEXT(ProCliCommand.EXTRACT_TEXT),
    EXTRACT_TEXT_BY_PAGES(ProCliCommand.EXTRACT_TEXT_BY_PAGES, new ExtractTextPagesDefaultsProvider()),
    SET_METADATA(StandardCliCommand.SET_METADATA, new SetMetadataDefaultsProvider()),
    SET_PAGE_LABELS(StandardCliCommand.SET_PAGE_LABELS, new SetPageLabelsDefaultsProvider()),
    SET_PAGE_TRANSITIONS(StandardCliCommand.SET_PAGE_TRANSITIONS, new SetPageTransitionsDefaultsProvider()),
    CROP(ProCliCommand.CROP, new CropDefaultsProvider()),
    PDF_TO_SINGLE_TIFF(StandardCliCommand.PDF_TO_SINGLE_TIFF, new PdfToSingleTiffDefaultsProvider()),
    PDF_TO_MULTIPLE_TIFF(StandardCliCommand.PDF_TO_MULTIPLE_TIFF, new PdfToMultipleTiffDefaultsProvider()),
    PDF_TO_JPEG(StandardCliCommand.PDF_TO_JPEG, new MultipleInputsAndFolderOutputDefaultsProvider()),
    SET_HEADER_FOOTER(StandardCliCommand.SET_HEADER_FOOTER, new SetHeaderFooterDefaultsProvider()),
    COMBINE_REORDER(StandardCliCommand.COMBINE_REORDER, new CombineReorderDefaultsProvider()),
    SPLIT_DOWN_THE_MIDDLE(ProCliCommand.SPLIT_DOWN_THE_MIDDLE, new MultipleInputsAndFolderOutputDefaultsProvider()),
    SPLIT_BY_TEXT(ProCliCommand.SPLIT_BY_TEXT, new SplitByTextDefaultsProvider()),
    COMPRESS(ProCliCommand.COMPRESS, new MultipleInputsAndFolderOutputDefaultsProvider()),
    ADD_BACK_PAGES(StandardCliCommand.ADD_BACK_PAGES, new AddBackPagesDefaultsProvider()),
    PORTFOLIO(StandardCliCommand.PORTFOLIO, taskName -> new CommandLineTestBuilder(taskName)
            .defaultMultipleNonPdfInputs().defaultFileOutput()),
    NUP(ProCliCommand.NUP, taskName -> new CommandLineTestBuilder(taskName).defaultMultiplePdfInputs()
            .defaultFolderOutput()),
    WATERMARK(StandardCliCommand.WATERMARK, new WatermarkDefaultsProvider()),
    SCALE(StandardCliCommand.SCALE, new ScaleDefaultsProvider());

    private final DefaultsProvider defaultsProvider;
    public final CliCommand command;

    private TestableTask(CliCommand command) {
        this(command, new DefaultDefaultsProvider());
    }

    private TestableTask(CliCommand command, DefaultsProvider defaultsProvider) {
        this.command = command;
        this.defaultsProvider = defaultsProvider;
    }

    public CommandLineTestBuilder getCommandLineDefaults() {
        return defaultsProvider.provideDefaults(getTaskName());
    }

    String getTaskName() {
        return name().toLowerCase().replaceAll("_", "");
    }

    public static List<TestableTask> allTasks() {
        return allTasksExceptFor();
    }

    public static List<TestableTask> allTasksExceptFor(TestableTask... exceptFor) {
        List<TestableTask> result = new ArrayList<TestableTask>(Arrays.asList(TestableTask.values()));
        result.removeAll(Arrays.asList(exceptFor));
        return result;
    }

    public static List<TestableTask> allTasksExceptFor(Collection<TestableTask> tasks) {
        List<TestableTask> result = new ArrayList<TestableTask>(Arrays.asList(TestableTask.values()));
        result.removeAll(tasks);
        return result;
    }

    public static List<TestableTask> getTasksWithMultipleSouceFiles() {
        return getTasksWith(TestableTask::hasMultiplePdfSource);
    }

    public static List<TestableTask> getTasksWithSingleSouceFiles() {
        return getTasksWith(TestableTask::hasSinglePdfSource);
    }

    public static List<TestableTask> getTasksWithFolderOutputAndPdfInput() {
        return getTasksWith(t -> !t.hasMultipleSource() && t.hasFolderOutput());
    }

    public static List<TestableTask> getTasksWithPrefixableOutput() {
        return getTasksWith(TestableTask::hasPrefixableOutput);
    }

    public static List<TestableTask> getTasksWith(Predicate<? super TestableTask> p) {
        return Arrays.stream(TestableTask.values()).filter(p).collect(Collectors.toList());
    }

    boolean hasFolderOutput() {
        return isInheritingTraitsFrom(CliArgumentsWithDirectoryOutput.class);
    }

    boolean hasPrefixableOutput() {
        return isInheritingTraitsFrom(CliArgumentsWithPrefixableOutput.class);
    }

    boolean hasMultiplePdfSource() {
        return isInheritingTraitsFrom(MultipleOptionalPdfSourceTaskCliArguments.class)
                || isInheritingTraitsFrom(MultiplePdfSourceTaskCliArguments.class);
    }

    boolean hasMultipleSource() {
        return isInheritingTraitsFrom(MultipleSourceTaskCliArguments.class);
    }

    boolean hasSinglePdfSource() {
        return isInheritingTraitsFrom(SinglePdfSourceTaskCliArguments.class);
    }

    boolean isInheritingTraitsFrom(Class<?> parentClazz) {
        return parentClazz.isAssignableFrom(command.getCliArgumentsClass());
    }
}

@FunctionalInterface
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

    @Override
    public CommandLineTestBuilder provideDefaults(String taskName) {
        return new CommandLineTestBuilder(taskName).defaultTwoInputs().defaultFolderOutput();
    }

}

class MultipleInputsAndFileOutputDefaultsProvider implements DefaultsProvider {

    @Override
    public CommandLineTestBuilder provideDefaults(String taskName) {
        return new CommandLineTestBuilder(taskName).defaultTwoInputs().defaultFileOutput();
    }

}

class MultipleInputsAndFolderOutputDefaultsProvider implements DefaultsProvider {

    @Override
    public CommandLineTestBuilder provideDefaults(String taskName) {
        return new CommandLineTestBuilder(taskName).defaultTwoInputs().defaultFolderOutput();
    }
}

class SingleInputAndFolderOutputDefaultsProvider implements DefaultsProvider {

    @Override
    public CommandLineTestBuilder provideDefaults(String taskName) {
        return new CommandLineTestBuilder(taskName).defaultSingleInput().defaultFolderOutput();
    }
}

class SingleInputAndFileOutputDefaultsProvider implements DefaultsProvider {

    @Override
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

class ExtractByBookmarksDefaultsProvider extends MultipleInputsAndFolderOutputDefaultsProvider {
    @Override
    public CommandLineTestBuilder provideDefaults(String taskName) {
        return super.provideDefaults(taskName).with("-l", "1");
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

class ExtractPagesDefaultsProvider extends MultipleInputsAndFolderOutputDefaultsProvider {
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

class SetHeaderFooterDefaultsProvider extends MultipleInputsAndFolderOutputDefaultsProvider {
    @Override
    public CommandLineTestBuilder provideDefaults(String taskName) {
        return super.provideDefaults(taskName).with("-l", "\"Page [PAGE_OF_TOTAL]\"").with("-s", "all");
    }
}

class SetPageTransitionsDefaultsProvider extends SingleInputAndFileOutputDefaultsProvider {
    @Override
    public CommandLineTestBuilder provideDefaults(String taskName) {
        return super.provideDefaults(taskName).with("--transitions", "dissolve:6:9:55");
    }
}

class CropDefaultsProvider extends MultipleInputsAndFolderOutputDefaultsProvider {
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

class PdfToMultipleTiffDefaultsProvider extends MultipleInputsAndFolderOutputDefaultsProvider {
    @Override
    public CommandLineTestBuilder provideDefaults(String taskName) {
        return super.provideDefaults(taskName).with("--colorType", "gray_scale");
    }
}

class CombineReorderDefaultsProvider extends MultipleInputsAndFileOutputDefaultsProvider {
    @Override
    public CommandLineTestBuilder provideDefaults(String taskName) {
        return super.provideDefaults(taskName).with("-n", "0:1 1:1 0:2 1:3");
    }
}

class SplitByTextDefaultsProvider extends MultipleInputsAndFolderOutputDefaultsProvider {
    @Override
    public CommandLineTestBuilder provideDefaults(String taskName) {
        return super.provideDefaults(taskName).with("--top", "10").with("--left", "10").with("--width", "100")
                .with("--height", "10");
    }
}

class AddBackPagesDefaultsProvider extends MultipleInputsAndFolderOutputDefaultsProvider {
    @Override
    public CommandLineTestBuilder provideDefaults(String taskName) {
        return super.provideDefaults(taskName).with("-b", "inputs/back.pdf");
    }
}

class WatermarkDefaultsProvider extends MultipleInputsAndFolderOutputDefaultsProvider {
    @Override
    public CommandLineTestBuilder provideDefaults(String taskName) {
        return super.provideDefaults(taskName).with("-w", "inputs/logo.png").with("-c", "50,30");
    }
}

class ScaleDefaultsProvider extends MultipleInputsAndFolderOutputDefaultsProvider {
    @Override
    public CommandLineTestBuilder provideDefaults(String taskName) {
        return super.provideDefaults(taskName).with("-s", "1.1");
    }
}