/*
 * Copyright 2015 by Andrea Vacondio (andrea.vacondio@gmail.com).
 * 
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
package org.sejda.impl.sambox.component;

import static org.sejda.core.notification.dsl.ApplicationEventsNotifier.notifyEvent;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.sejda.common.ComponentsUtility;
import org.sejda.model.exception.TaskException;
import org.sejda.model.input.PdfMixInput;
import org.sejda.model.task.TaskExecutionContext;
import org.sejda.sambox.pdmodel.PDPage;

/**
 * Component providing functionalities to perform an alternate mix on two {@link PdfMixInput}.
 * 
 * @author Andrea Vacondio
 * 
 */
public class PdfAlternateMixer extends PDDocumentHandler {

    private List<PdfMixFragment> mixFragments = new ArrayList<>();
    private int currentStep = 0;

    /**
     * Perform the alternate mix on the given {@link PdfMixInput}s.
     * 
     * @param executionContext
     * @throws TaskException
     */
    public void mix(List<PdfMixInput> inputs, TaskExecutionContext executionContext) throws TaskException {
        setCreatorOnPDDocument();
        for (PdfMixInput input : inputs) {
            mixFragments.add(PdfMixFragment.newInstance(input));
        }
        int totalSteps = mixFragments.stream().map(PdfMixFragment::getNumberOfPages).reduce(0,
                (curr, value) -> curr + value);

        while (mixFragments.stream().anyMatch(PdfMixFragment::hasNextPage)) {
            executionContext.assertTaskNotCancelled();
            mixFragments.stream().filter(PdfMixFragment::hasNextPage).forEach(f -> {
                for (int i = 0; i < f.getStep() && f.hasNextPage(); i++) {
                    PDPage current = f.nextPage();
                    f.addLookupEntry(current, importPage(current));
                    notifyEvent(executionContext.notifiableTaskMetadata()).stepsCompleted(++currentStep)
                            .outOf(totalSteps);
                }
            });
        }

        mixFragments.stream().forEach(PdfMixFragment::saintizeAnnotations);
    }

    @Override
    public void close() throws IOException {
        super.close();
        mixFragments.stream().forEach(ComponentsUtility::nullSafeCloseQuietly);
        mixFragments.clear();
        currentStep = 0;
    }

}
