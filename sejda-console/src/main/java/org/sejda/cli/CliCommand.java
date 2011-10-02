/*
 * Created on Jul 22, 2011
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

import org.apache.commons.lang.StringUtils;
import org.sejda.core.manipulation.model.parameter.AlternateMixParameters;
import org.sejda.core.manipulation.model.parameter.CropParameters;
import org.sejda.core.manipulation.model.parameter.DecryptParameters;
import org.sejda.core.manipulation.model.parameter.EncryptParameters;
import org.sejda.core.manipulation.model.parameter.ExtractPagesParameters;
import org.sejda.core.manipulation.model.parameter.ExtractTextParameters;
import org.sejda.core.manipulation.model.parameter.MergeParameters;
import org.sejda.core.manipulation.model.parameter.RotateParameters;
import org.sejda.core.manipulation.model.parameter.SetMetadataParameters;
import org.sejda.core.manipulation.model.parameter.SetPagesLabelParameters;
import org.sejda.core.manipulation.model.parameter.SetPagesTransitionParameters;
import org.sejda.core.manipulation.model.parameter.SimpleSplitParameters;
import org.sejda.core.manipulation.model.parameter.SplitByGoToActionLevelParameters;
import org.sejda.core.manipulation.model.parameter.SplitByPagesParameters;
import org.sejda.core.manipulation.model.parameter.SplitBySizeParameters;
import org.sejda.core.manipulation.model.parameter.UnpackParameters;
import org.sejda.core.manipulation.model.parameter.ViewerPreferencesParameters;
import org.sejda.core.manipulation.model.parameter.base.TaskParameters;
import org.sejda.core.manipulation.model.parameter.image.PdfToMultipleTiffParameters;
import org.sejda.core.manipulation.model.parameter.image.PdfToSingleTiffParameters;

import uk.co.flamingpenguin.jewel.cli.ArgumentValidationException;
import uk.co.flamingpenguin.jewel.cli.Cli;
import uk.co.flamingpenguin.jewel.cli.CliFactory;

/**
 * Enumeration of commands supported through the console
 * 
 * @author Eduard Weissmann
 * 
 */
public enum CliCommand {

    DECRYPT("decrypt", new CliInterfacedTask<DecryptTaskCliArguments, DecryptParameters>() {

        @Override
        protected Class<DecryptTaskCliArguments> getCliArgumentsClass() {
            return DecryptTaskCliArguments.class;
        }

        @Override
        protected CommandCliArgumentsTransformer<DecryptTaskCliArguments, DecryptParameters> getArgumentsTransformer() {
            return new DecryptCliArgumentsTransformer();
        }
    }),
    ENCRYPT("encrypt", new CliInterfacedTask<EncryptTaskCliArguments, EncryptParameters>() {

        @Override
        protected Class<EncryptTaskCliArguments> getCliArgumentsClass() {
            return EncryptTaskCliArguments.class;
        }

        @Override
        protected CommandCliArgumentsTransformer<EncryptTaskCliArguments, EncryptParameters> getArgumentsTransformer() {
            return new EncryptCliArgumentsTransformer();
        }
    }),
    ROTATE("rotate", new CliInterfacedTask<RotateTaskCliArguments, RotateParameters>() {

        @Override
        protected Class<RotateTaskCliArguments> getCliArgumentsClass() {
            return RotateTaskCliArguments.class;
        }

        @Override
        protected CommandCliArgumentsTransformer<RotateTaskCliArguments, RotateParameters> getArgumentsTransformer() {
            return new RotateCliArgumentsTransformer();
        }
    }),
    SET_VIEWER_PREFERENCES("setviewerpreferences", new CliInterfacedTask<ViewerPreferencesTaskCliArguments, ViewerPreferencesParameters>() {
        @Override
        protected Class<ViewerPreferencesTaskCliArguments> getCliArgumentsClass() {
            return ViewerPreferencesTaskCliArguments.class;
        }

        @Override
        protected CommandCliArgumentsTransformer<ViewerPreferencesTaskCliArguments, ViewerPreferencesParameters> getArgumentsTransformer() {
            return new ViewerPreferencesCliArgumentsTransformer();
        }
    }),
    ALTERNATE_MIX("alternatemix", new CliInterfacedTask<AlternateMixTaskCliArguments, AlternateMixParameters>() {

        @Override
        protected Class<AlternateMixTaskCliArguments> getCliArgumentsClass() {
            return AlternateMixTaskCliArguments.class;
        }

        @Override
        protected CommandCliArgumentsTransformer<AlternateMixTaskCliArguments, AlternateMixParameters> getArgumentsTransformer() {
            return new AlternateMixCliArgumentsTransformer();
        }
    }),
    UNPACK("unpack", new CliInterfacedTask<UnpackTaskCliArguments, UnpackParameters>() {

        @Override
        protected Class<UnpackTaskCliArguments> getCliArgumentsClass() {
            return UnpackTaskCliArguments.class;
        }

        @Override
        protected CommandCliArgumentsTransformer<UnpackTaskCliArguments, UnpackParameters> getArgumentsTransformer() {
            return new UnpackCliArgumentsTransformer();
        }
    }),
    MERGE("merge", new CliInterfacedTask<MergeTaskCliArguments, MergeParameters>() {

        @Override
        protected Class<MergeTaskCliArguments> getCliArgumentsClass() {
            return MergeTaskCliArguments.class;
        }

        @Override
        protected CommandCliArgumentsTransformer<MergeTaskCliArguments, MergeParameters> getArgumentsTransformer() {
            return new MergeCliArgumentsTransformer();
        }
    }),
    SPLIT_BY_BOOKMARKS("splitbybookmarks", new CliInterfacedTask<SplitByBookmarksTaskCliArguments, SplitByGoToActionLevelParameters>() {

        @Override
        protected Class<SplitByBookmarksTaskCliArguments> getCliArgumentsClass() {
            return SplitByBookmarksTaskCliArguments.class;
        }

        @Override
        protected CommandCliArgumentsTransformer<SplitByBookmarksTaskCliArguments, SplitByGoToActionLevelParameters> getArgumentsTransformer() {
            return new SplitByBookmarksCliArgumentsTransformer();
        }
    }),
    SPLIT_BY_SIZE("splitbysize", new CliInterfacedTask<SplitBySizeTaskCliArguments, SplitBySizeParameters>() {

        @Override
        protected Class<SplitBySizeTaskCliArguments> getCliArgumentsClass() {
            return SplitBySizeTaskCliArguments.class;
        }

        @Override
        protected CommandCliArgumentsTransformer<SplitBySizeTaskCliArguments, SplitBySizeParameters> getArgumentsTransformer() {
            return new SplitBySizeCliArgumentsTransformer();
        }
    }),
    SPLIT_BY_PAGES("splitbypages", new CliInterfacedTask<SplitByPagesTaskCliArguments, SplitByPagesParameters>() {

        @Override
        protected Class<SplitByPagesTaskCliArguments> getCliArgumentsClass() {
            return SplitByPagesTaskCliArguments.class;
        }

        @Override
        protected CommandCliArgumentsTransformer<SplitByPagesTaskCliArguments, SplitByPagesParameters> getArgumentsTransformer() {
            return new SplitByPagesCliArgumentsTransformer();
        }
    }),
    SIMPLE_SPLIT("simplesplit", new CliInterfacedTask<SimpleSplitTaskCliArguments, SimpleSplitParameters>() {

        @Override
        protected Class<SimpleSplitTaskCliArguments> getCliArgumentsClass() {
            return SimpleSplitTaskCliArguments.class;
        }

        @Override
        protected CommandCliArgumentsTransformer<SimpleSplitTaskCliArguments, SimpleSplitParameters> getArgumentsTransformer() {
            return new SimpleSplitCliArgumentsTransformer();
        }
    }),
    EXTRACT_PAGES("extractpages", new CliInterfacedTask<ExtractPagesTaskCliArguments, ExtractPagesParameters>() {

        @Override
        protected Class<ExtractPagesTaskCliArguments> getCliArgumentsClass() {
            return ExtractPagesTaskCliArguments.class;
        }

        @Override
        protected CommandCliArgumentsTransformer<ExtractPagesTaskCliArguments, ExtractPagesParameters> getArgumentsTransformer() {
            return new ExtractPagesCliArgumentsTransformer();
        }
    }),
    EXTRACT_TEXT("extracttext", new CliInterfacedTask<ExtractTextTaskCliArguments, ExtractTextParameters>() {

        @Override
        protected Class<ExtractTextTaskCliArguments> getCliArgumentsClass() {
            return ExtractTextTaskCliArguments.class;
        }

        @Override
        protected CommandCliArgumentsTransformer<ExtractTextTaskCliArguments, ExtractTextParameters> getArgumentsTransformer() {
            return new ExtractTextCliArgumentsTransformer();
        }

    }),
    SET_METADATA("setmetadata", new CliInterfacedTask<SetMetadataTaskCliArguments, SetMetadataParameters>() {

        @Override
        protected Class<SetMetadataTaskCliArguments> getCliArgumentsClass() {
            return SetMetadataTaskCliArguments.class;
        }

        @Override
        protected CommandCliArgumentsTransformer<SetMetadataTaskCliArguments, SetMetadataParameters> getArgumentsTransformer() {
            return new SetMetadataCliArgumentsTransformer();
        }
    }),
    SET_PAGE_LABELS("setpagelabels", new CliInterfacedTask<SetPageLabelsTaskCliArguments, SetPagesLabelParameters>() {

        @Override
        protected Class<SetPageLabelsTaskCliArguments> getCliArgumentsClass() {
            return SetPageLabelsTaskCliArguments.class;
        }

        @Override
        protected CommandCliArgumentsTransformer<SetPageLabelsTaskCliArguments, SetPagesLabelParameters> getArgumentsTransformer() {
            return new SetPageLabelsCliArgumentsTransformer();
        }
    }),
    SET_PAGE_TRANSITIONS("setpagetransitions", new CliInterfacedTask<SetPageTransitionsTaskCliArguments, SetPagesTransitionParameters>() {

        @Override
        protected Class<SetPageTransitionsTaskCliArguments> getCliArgumentsClass() {
            return SetPageTransitionsTaskCliArguments.class;
        }

        @Override
        protected CommandCliArgumentsTransformer<SetPageTransitionsTaskCliArguments, SetPagesTransitionParameters> getArgumentsTransformer() {
            return new SetPageTransitionsCliArgumentsTransformer();
        }
    }),
    CROP("crop", new CliInterfacedTask<CropTaskCliArguments, CropParameters>() {

        @Override
        protected Class<CropTaskCliArguments> getCliArgumentsClass() {
            return CropTaskCliArguments.class;
        }

        @Override
        protected CommandCliArgumentsTransformer<CropTaskCliArguments, CropParameters> getArgumentsTransformer() {
            return new CropCliArgumentsTransformer();
        }
    }),
    PDF_TO_SINGLE_TIFF("pdftosingletiff", new CliInterfacedTask<PdfToSingleTiffTaskCliArguments, PdfToSingleTiffParameters>() {

        @Override
        protected Class<PdfToSingleTiffTaskCliArguments> getCliArgumentsClass() {
            return PdfToSingleTiffTaskCliArguments.class;
        }

        @Override
        protected CommandCliArgumentsTransformer<PdfToSingleTiffTaskCliArguments, PdfToSingleTiffParameters> getArgumentsTransformer() {
            return new PdfToSingleTiffCliArgumentsTransformer();
        }
    }),
    PDF_TO_MULTIPLE_TIFF("pdftomultipletiff", new CliInterfacedTask<PdfToMultipleTiffTaskCliArguments, PdfToMultipleTiffParameters>() {

        @Override
        protected Class<PdfToMultipleTiffTaskCliArguments> getCliArgumentsClass() {
            return PdfToMultipleTiffTaskCliArguments.class;
        }

        @Override
        protected CommandCliArgumentsTransformer<PdfToMultipleTiffTaskCliArguments, PdfToMultipleTiffParameters> getArgumentsTransformer() {
            return new PdfToMultipleTiffCliArgumentsTransformer();
        }
    });

    private String displayName;
    private CliInterfacedTask<? extends TaskCliArguments, ? extends TaskParameters> cliInterfacedTask;

    private CliCommand(String displayName,
            CliInterfacedTask<? extends TaskCliArguments, ? extends TaskParameters> cliTask) {
        this.displayName = displayName;
        this.cliInterfacedTask = cliTask;
    }

    /**
     * @return the user friendly name
     */
    public String getDisplayName() {
        return displayName;
    }

    /**
     * 
     * @throws IllegalArgumentException
     *             if no command exists with specified name
     */
    public static CliCommand findByDisplayName(String displayName) {
        for (CliCommand eachCommand : CliCommand.values()) {
            if (StringUtils.equalsIgnoreCase(displayName, eachCommand.getDisplayName())) {
                return eachCommand;
            }
        }

        throw new IllegalArgumentException("Unknown command: '" + displayName + "'");
    }

    /**
     * Creates task parameters out of the raw string arguments
     * 
     * @param rawArguments
     * @return
     * @throws ArgumentValidationException
     */
    public TaskParameters parseTaskParameters(String[] rawArguments) throws ArgumentValidationException {
        return cliInterfacedTask.getTaskParameters(rawArguments);
    }

    /**
     * @return
     */
    public String getHelpMessage() {
        return cliInterfacedTask.createCli().getHelpMessage();
    }
}

/**
 * Base class defining the contract for {@link org.sejda.core.manipulation.model.task.Task}s with a cli interface
 * 
 * @author Eduard Weissmann
 * 
 * @param <T>
 * @param <P>
 */
abstract class CliInterfacedTask<T extends TaskCliArguments, P extends TaskParameters> {

    // TODO: figure out a way to simply return T.class
    protected abstract Class<T> getCliArgumentsClass();

    protected abstract CommandCliArgumentsTransformer<T, P> getArgumentsTransformer();

    protected Cli<T> createCli() {
        return CliFactory.createCli(getCliArgumentsClass());
    }

    protected P getTaskParameters(String[] rawArguments) throws ArgumentValidationException {
        T cliArguments = createCli().parseArguments(rawArguments);
        return getArgumentsTransformer().toTaskParameters(cliArguments);
    }
}
