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

import java.util.SortedMap;
import java.util.TreeMap;

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
// TODO: Docs: Detail descriptions that refer back to the pdf specs in a more user-friendly way. Keep this is sync with the website
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
    }, "Given a collection of encrypted pdf documents and their owner password, creates a decrypted version of each of them."),
    ENCRYPT("encrypt", new CliInterfacedTask<EncryptTaskCliArguments, EncryptParameters>() {

        @Override
        protected Class<EncryptTaskCliArguments> getCliArgumentsClass() {
            return EncryptTaskCliArguments.class;
        }

        @Override
        protected CommandCliArgumentsTransformer<EncryptTaskCliArguments, EncryptParameters> getArgumentsTransformer() {
            return new EncryptCliArgumentsTransformer();
        }
    }, "Given a collection of pdf documents, applies the selected permission using the selected encryption algorithm and the provided owner and user password."),
    ROTATE("rotate", new CliInterfacedTask<RotateTaskCliArguments, RotateParameters>() {

        @Override
        protected Class<RotateTaskCliArguments> getCliArgumentsClass() {
            return RotateTaskCliArguments.class;
        }

        @Override
        protected CommandCliArgumentsTransformer<RotateTaskCliArguments, RotateParameters> getArgumentsTransformer() {
            return new RotateCliArgumentsTransformer();
        }
    }, "Apply page rotation to a collection of pdf documents. Rotation can be applied to a specified set of pages or to a predefined set (all, even pages, odd pages)"),
    SET_VIEWER_PREFERENCES("setviewerpreferences", new CliInterfacedTask<ViewerPreferencesTaskCliArguments, ViewerPreferencesParameters>() {
        @Override
        protected Class<ViewerPreferencesTaskCliArguments> getCliArgumentsClass() {
            return ViewerPreferencesTaskCliArguments.class;
        }

        @Override
        protected CommandCliArgumentsTransformer<ViewerPreferencesTaskCliArguments, ViewerPreferencesParameters> getArgumentsTransformer() {
            return new ViewerPreferencesCliArgumentsTransformer();
        }
    }, "Given a collection of pdf documents, applies the selected viewer preferences."),
    ALTERNATE_MIX("alternatemix", new CliInterfacedTask<AlternateMixTaskCliArguments, AlternateMixParameters>() {

        @Override
        protected Class<AlternateMixTaskCliArguments> getCliArgumentsClass() {
            return AlternateMixTaskCliArguments.class;
        }

        @Override
        protected CommandCliArgumentsTransformer<AlternateMixTaskCliArguments, AlternateMixParameters> getArgumentsTransformer() {
            return new AlternateMixCliArgumentsTransformer();
        }
    }, "Given two pdf documents, creates a single output pdf document taking pages alternatively from the two input. Pages can be taken in straight or reverse order and using a configurable step (number of pages before the process switch from a document to the other)."),
    UNPACK("unpack", new CliInterfacedTask<UnpackTaskCliArguments, UnpackParameters>() {

        @Override
        protected Class<UnpackTaskCliArguments> getCliArgumentsClass() {
            return UnpackTaskCliArguments.class;
        }

        @Override
        protected CommandCliArgumentsTransformer<UnpackTaskCliArguments, UnpackParameters> getArgumentsTransformer() {
            return new UnpackCliArgumentsTransformer();
        }
    }, "Unpacks all the attachments of a given collection of pdf documents."),
    MERGE("merge", new CliInterfacedTask<MergeTaskCliArguments, MergeParameters>() {

        @Override
        protected Class<MergeTaskCliArguments> getCliArgumentsClass() {
            return MergeTaskCliArguments.class;
        }

        @Override
        protected CommandCliArgumentsTransformer<MergeTaskCliArguments, MergeParameters> getArgumentsTransformer() {
            return new MergeCliArgumentsTransformer();
        }
    }, "Given a collection of pdf documents, creates a single output pdf document composed by the selected pages of each input document taken in the given order."),
    SPLIT_BY_BOOKMARKS("splitbybookmarks", new CliInterfacedTask<SplitByBookmarksTaskCliArguments, SplitByGoToActionLevelParameters>() {

        @Override
        protected Class<SplitByBookmarksTaskCliArguments> getCliArgumentsClass() {
            return SplitByBookmarksTaskCliArguments.class;
        }

        @Override
        protected CommandCliArgumentsTransformer<SplitByBookmarksTaskCliArguments, SplitByGoToActionLevelParameters> getArgumentsTransformer() {
            return new SplitByBookmarksCliArgumentsTransformer();
        }
    }, "Splits a given pdf document at pages where exists a GoTo action in the document outline (bookmarks) at the specified level (optionally matching a provided regular expression)"),
    SPLIT_BY_SIZE("splitbysize", new CliInterfacedTask<SplitBySizeTaskCliArguments, SplitBySizeParameters>() {

        @Override
        protected Class<SplitBySizeTaskCliArguments> getCliArgumentsClass() {
            return SplitBySizeTaskCliArguments.class;
        }

        @Override
        protected CommandCliArgumentsTransformer<SplitBySizeTaskCliArguments, SplitBySizeParameters> getArgumentsTransformer() {
            return new SplitBySizeCliArgumentsTransformer();
        }
    }, "Splits a given pdf document in files of the selected size (roughly)."),
    SPLIT_BY_PAGES("splitbypages", new CliInterfacedTask<SplitByPagesTaskCliArguments, SplitByPagesParameters>() {

        @Override
        protected Class<SplitByPagesTaskCliArguments> getCliArgumentsClass() {
            return SplitByPagesTaskCliArguments.class;
        }

        @Override
        protected CommandCliArgumentsTransformer<SplitByPagesTaskCliArguments, SplitByPagesParameters> getArgumentsTransformer() {
            return new SplitByPagesCliArgumentsTransformer();
        }
    }, "Splits a given pdf document at a selected set of page numbers."),
    SIMPLE_SPLIT("simplesplit", new CliInterfacedTask<SimpleSplitTaskCliArguments, SimpleSplitParameters>() {

        @Override
        protected Class<SimpleSplitTaskCliArguments> getCliArgumentsClass() {
            return SimpleSplitTaskCliArguments.class;
        }

        @Override
        protected CommandCliArgumentsTransformer<SimpleSplitTaskCliArguments, SimpleSplitParameters> getArgumentsTransformer() {
            return new SimpleSplitCliArgumentsTransformer();
        }
    }, "Splits a given pdf document at a predefined set of page numbers (all, odd pages, even pages)."),
    EXTRACT_PAGES("extractpages", new CliInterfacedTask<ExtractPagesTaskCliArguments, ExtractPagesParameters>() {

        @Override
        protected Class<ExtractPagesTaskCliArguments> getCliArgumentsClass() {
            return ExtractPagesTaskCliArguments.class;
        }

        @Override
        protected CommandCliArgumentsTransformer<ExtractPagesTaskCliArguments, ExtractPagesParameters> getArgumentsTransformer() {
            return new ExtractPagesCliArgumentsTransformer();
        }
    }, "Extract pages from a pdf document creating a new one containing only the selected pages. Page selection can be done using a predefined set of pages (odd, even) or as a set of ranges (from page x to y)."),
    EXTRACT_TEXT("extracttext", new CliInterfacedTask<ExtractTextTaskCliArguments, ExtractTextParameters>() {

        @Override
        protected Class<ExtractTextTaskCliArguments> getCliArgumentsClass() {
            return ExtractTextTaskCliArguments.class;
        }

        @Override
        protected CommandCliArgumentsTransformer<ExtractTextTaskCliArguments, ExtractTextParameters> getArgumentsTransformer() {
            return new ExtractTextCliArgumentsTransformer();
        }

    }, "Given a collection of pdf documents, creates a collection of text files containing text extracted from them."),
    SET_METADATA("setmetadata", new CliInterfacedTask<SetMetadataTaskCliArguments, SetMetadataParameters>() {

        @Override
        protected Class<SetMetadataTaskCliArguments> getCliArgumentsClass() {
            return SetMetadataTaskCliArguments.class;
        }

        @Override
        protected CommandCliArgumentsTransformer<SetMetadataTaskCliArguments, SetMetadataParameters> getArgumentsTransformer() {
            return new SetMetadataCliArgumentsTransformer();
        }
    }, "Apply new metadata (title, author, subject, keywords) to an input pdf document."),
    SET_PAGE_LABELS("setpagelabels", new CliInterfacedTask<SetPageLabelsTaskCliArguments, SetPagesLabelParameters>() {

        @Override
        protected Class<SetPageLabelsTaskCliArguments> getCliArgumentsClass() {
            return SetPageLabelsTaskCliArguments.class;
        }

        @Override
        protected CommandCliArgumentsTransformer<SetPageLabelsTaskCliArguments, SetPagesLabelParameters> getArgumentsTransformer() {
            return new SetPageLabelsCliArgumentsTransformer();
        }
    }, "Given a collection of pdf documents, applies the selected page labels as defined in the Pdf reference 1.7, chapter 8.3.1."),
    SET_PAGE_TRANSITIONS("setpagetransitions", new CliInterfacedTask<SetPageTransitionsTaskCliArguments, SetPagesTransitionParameters>() {

        @Override
        protected Class<SetPageTransitionsTaskCliArguments> getCliArgumentsClass() {
            return SetPageTransitionsTaskCliArguments.class;
        }

        @Override
        protected CommandCliArgumentsTransformer<SetPageTransitionsTaskCliArguments, SetPagesTransitionParameters> getArgumentsTransformer() {
            return new SetPageTransitionsCliArgumentsTransformer();
        }
    }, "Given a pdf document, applies the selected pages transitions (to use the document as a slide show presentation) as defined in the Pdf reference 1.7, chapter 8.3.3."),
    CROP("crop", new CliInterfacedTask<CropTaskCliArguments, CropParameters>() {

        @Override
        protected Class<CropTaskCliArguments> getCliArgumentsClass() {
            return CropTaskCliArguments.class;
        }

        @Override
        protected CommandCliArgumentsTransformer<CropTaskCliArguments, CropParameters> getArgumentsTransformer() {
            return new CropCliArgumentsTransformer();
        }
    }, "Given a pdf document and a set of rectangular boxes, creates a single output pdf document where pages are cropped according to the input rectangular boxes. Input boxes are set as mediabox and cropbox on the resulting document pages (see Pdf reference 1.7, chapter 3.6.2, TABLE 3.27). Resulting document will have a number of pages that is the the number of pages of the original document multiplied by the number of rectangular boxes."),
    PDF_TO_SINGLE_TIFF("pdftosingletiff", new CliInterfacedTask<PdfToSingleTiffTaskCliArguments, PdfToSingleTiffParameters>() {

        @Override
        protected Class<PdfToSingleTiffTaskCliArguments> getCliArgumentsClass() {
            return PdfToSingleTiffTaskCliArguments.class;
        }

        @Override
        protected CommandCliArgumentsTransformer<PdfToSingleTiffTaskCliArguments, PdfToSingleTiffParameters> getArgumentsTransformer() {
            return new PdfToSingleTiffCliArgumentsTransformer();
        }
    }, "Converts a pdf document to a single TIFF image (TIFF format supports multiple images written to a single file)."),
    PDF_TO_MULTIPLE_TIFF("pdftomultipletiff", new CliInterfacedTask<PdfToMultipleTiffTaskCliArguments, PdfToMultipleTiffParameters>() {

        @Override
        protected Class<PdfToMultipleTiffTaskCliArguments> getCliArgumentsClass() {
            return PdfToMultipleTiffTaskCliArguments.class;
        }

        @Override
        protected CommandCliArgumentsTransformer<PdfToMultipleTiffTaskCliArguments, PdfToMultipleTiffParameters> getArgumentsTransformer() {
            return new PdfToMultipleTiffCliArgumentsTransformer();
        }
    }, "Converts a pdf document to multiple TIFF images (one image per page).");

    private String displayName;
    private String description;
    private CliInterfacedTask<? extends TaskCliArguments, ? extends TaskParameters> cliInterfacedTask;

    private CliCommand(String displayName,
            CliInterfacedTask<? extends TaskCliArguments, ? extends TaskParameters> cliTask, String description) {
        this.displayName = displayName;
        this.cliInterfacedTask = cliTask;
        this.description = description;
    }

    /**
     * @return the user friendly name
     */
    public String getDisplayName() {
        return displayName;
    }

    /**
     * @return task description, explaining what the task does in a nutshell
     */
    public String getDescription() {
        return description;
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
     * @return all values, sorted by display name
     */
    public static CliCommand[] sortedValues() {
        SortedMap<String, CliCommand> map = new TreeMap<String, CliCommand>();
        for (CliCommand each : CliCommand.values()) {
            map.put(each.getDisplayName(), each);
        }
        return map.values().toArray(new CliCommand[] {});
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
