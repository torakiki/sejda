/*
 * Created on Jul 22, 2011
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
package org.sejda.cli.transformer;

import java.lang.reflect.ParameterizedType;
import java.util.SortedMap;
import java.util.TreeMap;

import org.apache.commons.lang3.StringUtils;
import org.sejda.cli.model.AddBackPagesTaskCliArguments;
import org.sejda.cli.model.AlternateMixTaskCliArguments;
import org.sejda.cli.model.AttachmentsCollectionTaskCliArguments;
import org.sejda.cli.model.CliArgumentsWithDirectoryOutput;
import org.sejda.cli.model.CliArgumentsWithPrefixableOutput;
import org.sejda.cli.model.CombineReorderTaskCliArguments;
import org.sejda.cli.model.CompressTaskCliArguments;
import org.sejda.cli.model.CropTaskCliArguments;
import org.sejda.cli.model.DecryptTaskCliArguments;
import org.sejda.cli.model.EncryptTaskCliArguments;
import org.sejda.cli.model.ExtractByBookmarksTaskCliArguments;
import org.sejda.cli.model.ExtractPagesTaskCliArguments;
import org.sejda.cli.model.ExtractTextByPagesTaskCliArguments;
import org.sejda.cli.model.ExtractTextTaskCliArguments;
import org.sejda.cli.model.MergeTaskCliArguments;
import org.sejda.cli.model.MultipleOptionalPdfSourceTaskCliArguments;
import org.sejda.cli.model.MultiplePdfSourceTaskCliArguments;
import org.sejda.cli.model.MultipleSourceTaskCliArguments;
import org.sejda.cli.model.NupTaskCliArguments;
import org.sejda.cli.model.PdfToJpegTaskCliArguments;
import org.sejda.cli.model.PdfToMultipleTiffTaskCliArguments;
import org.sejda.cli.model.PdfToSingleTiffTaskCliArguments;
import org.sejda.cli.model.RotateTaskCliArguments;
import org.sejda.cli.model.SetHeaderFooterTaskCliArguments;
import org.sejda.cli.model.SetMetadataTaskCliArguments;
import org.sejda.cli.model.SetPageLabelsTaskCliArguments;
import org.sejda.cli.model.SetPageTransitionsTaskCliArguments;
import org.sejda.cli.model.SimpleSplitTaskCliArguments;
import org.sejda.cli.model.SinglePdfSourceTaskCliArguments;
import org.sejda.cli.model.SplitByBookmarksTaskCliArguments;
import org.sejda.cli.model.SplitByEveryXPagesTaskCliArguments;
import org.sejda.cli.model.SplitByPagesTaskCliArguments;
import org.sejda.cli.model.SplitBySizeTaskCliArguments;
import org.sejda.cli.model.SplitByTextTaskCliArguments;
import org.sejda.cli.model.SplitDownTheMiddleTaskCliArguments;
import org.sejda.cli.model.TaskCliArguments;
import org.sejda.cli.model.UnpackTaskCliArguments;
import org.sejda.cli.model.ViewerPreferencesTaskCliArguments;
import org.sejda.cli.model.WatermarkTaskCliArguments;
import org.sejda.model.parameter.AddBackPagesParameters;
import org.sejda.model.parameter.AlternateMixParameters;
import org.sejda.model.parameter.AttachmentsCollectionParameters;
import org.sejda.model.parameter.CombineReorderParameters;
import org.sejda.model.parameter.CropParameters;
import org.sejda.model.parameter.DecryptParameters;
import org.sejda.model.parameter.EncryptParameters;
import org.sejda.model.parameter.ExtractByOutlineParameters;
import org.sejda.model.parameter.ExtractPagesParameters;
import org.sejda.model.parameter.ExtractTextByPagesParameters;
import org.sejda.model.parameter.ExtractTextParameters;
import org.sejda.model.parameter.MergeParameters;
import org.sejda.model.parameter.NupParameters;
import org.sejda.model.parameter.OptimizeParameters;
import org.sejda.model.parameter.RotateParameters;
import org.sejda.model.parameter.SetHeaderFooterParameters;
import org.sejda.model.parameter.SetMetadataParameters;
import org.sejda.model.parameter.SetPagesLabelParameters;
import org.sejda.model.parameter.SetPagesTransitionParameters;
import org.sejda.model.parameter.SimpleSplitParameters;
import org.sejda.model.parameter.SplitByEveryXPagesParameters;
import org.sejda.model.parameter.SplitByOutlineLevelParameters;
import org.sejda.model.parameter.SplitByPagesParameters;
import org.sejda.model.parameter.SplitBySizeParameters;
import org.sejda.model.parameter.SplitByTextContentParameters;
import org.sejda.model.parameter.SplitDownTheMiddleParameters;
import org.sejda.model.parameter.UnpackParameters;
import org.sejda.model.parameter.ViewerPreferencesParameters;
import org.sejda.model.parameter.WatermarkParameters;
import org.sejda.model.parameter.base.TaskParameters;
import org.sejda.model.parameter.image.PdfToJpegParameters;
import org.sejda.model.parameter.image.PdfToMultipleTiffParameters;
import org.sejda.model.parameter.image.PdfToSingleTiffParameters;

import com.lexicalscope.jewel.cli.Cli;
import com.lexicalscope.jewel.cli.CliFactory;

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
    }, "Given a collection of encrypted PDF documents and their owner password, creates a decrypted version of each of them.", "decrypt -f /tmp/file1.pdf:secret123 -o /tmp -p decrypted_"),
    ENCRYPT("encrypt", new CliInterfacedTask<EncryptTaskCliArguments, EncryptParameters>() {

        @Override
        protected CommandCliArgumentsTransformer<EncryptTaskCliArguments, EncryptParameters> getArgumentsTransformer() {
            return new EncryptCliArgumentsTransformer();
        }
    }, "Given a collection of PDF documents, applies the selected permission using the selected encryption algorithm and the provided owner and user password.", "encrypt -f /tmp/file1.pdf -o /tmp -u secret123 -a top-secret123 --allow print fill -e aes_128 -p encrypted_"),
    ROTATE("rotate", new CliInterfacedTask<RotateTaskCliArguments, RotateParameters>() {

        @Override
        protected CommandCliArgumentsTransformer<RotateTaskCliArguments, RotateParameters> getArgumentsTransformer() {
            return new RotateCliArgumentsTransformer();
        }
    }, "Apply page rotation to a collection of PDF documents. Rotation can be applied to a specified set of pages or to a predefined set (all, even pages, odd pages).", "rotate -f /tmp/file1.pdf -o /tmp -r 180 -m all"),
    SET_VIEWER_PREFERENCES("setviewerpreferences", new CliInterfacedTask<ViewerPreferencesTaskCliArguments, ViewerPreferencesParameters>() {

        @Override
        protected CommandCliArgumentsTransformer<ViewerPreferencesTaskCliArguments, ViewerPreferencesParameters> getArgumentsTransformer() {
            return new ViewerPreferencesCliArgumentsTransformer();
        }
    }, "Given a collection of PDF documents, applies the selected viewer preferences.", "setviewerpreferences -f /tmp/file1.pdf -o /tmp --centerWindow --displayDocTitle --fitWindow --hideMenu --hideToolbar --hideWindowUI --layout onecolumn --mode fullscreen --nfsMode nfsthumbs"),
    ALTERNATE_MIX("alternatemix", new CliInterfacedTask<AlternateMixTaskCliArguments, AlternateMixParameters>() {

        @Override
        protected CommandCliArgumentsTransformer<AlternateMixTaskCliArguments, AlternateMixParameters> getArgumentsTransformer() {
            return new AlternateMixCliArgumentsTransformer();
        }
    }, "Given two PDF documents, creates a single output PDF document taking pages alternatively from the two input. Pages can be taken in straight or reverse order and using a configurable step (number of pages before the process switch from a document to the other).", "alternatemix -f /tmp/file1.pdf /tmp/file2.pdf -o /tmp/output.pdf --reverseSecond"),
    UNPACK("unpack", new CliInterfacedTask<UnpackTaskCliArguments, UnpackParameters>() {

        @Override
        protected CommandCliArgumentsTransformer<UnpackTaskCliArguments, UnpackParameters> getArgumentsTransformer() {
            return new UnpackCliArgumentsTransformer();
        }
    }, "Unpacks all the attachments of a given collection of PDF documents.", "unpack -f /tmp/file1.pdf -o /tmp"),
    MERGE("merge", new CliInterfacedTask<MergeTaskCliArguments, MergeParameters>() {

        @Override
        protected CommandCliArgumentsTransformer<MergeTaskCliArguments, MergeParameters> getArgumentsTransformer() {
            return new MergeCliArgumentsTransformer();
        }
    }, "Given a collection of PDF documents, creates a single output PDF document composed by the selected pages of each input document taken in the given order.", "merge -f /tmp/file1.pdf /tmp/file2.pdf -o /tmp/output.pdf -s all:12-14:32,12-14,4,34-:"),
    SPLIT_BY_BOOKMARKS("splitbybookmarks", new CliInterfacedTask<SplitByBookmarksTaskCliArguments, SplitByOutlineLevelParameters>() {

        @Override
        protected CommandCliArgumentsTransformer<SplitByBookmarksTaskCliArguments, SplitByOutlineLevelParameters> getArgumentsTransformer() {
            return new SplitByBookmarksCliArgumentsTransformer();
        }
    }, "Splits a given PDF document before each page that is a destination in the document outline (bookmarks) at the specified level (optionally matching a provided regular expression).", "splitbybookmarks -f /tmp/file1.pdf -o /tmp -l 2 -e \".+(Chapter)+.+\""),
    SPLIT_BY_SIZE("splitbysize", new CliInterfacedTask<SplitBySizeTaskCliArguments, SplitBySizeParameters>() {

        @Override
        protected CommandCliArgumentsTransformer<SplitBySizeTaskCliArguments, SplitBySizeParameters> getArgumentsTransformer() {
            return new SplitBySizeCliArgumentsTransformer();
        }
    }, "Splits a given PDF document in files of the selected size (roughly).", "splitbysize -f /tmp/file1.pdf -o /tmp -s 10000"),
    SPLIT_BY_PAGES("splitbypages", new CliInterfacedTask<SplitByPagesTaskCliArguments, SplitByPagesParameters>() {

        @Override
        protected CommandCliArgumentsTransformer<SplitByPagesTaskCliArguments, SplitByPagesParameters> getArgumentsTransformer() {
            return new SplitByPagesCliArgumentsTransformer();
        }
    }, "Splits a given PDF document after each one of the selected page numbers.", "splitbypages -f /tmp/file1.pdf -o /tmp -n 1 3 5 99"),
    SPLIT_BY_EVERY("splitbyevery", new CliInterfacedTask<SplitByEveryXPagesTaskCliArguments, SplitByEveryXPagesParameters>() {

        @Override
        protected CommandCliArgumentsTransformer<SplitByEveryXPagesTaskCliArguments, SplitByEveryXPagesParameters> getArgumentsTransformer() {
            return new SplitByEveryXPagesCliArgumentsTransformer();
        }
    }, "Splits a given PDF document every 'n' pages creating documents of 'n' pages each.", "splitbyevery -f /tmp/file1.pdf -o /tmp -n 2"),
    SIMPLE_SPLIT("simplesplit", new CliInterfacedTask<SimpleSplitTaskCliArguments, SimpleSplitParameters>() {

        @Override
        protected CommandCliArgumentsTransformer<SimpleSplitTaskCliArguments, SimpleSplitParameters> getArgumentsTransformer() {
            return new SimpleSplitCliArgumentsTransformer();
        }
    }, "Splits a given PDF document at a predefined set of page numbers (all, odd pages, even pages).", "simplesplit -f /tmp/file1.pdf -o /tmp -s odd"),
    EXTRACT_BY_BOOKMARKS("extractbybookmarks", new CliInterfacedTask<ExtractByBookmarksTaskCliArguments, ExtractByOutlineParameters>() {

        @Override
        protected CommandCliArgumentsTransformer<ExtractByBookmarksTaskCliArguments, ExtractByOutlineParameters> getArgumentsTransformer() {
            return new ExtractByBookmarksCliArgumentsTransformer();
        }
    }, "Extracts chapters to separate documents based on the bookmarks in the outline at a given level (optionally filtered by a given regex).", "extractbybookmarks -f /tmp/file1.pdf -o /tmp -l 2 -e \".+(Chapter)+.+\""),
    EXTRACT_PAGES("extractpages", new CliInterfacedTask<ExtractPagesTaskCliArguments, ExtractPagesParameters>() {

        @Override
        protected CommandCliArgumentsTransformer<ExtractPagesTaskCliArguments, ExtractPagesParameters> getArgumentsTransformer() {
            return new ExtractPagesCliArgumentsTransformer();
        }
    }, "Extracts pages from a PDF document creating a new one containing only the selected pages. Page selection can be done using a predefined set of pages (odd, even) or as a set of ranges (from page x to y).", "extractpages -f /tmp/file1.pdf -o /tmp -s 1-4,7,12-14,8,20-"),
    EXTRACT_TEXT("extracttext", new CliInterfacedTask<ExtractTextTaskCliArguments, ExtractTextParameters>() {

        @Override
        protected CommandCliArgumentsTransformer<ExtractTextTaskCliArguments, ExtractTextParameters> getArgumentsTransformer() {
            return new ExtractTextCliArgumentsTransformer();
        }

    }, "Given a collection of PDF documents, creates a collection of text files containing text extracted from them.", "extracttext -f /tmp/file1.pdf -o /tmp -e \"ISO-8859-1\""),
    EXTRACT_TEXT_BY_PAGES("extracttextbypages", new CliInterfacedTask<ExtractTextByPagesTaskCliArguments, ExtractTextByPagesParameters>() {

        @Override
        protected CommandCliArgumentsTransformer<ExtractTextByPagesTaskCliArguments, ExtractTextByPagesParameters> getArgumentsTransformer() {
            return new ExtractTextByPagesCliArgumentsTransformer();
        }

    }, "Extracts text from a single PDF document creating a collection of text files each containing text extracted from a single page.", "extracttextbypages -f /tmp/file1.pdf -o /tmp -e \"ISO-8859-1\" -s \"1,12-14\""),
    SET_METADATA("setmetadata", new CliInterfacedTask<SetMetadataTaskCliArguments, SetMetadataParameters>() {

        @Override
        protected CommandCliArgumentsTransformer<SetMetadataTaskCliArguments, SetMetadataParameters> getArgumentsTransformer() {
            return new SetMetadataCliArgumentsTransformer();
        }
    }, "Sets metadata (title, author, subject, keywords) to an input PDF document.", "setmetadata -f /tmp/file1.pdf -o /tmp/output.pdf --subject \"Subject of the document\" --keywords \"\""),
    SET_PAGE_LABELS("setpagelabels", new CliInterfacedTask<SetPageLabelsTaskCliArguments, SetPagesLabelParameters>() {

        @Override
        protected CommandCliArgumentsTransformer<SetPageLabelsTaskCliArguments, SetPagesLabelParameters> getArgumentsTransformer() {
            return new SetPageLabelsCliArgumentsTransformer();
        }
    }, "Given a collection of PDF documents, applies the selected page labels as defined in the PDF 32000-1:2008, chapter 12.4.2.", "setpagelabels -f /tmp/file1.pdf -o /tmp/output.pdf -l \"1:uroman:1:Preface \" 5:arabic:1"),
    // \n\nThis would label the pages starting from the first page with uppercase roman numbers suffixed with 'Preface' ('Preface I', 'Preface II' etc.) and starting from the fifth
    // page (first logical page) with arabic numbers ('1', '2', etc.)
    SET_PAGE_TRANSITIONS("setpagetransitions", new CliInterfacedTask<SetPageTransitionsTaskCliArguments, SetPagesTransitionParameters>() {

        @Override
        protected CommandCliArgumentsTransformer<SetPageTransitionsTaskCliArguments, SetPagesTransitionParameters> getArgumentsTransformer() {
            return new SetPageTransitionsCliArgumentsTransformer();
        }
    }, "Given a PDF document, applies the selected pages transitions (to use the document as a slide show presentation) as defined in the PDF 32000-1:2008, chapter 12.4.4.", "setpagetransitions -f /tmp/file1.pdf -o /tmp/output.pdf --defaultTransition fade:2:10 --transitions push_left_to_right:3:20:2 dissolve:1:10:3"),
    CROP("crop", new CliInterfacedTask<CropTaskCliArguments, CropParameters>() {

        @Override
        protected CommandCliArgumentsTransformer<CropTaskCliArguments, CropParameters> getArgumentsTransformer() {
            return new CropCliArgumentsTransformer();
        }
    }, "Given a PDF document and a set of rectangular boxes, creates an output PDF document where pages are cropped according to the input rectangular boxes. Input boxes are set as cropbox on the resulting document pages (see PDF 32000-1:2008, chapter 7.7.3.3, Table 30). Resulting document will have a number of pages that is the the number of pages of the original document multiplied by the number of rectangular boxes.", "crop -f /tmp/file1.pdf -o /tmp/ --cropAreas [0:0][5:10] [5:0][10:10]"),
    PDF_TO_SINGLE_TIFF("pdftosingletiff", new CliInterfacedTask<PdfToSingleTiffTaskCliArguments, PdfToSingleTiffParameters>() {

        @Override
        protected CommandCliArgumentsTransformer<PdfToSingleTiffTaskCliArguments, PdfToSingleTiffParameters> getArgumentsTransformer() {
            return new PdfToSingleTiffCliArgumentsTransformer();
        }
    }, "Converts a PDF document to a single TIFF image (TIFF format supports multiple images written to a single file).", "pdftosingletiff -f /tmp/file1.pdf -o /tmp/output.tiff --compressionType ccitt_group_3_2d --colorType gray_scale"),
    PDF_TO_MULTIPLE_TIFF("pdftomultipletiff", new CliInterfacedTask<PdfToMultipleTiffTaskCliArguments, PdfToMultipleTiffParameters>() {

        @Override
        protected CommandCliArgumentsTransformer<PdfToMultipleTiffTaskCliArguments, PdfToMultipleTiffParameters> getArgumentsTransformer() {
            return new PdfToMultipleTiffCliArgumentsTransformer();
        }
    }, "Converts a PDF document to multiple TIFF images (one image per page).", "pdftomultipletiff -f /tmp/file1.pdf -o /tmp --compressionType ccitt_group_3_2d --colorType gray_scale"),
    SET_HEADER_FOOTER("setheaderfooter", new CliInterfacedTask<SetHeaderFooterTaskCliArguments, SetHeaderFooterParameters>() {

        @Override
        protected CommandCliArgumentsTransformer<SetHeaderFooterTaskCliArguments, SetHeaderFooterParameters> getArgumentsTransformer() {
            return new SetHeaderFooterCliArgumentsTransformer();
        }
    }, "Adds a header or a footer to a PDF document or part of it.", "setheaderfooter -f /tmp/file1.pdf /tmp/file2.pdf -o /tmp -s all -y top -x center -t Courier -l \"Page [PAGE_OF_TOTAL]\""),
    PDF_TO_JPEG("pdftojpeg", new CliInterfacedTask<PdfToJpegTaskCliArguments, PdfToJpegParameters>() {

        @Override
        protected CommandCliArgumentsTransformer<PdfToJpegTaskCliArguments, PdfToJpegParameters> getArgumentsTransformer() {
            return new PdfToJpegCliArgumentsTransformer();
        }
    }, "Converts a PDF document to multiple JPEG images (one image per page).", "pdftojpeg -f /tmp/file1.pdf -o /tmp"),
    COMBINE_REORDER("combinereorder", new CliInterfacedTask<CombineReorderTaskCliArguments, CombineReorderParameters>() {

        @Override
        protected CommandCliArgumentsTransformer<CombineReorderTaskCliArguments, CombineReorderParameters> getArgumentsTransformer() {
            return new CombineReorderCliArgumentsTransformer();
        }
    }, "Combines multiple PDF documents reordering the pages if required.", "combinereorder -f /tmp/file1.pdf /tmp/file2.pdf -n 0:1 1:2 0:3 -o /tmp/output.pdf"),
    SPLIT_DOWN_THE_MIDDLE("splitdownthemiddle", new CliInterfacedTask<SplitDownTheMiddleTaskCliArguments, SplitDownTheMiddleParameters>() {

        @Override
        protected CommandCliArgumentsTransformer<SplitDownTheMiddleTaskCliArguments, SplitDownTheMiddleParameters> getArgumentsTransformer() {
            return new SplitDownTheMiddleCliArgumentsTransformer();
        }
    }, "Splits document pages in two, reordering pages if necessary.", "splitdownthemiddle -f /tmp/file1.pdf /tmp/file2.pdf -o /tmp"),
    SPLIT_BY_TEXT("splitbytext", new CliInterfacedTask<SplitByTextTaskCliArguments, SplitByTextContentParameters>() {

        @Override
        protected CommandCliArgumentsTransformer<SplitByTextTaskCliArguments, SplitByTextContentParameters> getArgumentsTransformer() {
            return new SplitByTextCliArgumentsTransformer();
        }
    }, "Splits a PDF document by text content, extracting separate documents when specific text changes from page to page.", "splitbytext -f /tmp/file1.pdf --top 114 --left 70 --width 41 --height 15 -o /tmp"),
    COMPRESS("compress", new CliInterfacedTask<CompressTaskCliArguments, OptimizeParameters>() {

        @Override
        protected CommandCliArgumentsTransformer<CompressTaskCliArguments, OptimizeParameters> getArgumentsTransformer() {
            return new CompressCliArgumentsTransformer();
        }
    }, "Compress PDF by optimizing images inside, reducing their dpi, size and/or quality.", "compress -f /tmp/file1.pdf --imageDpi 72 --imageQuality 0.8 -o /tmp"),
    ADD_BACK_PAGES("addbackpages", new CliInterfacedTask<AddBackPagesTaskCliArguments, AddBackPagesParameters>() {

        @Override
        protected CommandCliArgumentsTransformer<AddBackPagesTaskCliArguments, AddBackPagesParameters> getArgumentsTransformer() {
            return new AddBackPagesCliArgumentsTransformer();
        }
    }, "Takes one or more pages from a PDF document and adds them to one or more PDF documents after each 'n' pages.", "addbackpages -f /tmp/file1.pdf /tmp/file2.pdf -b /tmp/back.pdf -s 1 -n 2 -o /tmp"),
    PORTFOLIO("portfolio", new CliInterfacedTask<AttachmentsCollectionTaskCliArguments, AttachmentsCollectionParameters>() {

        @Override
        protected CommandCliArgumentsTransformer<AttachmentsCollectionTaskCliArguments, AttachmentsCollectionParameters> getArgumentsTransformer() {
            return new AttachmentsCollectionCliArgumentsTransformer();
        }
    }, "Creates a portfolio/collection of attachments.", "portfolio -f /tmp/file1.txt /tmp/file2.pdf -i details -o /tmp/portfolio.pdf"),
    NUP("nup", new CliInterfacedTask<NupTaskCliArguments, NupParameters>() {

        @Override
        protected CommandCliArgumentsTransformer<NupTaskCliArguments, NupParameters> getArgumentsTransformer() {
            return new NupCliArgumentsTransformer();
        }
    }, "Composes multiple PDF pages (4, 8, 16, 32) per sheet.", "nup -n 4 -f /tmp/file1.pdf -o /tmp"),
    WATERMARK("watermark", new CliInterfacedTask<WatermarkTaskCliArguments, WatermarkParameters>() {

        @Override
        protected CommandCliArgumentsTransformer<WatermarkTaskCliArguments, WatermarkParameters> getArgumentsTransformer() {
            return new WatermarkCliArgumentsTransformer();
        }
    }, "Stamps a watermark image on multiple PDF documents.", "watermark -f /tmp/file1.pdf -o /tmp -w /tmp/logo.png -l behind -d 300x100 -c 20,50 -a 30");

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
        SortedMap<String, CliCommand> map = new TreeMap<>();
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

    public boolean hasMultiplePdfSource() {
        return isInheritingTraitsFrom(MultipleOptionalPdfSourceTaskCliArguments.class)
                || isInheritingTraitsFrom(MultiplePdfSourceTaskCliArguments.class);
    }

    public boolean hasMultipleSource() {
        return isInheritingTraitsFrom(MultipleSourceTaskCliArguments.class);
    }

    public boolean hasSinglePdfSource() {
        return isInheritingTraitsFrom(SinglePdfSourceTaskCliArguments.class);
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
 * @param
 *            <P>
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
        } catch (com.lexicalscope.jewel.cli.ArgumentValidationException e) {
            throw new org.sejda.cli.exception.ArgumentValidationException(e.getMessage(), e);
        }
    }
}
