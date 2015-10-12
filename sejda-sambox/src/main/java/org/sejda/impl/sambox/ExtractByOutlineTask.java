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
package org.sejda.impl.sambox;

import org.sejda.impl.sambox.component.DefaultPdfSourceOpener;
import org.sejda.impl.sambox.component.PDDocumentHandler;
import org.sejda.impl.sambox.component.SamboxOutlineLevelsHandler;
import org.sejda.impl.sambox.component.split.PageDestinationsLevelPdfExtractor;
import org.sejda.model.exception.TaskException;
import org.sejda.model.input.PdfSourceOpener;
import org.sejda.model.outline.OutlineExtractPageDestinations;
import org.sejda.model.parameter.ExtractByOutlineParameters;
import org.sejda.model.task.BaseTask;
import org.sejda.sambox.pdmodel.PDDocument;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.sejda.common.ComponentsUtility.nullSafeCloseQuietly;

/**
 * Extract chapters to separate documents based on the bookmarks in the outline
 */
public class ExtractByOutlineTask extends BaseTask<ExtractByOutlineParameters> {

    private static final Logger LOG = LoggerFactory.getLogger(ExtractByOutlineTask.class);

    private PDDocument document = null;
    private PdfSourceOpener<PDDocumentHandler> documentLoader;

    @Override
    public void before(ExtractByOutlineParameters parameters) {
        documentLoader = new DefaultPdfSourceOpener();
    }

    @Override
    public void execute(ExtractByOutlineParameters parameters) throws TaskException {
        LOG.debug("Opening {} ", parameters.getSource());
        document = parameters.getSource().open(documentLoader).getUnderlyingPDDocument();

        LOG.debug("Retrieving outline information for level {} and match regex {}", parameters.getLevel(), parameters.getMatchingTitleRegEx());
        OutlineExtractPageDestinations pagesDestination = new SamboxOutlineLevelsHandler(document,
                parameters.getMatchingTitleRegEx()).getExtractPageDestinations(parameters.getLevel());

        LOG.debug("Starting extraction by outline, level {} and match regex {}", parameters.getLevel(), parameters.getMatchingTitleRegEx());
        new PageDestinationsLevelPdfExtractor(document, parameters, pagesDestination).extract(getNotifiableTaskMetadata());

        LOG.debug("Extraction completed and outputs written to {}", parameters.getOutput());
    }

    @Override
    public void after() {
        nullSafeCloseQuietly(document);
    }
}
