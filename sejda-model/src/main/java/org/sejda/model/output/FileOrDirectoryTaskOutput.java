package org.sejda.model.output;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.sejda.model.exception.TaskOutputVisitException;

import java.io.File;
import java.io.IOException;

public class FileOrDirectoryTaskOutput implements SingleOrMultipleTaskOutput {

    private File file;

    public FileOrDirectoryTaskOutput(File file) {
        this.file = file;
    }

    @Override
    public File getDestination() {
        return file;
    }

    @Override
    public void accept(TaskOutputDispatcher writer) throws TaskOutputVisitException {
        try {
            writer.dispatch(this);
        } catch (IOException e) {
            throw new TaskOutputVisitException("Exception dispatching the file task output.", e);
        }
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this).append(getDestination()).toString();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder().append(getDestination()).toHashCode();
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof FileTaskOutput)) {
            return false;
        }
        FileTaskOutput output = (FileTaskOutput) other;
        return new EqualsBuilder().append(file, output.getDestination()).isEquals();
    }

}
