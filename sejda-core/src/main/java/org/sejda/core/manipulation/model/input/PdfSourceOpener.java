package org.sejda.core.manipulation.model.input;

import org.sejda.core.exception.TaskIOException;

/**
 * Double Dispatch interface to open a {@link PdfSource}.
 * 
 * @author Andrea Vacondio
 * @param <T>
 *            the type returned by the open action.
 * @see <a href="http://java-x.blogspot.com/2006/05/double-dispatch-in-java.html">double dispatch</a>
 */
public interface PdfSourceOpener<T> {

    /**
     * Opens the input {@link PdfURLSource}.
     * 
     * @param source
     * @return generic result of the open action.
     * @throws TaskIOException
     *             if an error occurs opening the source
     */
    T open(PdfURLSource source) throws TaskIOException;

    /**
     * Opens the input {@link PdfFileSource}.
     * 
     * @param source
     * @return generic result of the open action.
     * @throws TaskIOException
     *             if an error occurs opening the source
     */

    T open(PdfFileSource source) throws TaskIOException;

    /**
     * Opens the input {@link PdfStreamSource}.
     * 
     * @param source
     * @return generic result of the open action.
     * @throws TaskIOException
     *             if an error occurs opening the source
     */

    T open(PdfStreamSource source) throws TaskIOException;
}
