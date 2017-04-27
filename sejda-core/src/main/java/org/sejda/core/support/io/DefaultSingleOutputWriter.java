/*
 * Created on 29/ago/2010
 *
 * Copyright 2010 by Andrea Vacondio (andrea.vacondio@gmail.com).
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
package org.sejda.core.support.io;

import static java.util.Objects.isNull;
import static java.util.Optional.of;
import static org.apache.commons.io.FilenameUtils.getExtension;
import static org.sejda.core.support.io.OutputWriterHelper.moveFile;
import static org.sejda.model.output.ExistingOutputPolicy.FAIL;
import static org.sejda.model.output.ExistingOutputPolicy.SKIP;

import java.io.File;
import java.io.IOException;

import org.sejda.model.exception.TaskIOException;
import org.sejda.model.output.DirectoryTaskOutput;
import org.sejda.model.output.ExistingOutputPolicy;
import org.sejda.model.output.FileOrDirectoryTaskOutput;
import org.sejda.model.output.FileTaskOutput;
import org.sejda.model.task.TaskExecutionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Single writer default implementation.
 * 
 * @author Andrea Vacondio
 * 
 */
class DefaultSingleOutputWriter implements SingleOutputWriter {

    private static final Logger LOG = LoggerFactory.getLogger(DefaultSingleOutputWriter.class);

    private File temporaryOutput;
    private final ExistingOutputPolicy existingOutputPolicy;
    private final TaskExecutionContext executionContext;

    DefaultSingleOutputWriter(ExistingOutputPolicy existingOutputPolicy, TaskExecutionContext executionContext) {
        this.existingOutputPolicy = of(existingOutputPolicy).filter(p -> p != SKIP).orElseGet(() -> {
            LOG.debug("Cannot use {} output policy for single output, replaced with {}", SKIP, FAIL);
            return FAIL;
        });
        this.executionContext = executionContext;
    }

    @Override
    public File taskOutput(File expectedDesintation) throws TaskIOException {
        if (expectedDesintation.exists()) {
            switch (existingOutputPolicy) {
            case OVERWRITE:
            case RENAME:
                this.temporaryOutput = IOUtils.createTemporaryBuffer("." + getExtension(expectedDesintation.getName()));
                break;
            default:
                throw new TaskIOException(
                        String.format("Unable to write to the already existing file destination %s. (policy is %s)",
                                expectedDesintation, existingOutputPolicy));
            }
        } else {
            this.temporaryOutput = expectedDesintation;
        }
        return temporaryOutput;
    }

    @Override
    public void dispatch(FileTaskOutput output) throws IOException {
        if (isNull(temporaryOutput)) {
            throw new IOException("No task output set");
        }
        if (!this.temporaryOutput.equals(output.getDestination())) {
            moveFile(temporaryOutput, output.getDestination(), existingOutputPolicy, executionContext);
        }
    }

    @Override
    public void dispatch(DirectoryTaskOutput output) throws IOException {
        throw new IOException("Unsupported DirectoryTaskOutput, expected a FileTaskOutput");
    }

    @Override
    public void dispatch(FileOrDirectoryTaskOutput output) throws IOException {
        throw new IOException("Unsupported FileOrDirectoryTaskOutput, expected a FileTaskOutput");
    }
}
