package org.sejda.core.manipulation.model.task.itext.component.split;

import java.io.File;
import java.util.Collection;

import org.sejda.core.exception.TaskException;
import org.sejda.core.manipulation.model.pdf.PdfVersion;
import org.sejda.core.manipulation.model.task.itext.component.DefaultPdfCopier;
import org.sejda.core.manipulation.model.task.itext.component.PdfCopier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lowagie.text.pdf.PdfReader;

//TODO javadoc

/**
 * Split by page numbers
 * 
 * @author Andrea Vacondio
 * 
 */
public class PagesPdfSplitter extends AbstractPdfSplitter {
    private static final Logger LOG = LoggerFactory.getLogger(PagesPdfSplitter.class);

    private SplitPages splitPages;

    public PagesPdfSplitter(PdfReader reader) {
        super(reader);
        this.splitPages = new SplitPages(super.getTotalNumberOfPages());
    }

    @Override
    PdfCopier openCopier(PdfReader reader, File outputFile, PdfVersion version) throws TaskException {
        return new DefaultPdfCopier(reader, outputFile, version);
    }

    @Override
    NextOutputStrategy nextOutputStrategy() {
        return splitPages;
    }

    /**
     * @throws TaskException
     *             if the resulting collection of pages is empty.
     */
    @Override
    public void split() throws TaskException {
        splitPages.ensureIsValid();
        super.split();
    }

    /**
     * Stores the pages in a sorted set removing those pages that are not in the input document.
     * 
     * @param pages
     */
    public PagesPdfSplitter setPages(Collection<Integer> pages) {
        for (Integer page : pages) {
            if (page > 0 && page <= super.getTotalNumberOfPages()) {
                splitPages.add(page);
            } else {
                LOG.warn("Cannot split at page {}. Page not found in the input document.");
            }
        }
        return this;
    }
}
