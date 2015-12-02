/*
 * This file is part of the Sejda source code
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.sejda.impl.sambox;

import com.google.common.hash.Hashing;
import com.google.common.io.Files;
import org.sejda.core.support.io.MultipleOutputWriter;
import org.sejda.core.support.io.OutputWriters;
import org.sejda.core.writer.model.ImageOptimizer;
import org.sejda.impl.sambox.component.DefaultPdfSourceOpener;
import org.sejda.impl.sambox.component.PDDocumentHandler;
import org.sejda.model.exception.TaskException;
import org.sejda.model.input.PdfSource;
import org.sejda.model.input.PdfSourceOpener;
import org.sejda.model.parameter.OptimizeParameters;
import org.sejda.model.task.BaseTask;
import org.sejda.sambox.cos.COSName;
import org.sejda.sambox.filter.MissingImageReaderException;
import org.sejda.sambox.pdmodel.PDPage;
import org.sejda.sambox.pdmodel.PDResources;
import org.sejda.sambox.pdmodel.graphics.PDXObject;
import org.sejda.sambox.pdmodel.graphics.image.PDImageXObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.lang.ref.SoftReference;
import java.util.HashMap;
import java.util.Map;

import static org.sejda.common.ComponentsUtility.nullSafeCloseQuietly;
import static org.sejda.core.notification.dsl.ApplicationEventsNotifier.notifyEvent;
import static org.sejda.core.support.io.IOUtils.createTemporaryPdfBuffer;
import static org.sejda.core.support.io.model.FileOutput.file;
import static org.sejda.core.support.prefix.NameGenerator.nameGenerator;
import static org.sejda.core.support.prefix.model.NameGenerationRequest.nameRequest;

/**
 * SAMbox implementation of the Optimize task
 */
public class OptimizeTask extends BaseTask<OptimizeParameters> {

    private static final Logger LOG = LoggerFactory.getLogger(OptimizeTask.class);

    private int totalSteps;
    private PDDocumentHandler documentHandler = null;
    private MultipleOutputWriter outputWriter;
    private OptimizeParameters parameters;
    private Map<String, SoftReference<PDImageXObject>> cache = new HashMap<>();

    private PdfSourceOpener<PDDocumentHandler> documentLoader;

    @Override
    public void before(OptimizeParameters parameters) {
        this.parameters = parameters;
        totalSteps = parameters.getSourceList().size();
        documentLoader = new DefaultPdfSourceOpener();
        outputWriter = OutputWriters.newMultipleOutputWriter(parameters.isOverwrite());
    }

    @Override
    public void execute(OptimizeParameters parameters) throws TaskException {

        int currentStep = 0;
        for (PdfSource<?> source : parameters.getSourceList()) {
            currentStep++;
            LOG.debug("Opening {}", source);
            documentHandler = source.open(documentLoader);
            documentHandler.setCreatorOnPDDocument();

            File tmpFile = createTemporaryPdfBuffer();
            LOG.debug("Created output on temporary buffer {}", tmpFile);


            for (int i = 1; i <= documentHandler.getNumberOfPages(); i++) {
                LOG.debug("Optimizing page {}", i);
                PDPage page = documentHandler.getPage(i);

                try {
                    if (parameters.isCompressImages()) {
                        optimizeImages(page);
                    }
                } catch (MissingImageReaderException e) {
                    LOG.warn(e.getLocalizedMessage());
                } catch (IOException e) {
                    throw new TaskException(e);
                }
            }

            documentHandler.setVersionOnPDDocument(parameters.getVersion());
            documentHandler.setCompress(parameters.isCompress());
            documentHandler.savePDDocument(tmpFile);

            String outName = nameGenerator(parameters.getOutputPrefix()).generate(
                    nameRequest().originalName(source.getName()).fileNumber(currentStep));
            outputWriter.addOutput(file(tmpFile).name(outName));

            nullSafeCloseQuietly(documentHandler);

            notifyEvent(getNotifiableTaskMetadata()).stepsCompleted(currentStep).outOf(totalSteps);
        }

        parameters.getOutput().accept(outputWriter);
        LOG.debug("Input documents optimized and written to {}", parameters.getOutput());
    }

    private void optimizeImages(PDPage page) throws IOException {
        PDResources pageResources = page.getResources();
        for (COSName xObjectName : pageResources.getXObjectNames()) {
            try {
                PDXObject obj = pageResources.getXObject(xObjectName);
                if (obj instanceof PDImageXObject) {
                    PDImageXObject imageXObject = ((PDImageXObject) obj);
                    LOG.debug("Found image {}x{}", imageXObject.getHeight(), imageXObject.getWidth());

                    File tmpImageFile = ImageOptimizer.optimize(imageXObject.getImage(),
                            parameters.getImageQuality(),
                            parameters.getImageDpi(),
                            parameters.getImageMaxWidthOrHeight()
                    );

                    // images are hashed and cached so only one PDImageXObject is used if the image is repeated
                    String hash = Files.hash(tmpImageFile, Hashing.md5()).toString();
                    PDImageXObject newImage;
                    if(cache.containsKey(hash)){
                        newImage = cache.get(hash).get();
                    } else {
                        newImage = PDImageXObject.createFromFile(tmpImageFile);
                        cache.put(hash, new SoftReference<>(newImage));
                    }

                    pageResources.put(xObjectName, newImage);
                }
            } catch (IOException | RuntimeException ex) {
                LOG.warn("Failed to optimize image, skipping and continuing with next.", ex);
            }
        }
    }

    @Override
    public void after() {
        nullSafeCloseQuietly(documentHandler);
    }

}
