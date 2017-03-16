/*
 * Created on 15 gen 2017
 * Copyright 2015 by Andrea Vacondio (andrea.vacondio@gmail.com).
 * This file is part of Sejda.
 *
 * Sejda is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Sejda is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with Sejda.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.sejda.cli.command;

import org.sejda.cli.CommandLineTestBuilder;
import org.sejda.cli.DefaultsProvider;

/**
 * {@link TestableTask} available in the standard sejda console
 * 
 * @author Andrea Vacondio
 */
public enum StandardTestableTask implements TestableTask {

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
    SET_METADATA(StandardCliCommand.SET_METADATA, new SetMetadataDefaultsProvider()),
    SET_PAGE_LABELS(StandardCliCommand.SET_PAGE_LABELS, new SetPageLabelsDefaultsProvider()),
    SET_PAGE_TRANSITIONS(StandardCliCommand.SET_PAGE_TRANSITIONS, new SetPageTransitionsDefaultsProvider()),
    PDF_TO_SINGLE_TIFF(StandardCliCommand.PDF_TO_SINGLE_TIFF, new PdfToSingleTiffDefaultsProvider()),
    PDF_TO_MULTIPLE_TIFF(StandardCliCommand.PDF_TO_MULTIPLE_TIFF, new PdfToMultipleTiffDefaultsProvider()),
    PDF_TO_JPEG(StandardCliCommand.PDF_TO_JPEG, new MultipleInputsAndFolderOutputDefaultsProvider()),
    PDF_TO_PNG(StandardCliCommand.PDF_TO_PNG, new MultipleInputsAndFolderOutputDefaultsProvider()),
    SET_HEADER_FOOTER(StandardCliCommand.SET_HEADER_FOOTER, new SetHeaderFooterDefaultsProvider()),
    COMBINE_REORDER(StandardCliCommand.COMBINE_REORDER, new CombineReorderDefaultsProvider()),
    ADD_BACK_PAGES(StandardCliCommand.ADD_BACK_PAGES, new AddBackPagesDefaultsProvider()),
    PORTFOLIO(StandardCliCommand.PORTFOLIO, taskName -> new CommandLineTestBuilder(taskName)
            .defaultMultipleNonPdfInputs().defaultFileOutput()),
    WATERMARK(StandardCliCommand.WATERMARK, new WatermarkDefaultsProvider()),
    SCALE(StandardCliCommand.SCALE, new ScaleDefaultsProvider());

    private final DefaultsProvider defaultsProvider;
    private final CliCommand command;

    private StandardTestableTask(CliCommand command) {
        this(command, new DefaultDefaultsProvider());
    }

    private StandardTestableTask(CliCommand command, DefaultsProvider defaultsProvider) {
        this.command = command;
        this.defaultsProvider = defaultsProvider;
    }

    @Override
    public DefaultsProvider getDefaultsProvider() {
        return defaultsProvider;
    }

    @Override
    public CliCommand getCommand() {
        return command;
    }

}

class RotateDefaultsProvider extends DefaultDefaultsProvider {
    @Override
    public CommandLineTestBuilder provideDefaults(String taskName) {
        return super.provideDefaults(taskName).with("-r", "90").with("-m", "all");
    }
}

class SplitByBookmarksDefaultsProvider extends SingleInputAndFolderOutputDefaultsProvider {
    @Override
    public CommandLineTestBuilder provideDefaults(String taskName) {
        return super.provideDefaults(taskName).with("-l", "1");
    }
}

class SplitBySizeDefaultsProvider extends MultipleInputsAndFolderOutputDefaultsProvider {
    @Override
    public CommandLineTestBuilder provideDefaults(String taskName) {
        return super.provideDefaults(taskName).with("-s", "1234567890123456789");
    }
}

class SplitByPagesDefaultsProvider extends MultipleInputsAndFolderOutputDefaultsProvider {
    @Override
    public CommandLineTestBuilder provideDefaults(String taskName) {
        return super.provideDefaults(taskName).with("-n", "1 2 3 9 23 78");
    }
}

class SplitByEveryXPagesDefaultsProvider extends MultipleInputsAndFolderOutputDefaultsProvider {
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

class SimpleSplitDefaultsProvider extends MultipleInputsAndFolderOutputDefaultsProvider {
    @Override
    public CommandLineTestBuilder provideDefaults(String taskName) {
        return super.provideDefaults(taskName).with("-s", "all");
    }
}

class ExtractPagesDefaultsProvider extends MultipleInputsAndFolderOutputDefaultsProvider {
    @Override
    public CommandLineTestBuilder provideDefaults(String taskName) {
        return super.provideDefaults(taskName).with("-m", "all");
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
