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
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.sejda.impl.sambox.component.split;

import org.sejda.core.support.io.MultipleOutputWriter;
import org.sejda.core.support.io.OutputWriters;
import org.sejda.impl.sambox.component.PagesExtractor;
import org.sejda.model.exception.TaskException;
import org.sejda.model.exception.TaskExecutionException;
import org.sejda.model.outline.OutlineExtractPageDestinations;
import org.sejda.model.parameter.ExtractByOutlineParameters;
import org.sejda.model.task.NotifiableTaskMetadata;
import org.sejda.sambox.pdmodel.PDDocument;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

import static org.sejda.core.notification.dsl.ApplicationEventsNotifier.notifyEvent;
import static org.sejda.core.support.io.IOUtils.createTemporaryPdfBuffer;
import static org.sejda.core.support.io.model.FileOutput.file;
import static org.sejda.core.support.prefix.NameGenerator.nameGenerator;
import static org.sejda.core.support.prefix.model.NameGenerationRequest.nameRequest;

/**
 * Extracts separate docs based on selected outline page sections
 */
public class PageDestinationsLevelPdfExtractor {

    private static final Logger LOG = LoggerFactory.getLogger(PageDestinationsLevelPdfExtractor.class);

    private final OutlineExtractPageDestinations outlineDestinations;
    private final ExtractByOutlineParameters parameters;
    private final PDDocument document;
    private final MultipleOutputWriter outputWriter;

    public PageDestinationsLevelPdfExtractor(PDDocument document, ExtractByOutlineParameters parameters,
                                             OutlineExtractPageDestinations outlineDestinations) {
        this.outlineDestinations = outlineDestinations;
        this.parameters = parameters;
        this.document = document;
        this.outputWriter = OutputWriters.newMultipleOutputWriter(parameters.isOverwrite());
    }

    public void extract(NotifiableTaskMetadata taskMetadata) throws TaskException {
        int outputDocumentsCounter = 0;

        try (PagesExtractor extractor = new PagesExtractor(document)) {
            int totalExtractions = outlineDestinations.sections.size();

            if(totalExtractions == 0) {
                throw new TaskExecutionException("No page has been selected for extraction.");
            }

            for(int s = 0; s < totalExtractions; s++) {
                OutlineExtractPageDestinations.OutlineItemBoundaries section = outlineDestinations.sections.get(s);
                // open
                int page = section.startPage;
                LOG.debug("Starting extracting {} pages {} {}", section.title, section.startPage, section.endPage);

                outputDocumentsCounter++;
                File tmpFile = createTemporaryPdfBuffer();
                LOG.debug("Created output temporary buffer {}", tmpFile);

                String outName = nameGenerator(parameters.getOutputPrefix()).generate(nameRequest()
                        .page(page)
                        .originalName(parameters.getSource().getName())
                        .fileNumber(outputDocumentsCounter)
                        .bookmark(section.title)
                );
                outputWriter.addOutput(file(tmpFile).name(outName));

                for(; page <= section.endPage; page++) {
                    // retain
                    LOG.trace("Retaining page {} of the original document", page);
                    extractor.retain(page);
                }

                // close
                extractor.setVersion(parameters.getVersion());
                extractor.setCompress(parameters.isCompress());
                extractor.save(tmpFile);
                extractor.reset();
                LOG.debug("Ending extracting {}", section.title);

                notifyEvent(taskMetadata).stepsCompleted(s).outOf(totalExtractions);
            }
        }
        parameters.getOutput().accept(outputWriter);
    }
}
