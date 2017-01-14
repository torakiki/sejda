/*
 * Created on 14 gen 2017
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

import org.sejda.cli.model.CompressTaskCliArguments;
import org.sejda.cli.model.CropTaskCliArguments;
import org.sejda.cli.model.ExtractTextByPagesTaskCliArguments;
import org.sejda.cli.model.ExtractTextTaskCliArguments;
import org.sejda.cli.model.NupTaskCliArguments;
import org.sejda.cli.model.SplitByTextTaskCliArguments;
import org.sejda.cli.model.SplitDownTheMiddleTaskCliArguments;
import org.sejda.cli.model.TaskCliArguments;
import org.sejda.cli.transformer.CommandCliArgumentsTransformer;
import org.sejda.cli.transformer.CompressCliArgumentsTransformer;
import org.sejda.cli.transformer.CropCliArgumentsTransformer;
import org.sejda.cli.transformer.ExtractTextByPagesCliArgumentsTransformer;
import org.sejda.cli.transformer.ExtractTextCliArgumentsTransformer;
import org.sejda.cli.transformer.NupCliArgumentsTransformer;
import org.sejda.cli.transformer.SplitByTextCliArgumentsTransformer;
import org.sejda.cli.transformer.SplitDownTheMiddleCliArgumentsTransformer;
import org.sejda.model.parameter.CropParameters;
import org.sejda.model.parameter.ExtractTextByPagesParameters;
import org.sejda.model.parameter.ExtractTextParameters;
import org.sejda.model.parameter.NupParameters;
import org.sejda.model.parameter.OptimizeParameters;
import org.sejda.model.parameter.SplitByTextContentParameters;
import org.sejda.model.parameter.SplitDownTheMiddleParameters;
import org.sejda.model.parameter.base.TaskParameters;

/**
 * @author Andrea Vacondio
 *
 */
public enum ProCliCommand implements CliCommand {
    COMPRESS("compress", new CliInterfacedTask<CompressTaskCliArguments, OptimizeParameters>() {

        @Override
        protected CommandCliArgumentsTransformer<CompressTaskCliArguments, OptimizeParameters> getArgumentsTransformer() {
            return new CompressCliArgumentsTransformer();
        }
    }, "Compress PDF by optimizing images inside, reducing their dpi, size and/or quality.", "compress -f /tmp/file1.pdf --imageDpi 72 --imageQuality 0.8 -o /tmp"),
    CROP("crop", new CliInterfacedTask<CropTaskCliArguments, CropParameters>() {

        @Override
        protected CommandCliArgumentsTransformer<CropTaskCliArguments, CropParameters> getArgumentsTransformer() {
            return new CropCliArgumentsTransformer();
        }
    }, "Given a PDF document and a set of rectangular boxes, creates an output PDF document where pages are cropped according to the input rectangular boxes. Input boxes are set as cropbox on the resulting document pages (see PDF 32000-1:2008, chapter 7.7.3.3, Table 30). Resulting document will have a number of pages that is the the number of pages of the original document multiplied by the number of rectangular boxes.", "crop -f /tmp/file1.pdf -o /tmp/ --cropAreas [0:0][5:10] [5:0][10:10]"),
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
    NUP("nup", new CliInterfacedTask<NupTaskCliArguments, NupParameters>() {

        @Override
        protected CommandCliArgumentsTransformer<NupTaskCliArguments, NupParameters> getArgumentsTransformer() {
            return new NupCliArgumentsTransformer();
        }
    }, "Composes multiple PDF pages (4, 8, 16, 32) per sheet.", "nup -n 4 -f /tmp/file1.pdf -o /tmp"),
    SPLIT_BY_TEXT("splitbytext", new CliInterfacedTask<SplitByTextTaskCliArguments, SplitByTextContentParameters>() {

        @Override
        protected CommandCliArgumentsTransformer<SplitByTextTaskCliArguments, SplitByTextContentParameters> getArgumentsTransformer() {
            return new SplitByTextCliArgumentsTransformer();
        }
    }, "Splits a PDF document by text content, extracting separate documents when specific text changes from page to page.", "splitbytext -f /tmp/file1.pdf --top 114 --left 70 --width 41 --height 15 -o /tmp"),
    SPLIT_DOWN_THE_MIDDLE("splitdownthemiddle", new CliInterfacedTask<SplitDownTheMiddleTaskCliArguments, SplitDownTheMiddleParameters>() {

        @Override
        protected CommandCliArgumentsTransformer<SplitDownTheMiddleTaskCliArguments, SplitDownTheMiddleParameters> getArgumentsTransformer() {
            return new SplitDownTheMiddleCliArgumentsTransformer();
        }
    }, "Splits document pages in two, reordering pages if necessary.", "splitdownthemiddle -f /tmp/file1.pdf /tmp/file2.pdf -o /tmp");

    private BaseCliCommand command;

    private ProCliCommand(String displayName,
            CliInterfacedTask<? extends TaskCliArguments, ? extends TaskParameters> cliTask, String description,
            String exampleUsage) {
        this.command = new BaseCliCommand(displayName, cliTask, description, exampleUsage);
    }

    @Override
    public String getDisplayName() {
        return command.getDisplayName();
    }

    @Override
    public String getDescription() {
        return command.getDescription();
    }

    @Override
    public String getExampleUsage() {
        return command.getExampleUsage();
    }

    @Override
    public String getHelpMessage() {
        return command.getHelpMessage();
    }

    @Override
    public TaskParameters parseTaskParameters(String[] rawArguments) {
        return command.parseTaskParameters(rawArguments);
    }

    @Override
    public Class<?> getCliArgumentsClass() {
        return command.getCliArgumentsClass();
    }
}
