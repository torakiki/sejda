/*
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

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.sejda.cli.model.CombineReorderTaskCliArguments;
import org.sejda.conversion.MultiplePdfMergeInputAdapter;
import org.sejda.model.exception.SejdaRuntimeException;
import org.sejda.model.input.FileIndexAndPage;
import org.sejda.model.input.PdfFileSource;
import org.sejda.model.input.PdfMergeInput;
import org.sejda.model.parameter.CombineReorderParameters;
import org.sejda.model.rotation.Rotation;

public class CombineReorderCliArgumentsTransformer extends BaseCliArgumentsTransformer implements
        CommandCliArgumentsTransformer<CombineReorderTaskCliArguments, CombineReorderParameters> {

    @Override
    public CombineReorderParameters toTaskParameters(CombineReorderTaskCliArguments taskCliArguments) {
        CombineReorderParameters parameters = new CombineReorderParameters();
        populateAbstractParameters(parameters, taskCliArguments);
        populateOutputTaskParameters(parameters, taskCliArguments);

        MultiplePdfMergeInputAdapter mergeInputsAdapter = extractPdfMergeInputs(taskCliArguments);

        for (PdfMergeInput eachMergeInput : mergeInputsAdapter.getPdfMergeInputs()) {
            parameters.addInput(eachMergeInput);
        }

        taskCliArguments.getPages().stream()
                .map(in -> parseIndexAndPage(in))
                .filter(item -> item.isPresent())
                .map(item -> item.get())
                .forEach(item -> parameters.addPage(item.getFileIndex(), item.getPage(), item.getRotation()));

        return parameters;
    }

    private MultiplePdfMergeInputAdapter extractPdfMergeInputs(CombineReorderTaskCliArguments taskCliArguments) {
        List<PdfFileSource> inputFiles = taskCliArguments.getFiles().stream().flatMap(a -> a.getPdfFileSources().stream())
                .collect(Collectors.toList());

        if (inputFiles.isEmpty()) {
            throw new SejdaRuntimeException("No input files specified");
        }

        MultiplePdfMergeInputAdapter mergeInputsAdapter = new MultiplePdfMergeInputAdapter(inputFiles, Collections.emptyList());
        return mergeInputsAdapter;
    }

    private Optional<FileIndexAndPage> parseIndexAndPage(String in) {
        String[] parts = in.split(":");
        try {
            Rotation rotation = parts.length > 2 ? Rotation.getRotation(Integer.valueOf(parts[2])) : Rotation.DEGREES_0;
            return Optional.of(new FileIndexAndPage(Integer.valueOf(parts[0]), Integer.valueOf(parts[1]), rotation));
        } catch (RuntimeException e) {
            return Optional.empty();
        }
    }
}
