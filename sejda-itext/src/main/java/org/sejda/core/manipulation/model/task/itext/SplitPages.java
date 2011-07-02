package org.sejda.core.manipulation.model.task.itext;

import java.util.HashSet;
import java.util.Set;

import org.sejda.core.exception.TaskExecutionException;
import org.sejda.core.manipulation.model.task.itext.PdfSplitter.NextOutputStrategy;

/**
 * Strategy that holds the page numbers where the split process has to split.
 * 
 * @author Andrea Vacondio
 * 
 */
class SplitPages implements NextOutputStrategy {

    private Set<Integer> closingPages = new HashSet<Integer>();
    private Set<Integer> openingPages = new HashSet<Integer>();

    SplitPages(Integer totalPages) {
        closingPages.add(totalPages);
        openingPages.add(1);
    }

    /**
     * Adds a page to split at.
     * 
     * @param page
     */
    void add(Integer page) {
        closingPages.add(page);
        if (page > 1) {
            openingPages.add(page - 1);
        }
    }

    public void ensureIsValid() throws TaskExecutionException {
        if (closingPages.size() <= 1) {
            throw new TaskExecutionException("Unable to split, no page number given.");
        }
    }

    /**
     * @param page
     * @return true if the given page is an opening page (a page where the split process should start a new document).
     */
    public boolean isOpening(Integer page) {
        return openingPages.contains(page);
    }

    /**
     * @param page
     * @return true if the given page is an closing page (a page where the split process should close the document).
     */
    public boolean isClosing(Integer page) {
        return closingPages.contains(page);
    }
}
