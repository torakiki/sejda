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

import org.sejda.commons.util.IOUtils;
import org.sejda.model.exception.TaskException;
import org.sejda.model.input.PdfMixInput;
import org.sejda.model.task.TaskExecutionContext;
import org.sejda.sambox.pdmodel.PDPage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.sejda.core.notification.dsl.ApplicationEventsNotifier.notifyEvent;

/**
 * Component providing functionalities to perform an alternate mix on two {@link PdfMixInput}.
 * 
 * @author Andrea Vacondio
 * 
 */
public class PdfAlternateMixer extends PDDocumentHandler {


    private List<PdfMixFragment> mixFragments = new ArrayList<>();

    /**
     * Perform the alternate mix on the given {@link PdfMixInput}s.
     * 
     * @param executionContext
     * @throws TaskException
     */
    public void mix(List<PdfMixInput> inputs, TaskExecutionContext executionContext) throws TaskException {
        setCreatorOnPDDocument();
        for (PdfMixInput input : inputs) {
            executionContext.notifiableTaskMetadata().setCurrentSource(input.getSource());
            mixFragments.add(PdfMixFragment.newInstance(input));
        }
        
        int maxNumberOfPages = 0;
        for(PdfMixFragment fragment: mixFragments) {
            maxNumberOfPages = Math.max(fragment.getNumberOfPages(), maxNumberOfPages);
        }
        
        int currentStep = 0;
        int maxSteps = mixFragments.size() * maxNumberOfPages + 1;

        // to properly calculate the expected total number of steps we'd have to look at page numbers, steps, reverse
        notifyEvent(executionContext.notifiableTaskMetadata()).progressUndetermined();

        while (mixFragments.stream().anyMatch(PdfMixFragment::hasNotReachedTheEnd)) {
            mixFragments.stream().filter(PdfMixFragment::hasNextPage).forEach(f -> {
                for (int i = 0; i < f.getStep() && f.hasNextPage(); i++) {
                    executionContext.notifiableTaskMetadata().setCurrentSource(f.source());
                    PDPage current = f.nextPage();
                    f.addLookupEntry(current, importPage(current));
                }
            });

            currentStep++;

            // a safety net so we don't loop here forever due to a bug and fill up the disk
            if (currentStep > maxSteps) {
                throw new RuntimeException("Too many loops, currentStep: " + currentStep + ", maxSteps: " + maxSteps);
            }
        }

        mixFragments.forEach(f -> {
            executionContext.notifiableTaskMetadata().setCurrentSource(f.source());
            f.saintizeAnnotations();
        });
        executionContext.notifiableTaskMetadata().clearCurrentSource();
    }

    @Override
    public void close() throws IOException {
        super.close();
        mixFragments.forEach(IOUtils::closeQuietly);
        mixFragments.clear();
    }

}
