package org.sejda.core.manipulation.model.task.itext;

import static org.sejda.core.manipulation.model.task.itext.util.ITextUtils.nullSafeClosePdfCopy;
import static org.sejda.core.notification.dsl.ApplicationEventsNotifier.notifyEvent;
import static org.sejda.core.support.io.model.FileOutput.file;
import static org.sejda.core.support.perfix.NameGenerator.nameGenerator;
import static org.sejda.core.support.perfix.model.NameGenerationRequest.nameRequest;

import java.io.File;
import java.util.Collection;
import java.util.Map;

import org.sejda.core.exception.TaskException;
import org.sejda.core.exception.TaskExecutionException;
import org.sejda.core.manipulation.model.parameter.SinglePdfSourceParameters;
import org.sejda.core.manipulation.model.task.itext.component.DefaultPdfCopier;
import org.sejda.core.support.io.MultipleOutputWriterSupport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lowagie.text.pdf.PdfReader;

//TODO check javadoc once the class is done
/**
 * Component responsible for the split process on an open {@link PdfReader}.
 * 
 * <pre>
 * {@code
 * new PdfSplitter(reader).usingParams(params).usingPrefix(prefix).split(pages);
 * }
 * </pre>
 * 
 * @author Andrea Vacondio
 * 
 */
public class PdfSplitter {

    private static final Logger LOG = LoggerFactory.getLogger(PdfSplitter.class);

    private PdfReader reader;
    private String outputPrefix;
    private SinglePdfSourceParameters parameters;
    private Map<Integer, String> bookmarksMap;
    private NextOutputStrategy splitPages;
    private int totalPages;
    private MultipleOutputWriterSupport outputWriter = new MultipleOutputWriterSupport();

    /**
     * Creates a new splitter using the given reader.
     * 
     * @param reader
     */
    public PdfSplitter(PdfReader reader) {
        this.reader = reader;
        this.totalPages = reader.getNumberOfPages();
        this.splitPages = new SplitPages(totalPages);
    }

    public void split(Collection<Integer> pages) throws TaskException {
        setPages(pages);
        DefaultPdfCopier copyHandler = null;
        // TODO try finally to close the handler
        for (int page = 1; page <= totalPages; page++) {
            if (splitPages.isOpening(page)) {
                LOG.debug("Starting split at page {} of the original document...", page);
                copyHandler = open(page);
            }
            copyHandler.addPage(reader, page);
            notifyEvent().stepsCompleted(page).outOf(totalPages);
            if (splitPages.isClosing(page)) {
                close(copyHandler);
                LOG.debug("Ending split at page {} of the original document...", page);
            }
        }
        outputWriter.flushOutputs(parameters.getOutput(), parameters.isOverwrite());
    }

    private void close(DefaultPdfCopier copyHandler) {
        LOG.debug("Adding bookmarks to the temporary buffer ...");
        // TODO take care of bookmarks
        nullSafeClosePdfCopy(copyHandler);
    }

    private DefaultPdfCopier open(Integer page) throws TaskException {
        File tmpFile = outputWriter.createTemporaryPdfBuffer();
        LOG.debug("Created output on temporary buffer {} ...", tmpFile);

        DefaultPdfCopier copyHandler = new DefaultPdfCopier(reader, tmpFile, parameters.getVersion());
        copyHandler.setCompression(parameters.isCompressXref());

        // TODO name request using file number, bookmarks etc
        bookmarksMap.get(page);
        String outName = nameGenerator(outputPrefix, parameters.getSource().getName()).generate(
                nameRequest().page(page));
        outputWriter.addOutput(file(tmpFile).name(outName));

        return copyHandler;
    }

    /**
     * Stores the pages in a sorted set removing those pages that are not in the input document.
     * 
     * @param pages
     * @throws TaskExecutionException
     *             if the resulting collection of pages is empty.
     */
    private void setPages(Collection<Integer> pages) throws TaskExecutionException {
        for (Integer page : pages) {
            if (page > 0 && page <= totalPages) {
                splitPages.add(page);
            } else {
                LOG.warn("Cannot split at page {}. Page not found in the input document.");
            }
        }
        splitPages.ensureIsValid();
    }

    /**
     * Sets the outputPrefix to use during the split process.
     * 
     * @param outputPrefix
     * @return the splitter instance with the outputPrefix set.
     */
    public PdfSplitter usingPrefix(String outputPrefix) {
        this.outputPrefix = outputPrefix;
        return this;
    }

    /**
     * Sets the parameters to use during the split process. Parameters are mandatory to be able to perform the split.
     * 
     * @param parameters
     * @return the splitter instance with the parameters set.
     */
    public PdfSplitter usingParams(SinglePdfSourceParameters parameters) {
        this.parameters = parameters;
        return this;
    }

    /**
     * Sets the bookmarksMap to use during the split process. Not mandatory, it's used during the output file name generation in case the the bookmark name is needed.
     * 
     * @param bookmarksMap
     * @return the splitter instance with the bookmarksMap set.
     */
    public PdfSplitter usingBookmarks(Map<Integer, String> bookmarksMap) {
        this.bookmarksMap = bookmarksMap;
        return this;
    }

    /**
     * Strategy used by the {@link PdfSplitter} to know when it's time to close the ongoing output and open a new one.
     * 
     * @author Andrea Vacondio
     * 
     */
    interface NextOutputStrategy {

        /**
         * Ensures that the strategy implementation is in a valid state.
         * 
         * @throws TaskExecutionException
         *             if not in a valid state.
         */
        void ensureIsValid() throws TaskExecutionException;

        /**
         * @param page
         *            the current processing page
         * @return true if the splitter should open a new output, false otherwise.
         */
        boolean isOpening(Integer page);

        /**
         * @param page
         *            the current processing page
         * @return true if the splitter should close the current output, false otherwise.
         */
        boolean isClosing(Integer page);
    }
}
