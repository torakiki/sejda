package org.sejda.core.manipulation.model.task.itext;

import static org.sejda.core.manipulation.model.task.itext.component.PdfStamperHandler.nullSafeClosePdfStamperHandler;
import static org.sejda.core.manipulation.model.task.itext.util.ITextUtils.nullSafeClosePdfReader;
import static org.sejda.core.manipulation.model.task.itext.util.ViewerPreferencesUtils.getPageMode;
import static org.sejda.core.notification.dsl.ApplicationEventsNotifier.notifyEvent;
import static org.sejda.core.support.io.model.FileOutput.file;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.sejda.core.exception.TaskException;
import org.sejda.core.manipulation.model.input.PdfSource;
import org.sejda.core.manipulation.model.input.PdfSourceOpener;
import org.sejda.core.manipulation.model.parameter.SetPagesTransitionParameters;
import org.sejda.core.manipulation.model.pdf.transition.PdfPageTransition;
import org.sejda.core.manipulation.model.pdf.viewerpreferences.PdfPageMode;
import org.sejda.core.manipulation.model.task.Task;
import org.sejda.core.manipulation.model.task.itext.component.PdfReaderPartialLoader;
import org.sejda.core.manipulation.model.task.itext.component.PdfStamperHandler;
import org.sejda.core.support.io.SingleOutputWriterSupport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lowagie.text.pdf.PdfReader;

/**
 * Task that applies pages transitions to an input document.
 * 
 * @author Andrea Vacondio
 * 
 */
public class SetPagesTransitionTask implements Task<SetPagesTransitionParameters> {

    private static final Logger LOG = LoggerFactory.getLogger(SetPagesTransitionTask.class);

    private PdfReader reader = null;
    private PdfStamperHandler stamperHandler = null;
    private SingleOutputWriterSupport outputWriter;
    private PdfSourceOpener<PdfReader> sourceOpener;

    public void before(SetPagesTransitionParameters parameters) {
        outputWriter = new SingleOutputWriterSupport();
        sourceOpener = new PdfReaderPartialLoader();
    }

    public void execute(SetPagesTransitionParameters parameters) throws TaskException {
        PdfSource source = parameters.getSource();
        LOG.debug("Opening {} ...", source);
        reader = source.open(sourceOpener);

        File tmpFile = outputWriter.createTemporaryPdfBuffer();
        LOG.debug("Created output on temporary buffer {} ...", tmpFile);
        stamperHandler = new PdfStamperHandler(reader, tmpFile, parameters.getVersion());

        stamperHandler.setCompressionOnStamper(parameters.isCompressXref());
        stamperHandler.setCreatorOnStamper(reader);
        if (parameters.isFullScreen()) {
            LOG.debug("Setting fullscreen mode...");
            stamperHandler.setViewerPreferencesOnStamper(getPageMode(PdfPageMode.FULLSCREEN));
        }

        Map<Integer, PdfPageTransition> transitions = getTransitionsMap(parameters, reader.getNumberOfPages());
        LOG.debug("Applying {} transitions...", transitions.size());
        int currentStep = 0;
        for (Entry<Integer, PdfPageTransition> entry : transitions.entrySet()) {
            stamperHandler.setTransitionOnStamper(entry.getKey(), entry.getValue());
            notifyEvent().stepsCompleted(++currentStep).outOf(transitions.size());
        }

        nullSafeClosePdfReader(reader);
        nullSafeClosePdfStamperHandler(stamperHandler);

        outputWriter.flushSingleOutput(file(tmpFile).name(source.getName()), parameters.getOutput(),
                parameters.isOverwrite());

        LOG.debug("Transitions set on {}", parameters.getOutput());
    }

    public void after() {
        nullSafeClosePdfReader(reader);
        nullSafeClosePdfStamperHandler(stamperHandler);
    }

    /**
     * @param parameters
     * @param totalPages
     * @return a map containing all the transitions to apply considering the default transition if there is one.
     */
    private Map<Integer, PdfPageTransition> getTransitionsMap(SetPagesTransitionParameters parameters, int totalPages) {
        Map<Integer, PdfPageTransition> map = new HashMap<Integer, PdfPageTransition>();
        if (parameters.getDefaultTransition() != null) {
            for (int i = 1; i <= totalPages; i++) {
                map.put(i, parameters.getDefaultTransition());
            }
        }
        map.putAll(parameters.getTransitions());
        return map;
    }
}
