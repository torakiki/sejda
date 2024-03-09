/*
 * Created on 14/set/2011
 * Copyright 2011 by Andrea Vacondio (andrea.vacondio@gmail.com).
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
package org.sejda.model.parameter.base;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.sejda.model.util.IOUtils;
import org.sejda.model.output.SingleOrMultipleTaskOutput;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import static java.util.Optional.ofNullable;
import static org.apache.commons.lang3.StringUtils.defaultString;

/**
 * Provides a skeletal implementation for parameter classes having multiple pdf source as input and generating multiple output files. The output can be a file for scenarios where a
 * the task produces one output file (eg: rotate on a single input)
 * 
 * @author Andrea Vacondio
 * 
 */
public class MultiplePdfSourceMultipleOutputParameters extends MultiplePdfSourceParameters
        implements SingleOrMultipleOutputTaskParameters {

    private String outputPrefix = "";
    @Valid
    @NotNull
    private SingleOrMultipleTaskOutput output;

    private final List<String> specificResultFilenames = new ArrayList<>();

    @Override
    public String getOutputPrefix() {
        return outputPrefix;
    }

    @Override
    public void setOutputPrefix(String outputPrefix) {
        this.outputPrefix = outputPrefix;
    }

    @Override
    public SingleOrMultipleTaskOutput getOutput() {
        return output;
    }

    @Override
    public void setOutput(SingleOrMultipleTaskOutput output) {
        this.output = output;
    }

    public void addSpecificResultFilename(String filename) {
        this.specificResultFilenames.add(filename);
    }

    /**
     * Adds the given filenames to the list of the explicit filenames
     * 
     * @param filenames
     * @see ArrayList#addAll(Collection)
     */
    public void addSpecificResultFilenames(Collection<String> filenames) {
        this.specificResultFilenames.addAll(filenames);
    }

    /**
     * @return the {@link List} of names explicitly set for the output files. The first file generated by the task will be named using the first element of this list, the second
     *         file using the second name and so on. If no valid name is found we fallback to the default names generation logic.
     */
    public List<String> getSpecificResultFilenames() {
        return Collections.unmodifiableList(specificResultFilenames);
    }

    /**
     * 
     * @param fileNumber
     * @return the explicit filename set for the given PDF output fileNumber or null if no valid filename is found. The resulting filename is sanitized to contain only valid
     *         characters and to have a PDF extension.
     */
    public String getSpecificResultFilename(int fileNumber) {
        return getSpecificResultFilename(fileNumber, ".pdf");
    }

    /**
     * 
     * @param fileNumber
     * @param expectedExtension
     *            the expect extension (.pdf, .json, .txt).
     * @return the explicit filename set for the given output fileNumber or null if no valid filename is found. The resulting filename is sanitized to contain only valid characters
     *         and to have the expected file extension.
     */
    public String getSpecificResultFilename(int fileNumber, String expectedExtension) {
        if (specificResultFilenames.size() >= fileNumber) {
            return ofNullable(specificResultFilenames.get(fileNumber - 1))
                    .filter(StringUtils::isNotBlank)
                    .map(IOUtils::toSafeFilename)
                    .map(n -> {
                        String ext = defaultString(expectedExtension).toLowerCase();
                        if (!n.toLowerCase().endsWith(ext)) {
                            return n + ext;
                        }
                        return n;
                    })
                    .map(IOUtils::shortenFilename)
                    .orElse(null);
        }
        return null;
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder().appendSuper(super.hashCode()).append(outputPrefix).append(output)
                .append(specificResultFilenames).toHashCode();
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof MultiplePdfSourceMultipleOutputParameters parameter)) {
            return false;
        }
        return new EqualsBuilder().appendSuper(super.equals(other)).append(outputPrefix, parameter.outputPrefix)
                .append(output, parameter.output).append(specificResultFilenames, parameter.specificResultFilenames)
                .isEquals();
    }

}
