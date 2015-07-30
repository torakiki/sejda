package org.sejda.impl.sambox;

import org.sejda.sambox.pdmodel.PDDocument;
import org.sejda.impl.sambox.component.*;
import org.sejda.model.exception.TaskException;
import org.sejda.model.input.PdfSource;
import org.sejda.model.input.PdfSourceOpener;
import org.sejda.model.parameter.SplitByTextContentParameters;
import org.sejda.model.pdf.encryption.PdfAccessPermission;
import org.sejda.model.task.BaseTask;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.sejda.common.ComponentsUtility.nullSafeCloseQuietly;

public class SplitByTextContentTask extends BaseTask<SplitByTextContentParameters> {

    private static final Logger LOG = LoggerFactory.getLogger(SplitByTextContentTask.class);

    private PdfSourceOpener<PDDocumentHandler> documentLoader;
    private PDDocumentHandler sourceDocumentHandler;
    private AbstractPdfSplitter splitter;

    public void before(SplitByTextContentParameters parameters) throws TaskException {
        documentLoader = new DefaultPdfSourceOpener();
    }

    public void execute(SplitByTextContentParameters parameters) throws TaskException {
        PdfSource<?> source = parameters.getSource();
        LOG.debug("Opening {}", source);
        sourceDocumentHandler = source.open(documentLoader);
        sourceDocumentHandler.getPermissions().ensurePermission(PdfAccessPermission.ASSEMBLE);
        PDDocument sourceDocument = sourceDocumentHandler.getUnderlyingPDDocument();

        splitter = new ByTextChangesPdfSplitter(sourceDocument, parameters);
        LOG.debug("Starting to split by text content");
        splitter.split(getNotifiableTaskMetadata());

        LOG.debug("Input documents split and written to {}", parameters.getOutput());
    }

    public void after() {
        closeResource();
    }

    private void closeResource() {
        nullSafeCloseQuietly(sourceDocumentHandler);
        splitter = null;
    }
}
