package org.sejda.cli.model.adapter;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.sejda.cli.exception.ArgumentValidationException;
import org.sejda.core.manipulation.model.input.PdfFileSource;

/**
 * 
 * Adapter for {@link PdfFileSourceAdapter}. Main role is to be a string-based constructor for the underlying model object
 * 
 * @author Eduard Weissmann
 * 
 */
public class PdfFileSourceAdapter {
    public static final String PASSWORD_SEPARATOR = ":";

    private final PdfFileSource pdfFileSource;

    /**
     * Constructor for a {@link PdfFileSourceAdapter}. Supports pdf files that are password protected<br/>
     * If file has no password protection, input parameter is the path to the file. Eg: {@code /path/to/file.pdf}<br/>
     * For password protected files, input is concatenation of file path and password, using {@value #PASSWORD_SEPARATOR} as delimiter. Eg: {@code /path/to/file.pdf:secret123}
     * 
     * @param filePathAndPassword
     *            file path concatenated using {@value #PASSWORD_SEPARATOR} with (optional) password
     */
    public PdfFileSourceAdapter(String filePathAndPassword) {

        File file = new File(extractFilePath(filePathAndPassword));
        String password = extractPassword(filePathAndPassword);

        if (!file.exists()) {
            throw new ArgumentValidationException("File '" + file.getPath() + "' does not exist");
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

    /**
     * Extracts the file path part from the specified input
     * 
     * @param filePathAndPassword
     *            input containing file path concatenated using {@value #PASSWORD_SEPARATOR} with (optional) password
     * @return file path part
     */
    static String extractFilePath(String filePathAndPassword) {
        if (!filePathAndPassword.contains(PASSWORD_SEPARATOR)) {
            return filePathAndPassword;
        }
        return filePathAndPassword.substring(0, filePathAndPassword.indexOf(PASSWORD_SEPARATOR));
    }

    /**
     * Extracts the password part from the specified input
     * 
     * @param filePathAndPassword
     *            input containing file path concatenated using {@value #PASSWORD_SEPARATOR} with (optional) password
     * @return the password part
     */
    static String extractPassword(String filePathAndPassword) {
        if (!filePathAndPassword.contains(PASSWORD_SEPARATOR)) {
            return "";
        }
        return filePathAndPassword.substring(filePathAndPassword.indexOf(PASSWORD_SEPARATOR) + 1);
    }

    /**
     * Factory method for transforming a collection of filenames to {@link PdfFileSource}s<br/>
     * Password protected file support included
     * 
     * @param filenames
     * @return a list of {@link PdfFileSource}s built from the filenames specified
     */
    static List<PdfFileSource> fromStrings(List<String> filenames) {
        List<PdfFileSource> result = new ArrayList<PdfFileSource>();
        for (String filename : filenames) {
            result.add(new PdfFileSourceAdapter(StringUtils.trim(filename)).getPdfFileSource());
        }

        return result;
    }
}
