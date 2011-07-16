package org.sejda.core.manipulation.model.task.itext.component;

import java.util.List;

import org.sejda.core.exception.TaskException;
import org.sejda.core.exception.TaskIOException;

import com.lowagie.text.pdf.PdfPageLabels;
import com.lowagie.text.pdf.PdfReader;

/**
 * Prodvides functionalities to copy pages from a given {@link PdfReader}.
 * 
 * @author Andrea Vacondio
 * 
 */
public interface PdfCopier {

    /**
     * Adds the given page extracted from the input reader.
     * 
     * @param reader
     * @param pageNumber
     * @throws TaskException
     */
    void addPage(PdfReader reader, int pageNumber) throws TaskException;

    /**
     * Adds all the pages from the input reader
     * 
     * @param reader
     * @throws TaskException
     */
    void addAllPages(PdfReader reader) throws TaskException;

    /**
     * Enables compression if compress is true.
     * 
     * @param compress
     */
    void setCompression(boolean compress);

    /**
     * sets the input page labels to the underlying pdf copy.
     * 
     * @param labels
     */
    void setPageLabels(PdfPageLabels labels);

    /**
     * Closes the copier.
     */
    void close();

    /**
     * Frees the reader on the underlying pdf copy.
     * 
     * @param reader
     * @throws TaskIOException
     */
    void freeReader(PdfReader reader) throws TaskIOException;

    /**
     * Sets the input list of bookmarks to the copier.
     * 
     * @param bookmarks
     */
    void setBookmarks(List<?> bookmarks);

}
