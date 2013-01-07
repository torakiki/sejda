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
package org.sejda.cli.transformer;

import java.lang.reflect.ParameterizedType;
import java.util.SortedMap;
import java.util.TreeMap;

import org.apache.commons.lang3.StringUtils;
import org.sejda.cli.model.AlternateMixTaskCliArguments;
import org.sejda.cli.model.CliArgumentsWithDirectoryOutput;
import org.sejda.cli.model.CliArgumentsWithPrefixableOutput;
import org.sejda.cli.model.CropTaskCliArguments;
import org.sejda.cli.model.DecryptTaskCliArguments;
import org.sejda.cli.model.EncryptTaskCliArguments;
import org.sejda.cli.model.ExtractPagesTaskCliArguments;
import org.sejda.cli.model.ExtractTextTaskCliArguments;
import org.sejda.cli.model.MergeTaskCliArguments;
import org.sejda.cli.model.PdfToMultipleTiffTaskCliArguments;
import org.sejda.cli.model.PdfToSingleTiffTaskCliArguments;
import org.sejda.cli.model.RotateTaskCliArguments;
import org.sejda.cli.model.SetHeaderFooterTaskCliArguments;
import org.sejda.cli.model.SetMetadataTaskCliArguments;
import org.sejda.cli.model.SetPageLabelsTaskCliArguments;
import org.sejda.cli.model.SetPageTransitionsTaskCliArguments;
import org.sejda.cli.model.SimpleSplitTaskCliArguments;
import org.sejda.cli.model.SplitByBookmarksTaskCliArguments;
import org.sejda.cli.model.SplitByPagesTaskCliArguments;
import org.sejda.cli.model.SplitBySizeTaskCliArguments;
import org.sejda.cli.model.TaskCliArguments;
import org.sejda.cli.model.UnpackTaskCliArguments;
import org.sejda.cli.model.ViewerPreferencesTaskCliArguments;
import org.sejda.model.parameter.AlternateMixParameters;
import org.sejda.model.parameter.CropParameters;
import org.sejda.model.parameter.DecryptParameters;
import org.sejda.model.parameter.EncryptParameters;
import org.sejda.model.parameter.ExtractPagesParameters;
import org.sejda.model.parameter.ExtractTextParameters;
import org.sejda.model.parameter.MergeParameters;
import org.sejda.model.parameter.RotateParameters;
import org.sejda.model.parameter.SetHeaderFooterParameters;
import org.sejda.model.parameter.SetMetadataParameters;
import org.sejda.model.parameter.SetPagesLabelParameters;
import org.sejda.model.parameter.SetPagesTransitionParameters;
import org.sejda.model.parameter.SimpleSplitParameters;
import org.sejda.model.parameter.SplitByGoToActionLevelParameters;
import org.sejda.model.parameter.SplitByPagesParameters;
import org.sejda.model.parameter.SplitBySizeParameters;
import org.sejda.model.parameter.UnpackParameters;
import org.sejda.model.parameter.ViewerPreferencesParameters;
import org.sejda.model.parameter.base.TaskParameters;
import org.sejda.model.parameter.image.PdfToMultipleTiffParameters;
import org.sejda.model.parameter.image.PdfToSingleTiffParameters;

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
        protected CommandCliArgumentsTransformer<DecryptTaskCliArguments, DecryptParameters> getArgumentsTransformer() {
            return new DecryptCliArgumentsTransformer();
        }
    }, "Given a collection of encrypted pdf documents and their owner password, creates a decrypted version of each of them.", "decrypt -f /tmp/file1.pdf:secret123 -o /tmp -p decrypted_"),
    ENCRYPT("encrypt", new CliInterfacedTask<EncryptTaskCliArguments, EncryptParameters>() {

        @Override
        protected CommandCliArgumentsTransformer<EncryptTaskCliArguments, EncryptParameters> getArgumentsTransformer() {
            return new EncryptCliArgumentsTransformer();
        }
    }, "Given a collection of pdf documents, applies the selected permission using the selected encryption algorithm and the provided owner and user password.", "encrypt -f /tmp/file1.pdf -o /tmp -u secret123 -a top-secret123 --allow print fill -e aes_128 -p encrypted_"),
    ROTATE("rotate", new CliInterfacedTask<RotateTaskCliArguments, RotateParameters>() {

        @Override
        protected CommandCliArgumentsTransformer<RotateTaskCliArguments, RotateParameters> getArgumentsTransformer() {
            return new RotateCliArgumentsTransformer();
        }
    }, "Apply page rotation to a collection of pdf documents. Rotation can be applied to a specified set of pages or to a predefined set (all, even pages, odd pages).", "rotate -f /tmp/file1.pdf -o /tmp -r all:180"),
    SET_VIEWER_PREFERENCES("setviewerpreferences", new CliInterfacedTask<ViewerPreferencesTaskCliArguments, ViewerPreferencesParameters>() {

        @Override
        protected CommandCliArgumentsTransformer<ViewerPreferencesTaskCliArguments, ViewerPreferencesParameters> getArgumentsTransformer() {
            return new ViewerPreferencesCliArgumentsTransformer();
        }
    }, "Given a collection of pdf documents, applies the selected viewer preferences.", "setviewerpreferences -f /tmp/file1.pdf -o /tmp --centerWindow --displayDocTitle --fitWindow --hideMenu --hideToolbar --hideWindowUI --layout onecolumn --mode fullscreen --nfsMode nfsthumbs"),
    ALTERNATE_MIX("alternatemix", new CliInterfacedTask<AlternateMixTaskCliArguments, AlternateMixParameters>() {

        @Override
        protected CommandCliArgumentsTransformer<AlternateMixTaskCliArguments, AlternateMixParameters> getArgumentsTransformer() {
            return new AlternateMixCliArgumentsTransformer();
        }
    }, "Given two pdf documents, creates a single output pdf document taking pages alternatively from the two input. Pages can be taken in straight or reverse order and using a configurable step (number of pages before the process switch from a document to the other).", "alternatemix -f /tmp/file1.pdf /tmp/file2.pdf -o /tmp/output.pdf --reverseSecond"),
    UNPACK("unpack", new CliInterfacedTask<UnpackTaskCliArguments, UnpackParameters>() {

        @Override
        protected CommandCliArgumentsTransformer<UnpackTaskCliArguments, UnpackParameters> getArgumentsTransformer() {
            return new UnpackCliArgumentsTransformer();
        }
    }, "Unpacks all the attachments of a given collection of pdf documents.", "unpack -f /tmp/file1.pdf -o /tmp"),
    MERGE("merge", new CliInterfacedTask<MergeTaskCliArguments, MergeParameters>() {

        @Override
        protected CommandCliArgumentsTransformer<MergeTaskCliArguments, MergeParameters> getArgumentsTransformer() {
            return new MergeCliArgumentsTransformer();
        }
    }, "Given a collection of pdf documents, creates a single output pdf document composed by the selected pages of each input document taken in the given order.", "merge -f /tmp/file1.pdf /tmp/file2.pdf -o /tmp/output.pdf -s all:12-14:32,12-14,4,34-:"),
    SPLIT_BY_BOOKMARKS("splitbybookmarks", new CliInterfacedTask<SplitByBookmarksTaskCliArguments, SplitByGoToActionLevelParameters>() {

        @Override
        protected CommandCliArgumentsTransformer<SplitByBookmarksTaskCliArguments, SplitByGoToActionLevelParameters> getArgumentsTransformer() {
            return new SplitByBookmarksCliArgumentsTransformer();
        }
    }, "Splits a given pdf document at pages where exists a GoTo action in the document outline (bookmarks) at the specified level (optionally matching a provided regular expression).", "splitbybookmarks -f /tmp/file1.pdf -o /tmp -l 2 -e \".+(page)+.+\""),
    SPLIT_BY_SIZE("splitbysize", new CliInterfacedTask<SplitBySizeTaskCliArguments, SplitBySizeParameters>() {

        @Override
        protected CommandCliArgumentsTransformer<SplitBySizeTaskCliArguments, SplitBySizeParameters> getArgumentsTransformer() {
            return new SplitBySizeCliArgumentsTransformer();
        }
    }, "Splits a given pdf document in files of the selected size (roughly).", "splitbysize -f /tmp/file1.pdf -o /tmp -s 10000"),
    SPLIT_BY_PAGES("splitbypages", new CliInterfacedTask<SplitByPagesTaskCliArguments, SplitByPagesParameters>() {

        @Override
        protected CommandCliArgumentsTransformer<SplitByPagesTaskCliArguments, SplitByPagesParameters> getArgumentsTransformer() {
            return new SplitByPagesCliArgumentsTransformer();
        }
    }, "Splits a given pdf document at a selected set of page numbers.", "splitbypages -f /tmp/file1.pdf -o /tmp -n 1 3 5 99"),
    SIMPLE_SPLIT("simplesplit", new CliInterfacedTask<SimpleSplitTaskCliArguments, SimpleSplitParameters>() {

        @Override
        protected CommandCliArgumentsTransformer<SimpleSplitTaskCliArguments, SimpleSplitParameters> getArgumentsTransformer() {
            return new SimpleSplitCliArgumentsTransformer();
        }
    }, "Splits a given pdf document at a predefined set of page numbers (all, odd pages, even pages).", "simplesplit -f /tmp/file1.pdf -o /tmp -s odd"),
    EXTRACT_PAGES("extractpages", new CliInterfacedTask<ExtractPagesTaskCliArguments, ExtractPagesParameters>() {

        @Override
        protected CommandCliArgumentsTransformer<ExtractPagesTaskCliArguments, ExtractPagesParameters> getArgumentsTransformer() {
            return new ExtractPagesCliArgumentsTransformer();
        }
    }, "Extract pages from a pdf document creating a new one containing only the selected pages. Page selection can be done using a predefined set of pages (odd, even) or as a set of ranges (from page x to y).", "extractpages -f /tmp/file1.pdf -o /tmp/output.pdf -s 1-4,7,12-14,8,20-"),
    EXTRACT_TEXT("extracttext", new CliInterfacedTask<ExtractTextTaskCliArguments, ExtractTextParameters>() {

        @Override
        protected CommandCliArgumentsTransformer<ExtractTextTaskCliArguments, ExtractTextParameters> getArgumentsTransformer() {
            return new ExtractTextCliArgumentsTransformer();
        }

    }, "Given a collection of pdf documents, creates a collection of text files containing text extracted from them.", "extracttext -f /tmp/file1.pdf -o /tmp -e \"ISO-8859-1\""),
    SET_METADATA("setmetadata", new CliInterfacedTask<SetMetadataTaskCliArguments, SetMetadataParameters>() {

        @Override
        protected CommandCliArgumentsTransformer<SetMetadataTaskCliArguments, SetMetadataParameters> getArgumentsTransformer() {
            return new SetMetadataCliArgumentsTransformer();
        }
    }, "Apply new metadata (title, author, subject, keywords) to an input pdf document.", "setmetadata -f /tmp/file1.pdf -o /tmp/output.pdf --subject \"Subject of the document\" --keywords \"\""),
    SET_PAGE_LABELS("setpagelabels", new CliInterfacedTask<SetPageLabelsTaskCliArguments, SetPagesLabelParameters>() {

        @Override
        protected CommandCliArgumentsTransformer<SetPageLabelsTaskCliArguments, SetPagesLabelParameters> getArgumentsTransformer() {
            return new SetPageLabelsCliArgumentsTransformer();
        }
    }, "Given a collection of pdf documents, applies the selected page labels as defined in the Pdf reference 1.7, chapter 8.3.1.", "setpagelabels -f /tmp/file1.pdf -o /tmp/output.pdf -l \"1:uroman:1:Preface \" 5:arabic:1"),
    // \n\nThis would label the pages starting from the first page with uppercase roman numbers suffixed with 'Preface' ('Preface I', 'Preface II' etc.) and starting from the fifth
    // page (first logical page) with arabic numbers ('1', '2', etc.)
    SET_PAGE_TRANSITIONS("setpagetransitions", new CliInterfacedTask<SetPageTransitionsTaskCliArguments, SetPagesTransitionParameters>() {

        @Override
        protected CommandCliArgumentsTransformer<SetPageTransitionsTaskCliArguments, SetPagesTransitionParameters> getArgumentsTransformer() {
            return new SetPageTransitionsCliArgumentsTransformer();
        }
    }, "Given a pdf document, applies the selected pages transitions (to use the document as a slide show presentation) as defined in the Pdf reference 1.7, chapter 8.3.3.", "setpagetransitions -f /tmp/file1.pdf -o /tmp/output.pdf --defaultTransition fade:2:10 --transitions push_left_to_right:3:20:2 dissolve:1:10:3"),
    CROP("crop", new CliInterfacedTask<CropTaskCliArguments, CropParameters>() {

        @Override
        protected CommandCliArgumentsTransformer<CropTaskCliArguments, CropParameters> getArgumentsTransformer() {
            return new CropCliArgumentsTransformer();
        }
    }, "Given a pdf document and a set of rectangular boxes, creates a single output pdf document where pages are cropped according to the input rectangular boxes. Input boxes are set as mediabox and cropbox on the resulting document pages (see Pdf reference 1.7, chapter 3.6.2, TABLE 3.27). Resulting document will have a number of pages that is the the number of pages of the original document multiplied by the number of rectangular boxes.", "crop -f /tmp/file1.pdf -o /tmp/output.pdf --cropAreas [0:0][5:10] [5:0][10:10]"),
    PDF_TO_SINGLE_TIFF("pdftosingletiff", new CliInterfacedTask<PdfToSingleTiffTaskCliArguments, PdfToSingleTiffParameters>() {

        @Override
        protected CommandCliArgumentsTransformer<PdfToSingleTiffTaskCliArguments, PdfToSingleTiffParameters> getArgumentsTransformer() {
            return new PdfToSingleTiffCliArgumentsTransformer();
        }
    }, "Converts a pdf document to a single TIFF image (TIFF format supports multiple images written to a single file).", "pdftosingletiff -f /tmp/file1.pdf -o /tmp/output.tiff --compressionType ccitt_group_3_2d --colorType gray_scale"),
    PDF_TO_MULTIPLE_TIFF("pdftomultipletiff", new CliInterfacedTask<PdfToMultipleTiffTaskCliArguments, PdfToMultipleTiffParameters>() {

        @Override
        protected CommandCliArgumentsTransformer<PdfToMultipleTiffTaskCliArguments, PdfToMultipleTiffParameters> getArgumentsTransformer() {
            return new PdfToMultipleTiffCliArgumentsTransformer();
        }
    }, "Converts a pdf document to multiple TIFF images (one image per page).", "pdftomultipletiff -f /tmp/file1.pdf -o /tmp --compressionType ccitt_group_3_2d --colorType gray_scale"),
    SET_HEADER_FOOTER("setheaderfooter", new CliInterfacedTask<SetHeaderFooterTaskCliArguments, SetHeaderFooterParameters>() {

        @Override
        protected CommandCliArgumentsTransformer<SetHeaderFooterTaskCliArguments, SetHeaderFooterParameters> getArgumentsTransformer() {
            return new SetHeaderFooterCliArgumentsTransformer();
        }
    }, "Adds a header or a footer to a pdf document or part of it.", "setheaderfooter -f /tmp/file1.pdf -o /output.pdf -s 5- -ha right -t Curier -n 1:arabic -l \"some text\"");

    private String displayName;
    private String description;
    private String exampleUsage;
    private CliInterfacedTask<? extends TaskCliArguments, ? extends TaskParameters> cliInterfacedTask;

    private CliCommand(String displayName,
            CliInterfacedTask<? extends TaskCliArguments, ? extends TaskParameters> cliTask, String description,
            String exampleUsage) {
        this.displayName = displayName;
        this.exampleUsage = exampleUsage;
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
     * @return extended task description, explaining what the task does in detail, providing an example
     */
    public String getExampleUsage() {
        return exampleUsage;
    }

    public static CliCommand findByDisplayNameSilently(String displayName) {
        for (CliCommand eachCommand : CliCommand.values()) {
            if (StringUtils.equalsIgnoreCase(displayName, eachCommand.getDisplayName())) {
                return eachCommand;
            }
        }

        return null;
    }

    /**
     * @return all values, sorted by display name
     */
    public static CliCommand[] sortedValues() {
        SortedMap<String, CliCommand> map = new TreeMap<String, CliCommand>();
        for (CliCommand each : CliCommand.values()) {
            map.put(each.getDisplayName(), each);
        }
        return map.values().toArray(new CliCommand[map.values().size()]);
    }

    /**
     * @param rawArguments
     * @return task parameters out of the raw string arguments passed as input (removing the command argument for example)
     */
    public TaskParameters parseTaskParameters(String[] rawArguments) {
        return cliInterfacedTask.getTaskParameters(rawArguments);
    }

    /**
     * @return help message, detailing purpose, usage and parameter valid values
     */
    public String getHelpMessage() {
        StringBuilder result = new StringBuilder();

        result.append(getDescription());
        result.append("\n\n");

        result.append("Example usage: ").append(TaskCliArguments.EXECUTABLE_NAME).append(" ").append(getExampleUsage());
        result.append("\n\n");

        result.append(cliInterfacedTask.createCli().getHelpMessage());

        return result.toString();
    }

    public boolean hasFolderOutput() {
        return isInheritingTraitsFrom(CliArgumentsWithDirectoryOutput.class);
    }

    public boolean hasPrefixableOutput() {
        return isInheritingTraitsFrom(CliArgumentsWithPrefixableOutput.class);
    }

    boolean isInheritingTraitsFrom(Class<?> parentClazz) {
        return parentClazz.isAssignableFrom(getCliArgumentsClass());
    }

    public Class<?> getCliArgumentsClass() {
        return cliInterfacedTask.getCliArgumentsClass();
    }
}

/**
 * Base class defining the contract for {@link org.sejda.model.task.Task}s with a cli interface
 * 
 * @author Eduard Weissmann
 * 
 * @param <T>
 * @param <P>
 */
abstract class CliInterfacedTask<T extends TaskCliArguments, P extends TaskParameters> {

    @SuppressWarnings("unchecked")
    protected Class<T> getCliArgumentsClass() {
        // returning T.class see http://www.artima.com/weblogs/viewpost.jsp?thread=208860
        ParameterizedType parameterizedType = (ParameterizedType) getClass().getGenericSuperclass();
        return (Class<T>) parameterizedType.getActualTypeArguments()[0];
    }

    protected abstract CommandCliArgumentsTransformer<T, P> getArgumentsTransformer();

    protected Cli<T> createCli() {
        return CliFactory.createCli(getCliArgumentsClass());
    }

    protected P getTaskParameters(String[] rawArguments) {
        try {
            T cliArguments = createCli().parseArguments(rawArguments);
            return getArgumentsTransformer().toTaskParameters(cliArguments);
        } catch (uk.co.flamingpenguin.jewel.cli.ArgumentValidationException e) {
            throw new org.sejda.cli.exception.ArgumentValidationException(e.getMessage(), e);
        }
    }
}
