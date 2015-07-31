package org.sejda.core.service;

import org.sejda.core.Sejda;
import org.sejda.model.exception.TaskException;

import java.io.IOException;

public abstract class WithUnethicalReadProperty {
    public WithUnethicalReadProperty(Boolean value) throws TaskException, IOException {
        try {
            System.setProperty(Sejda.UNETHICAL_READ_PROPERTY_NAME, String.valueOf(value));
            execute();
        } finally {
            System.setProperty(Sejda.UNETHICAL_READ_PROPERTY_NAME, "false");
        }
    }

    abstract public void execute() throws TaskException, IOException;
}
