package org.sejda.cli.adapters;

import java.io.File;

import org.apache.commons.lang.StringUtils;
import org.sejda.core.exception.SejdaRuntimeException;
import org.sejda.core.manipulation.model.input.PdfFileSource;

/**
 * @author Eduard Weissmann
 * 
 */
public class PdfFileSourceAdapter {
    private static final String PASSWORD_SEPARATOR = ";";

    private final PdfFileSource pdfFileSource;

    public PdfFileSourceAdapter(String filePathAndPassword) {

        File file = new File(extractFilePath(filePathAndPassword));
        String password = extractPassword(filePathAndPassword);

        if (!file.exists()) {
            throw new SejdaRuntimeException("File '" + file.getPath() + "' does not exist");
        }

        this.pdfFileSource = StringUtils.isBlank(password) ? PdfFileSource.newInstanceNoPassword(file) : PdfFileSource
                .newInstanceWithPassword(file, password);
    }

    /**
     * @return the pdfFileSource
     */
    public PdfFileSource getPdfFileSource() {
        return pdfFileSource;
    }

    static String extractFilePath(String filePathAndPassword) {
        if (!filePathAndPassword.contains(PASSWORD_SEPARATOR)) {
            return filePathAndPassword;
        }
        return filePathAndPassword.substring(0, filePathAndPassword.indexOf(PASSWORD_SEPARATOR));
    }

    static String extractPassword(String filePathAndPassword) {
        if (!filePathAndPassword.contains(PASSWORD_SEPARATOR)) {
            return "";
        }
        return filePathAndPassword.substring(filePathAndPassword.indexOf(PASSWORD_SEPARATOR) + 1);
    }
}
