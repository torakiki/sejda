package org.sejda.core.manipulation.model.task;

import java.util.Collection;

import org.sejda.core.exception.TaskException;

/**
 * Statefull component providing a subset of the document outline.
 * 
 * @author Andrea Vacondio
 * @param <T>
 *            generic type for the outline returned by the provider.
 */
public interface OutlineSubsetProvider<T> {

    /**
     * Sets the start page from which the component will provide outline.
     * 
     * @param startPage
     */
    void startPage(int startPage);

    /**
     * 
     * @param endPage
     *            end page till which the component will provide bookmarks.
     * @return collection of item representing the document outline from start page to the provided end page.
     * @throws TaskException
     *             if the start page is not set or the end page is before the start.
     */
    Collection<T> getOutlineUntillPage(int endPage) throws TaskException;

    /**
     * 
     * @param endPage
     *            end page till which the component will provide bookmarks.
     * @return collection of item representing the document outline from start page to the provided end page with the offset applied.
     * @throws TaskException
     *             if the start page is not set or the end page is before the start.
     */
    Collection<T> getOutlineUntillPageWithOffset(int endPage, int offset) throws TaskException;

}
