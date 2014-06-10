/*
 * Created on 10/giu/2014
 * Copyright 2014 by Andrea Vacondio (andrea.vacondio@gmail.com).
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); 
 * you may not use this file except in compliance with the License. 
 * You may obtain a copy of the License at 
 * 
 * http://www.apache.org/licenses/LICENSE-2.0 
 * 
 * Unless required by applicable law or agreed to in writing, software 
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. 
 * See the License for the specific language governing permissions and 
 * limitations under the License. 
 */
package org.sejda.model.split;

import org.sejda.model.exception.TaskExecutionException;
import org.sejda.model.outline.OutlineGoToPageDestinations;

/**
 * Strategy to define opening and closing page numbers from an {@link OutlineGoToPageDestinations}. The logic behind is that if a GoTo points to page x and the user splits at the
 * level of that GoTo, he probably wants to split at page x-1 (i.e. the end of the preceding chapter or paragraph), resulting in a pdf document per chapter (or paragraph...).
 * 
 * @author Andrea Vacondio
 * 
 */
public class GoToPageDestinationsSplitPages implements NextOutputStrategy {

    private SplitPages delegate = new SplitPages();

    public GoToPageDestinationsSplitPages(OutlineGoToPageDestinations destinations) {
        for (Integer page : destinations.getPages()) {
            delegate.add(page - 1);
        }
    }

    public void ensureIsValid() throws TaskExecutionException {
        delegate.ensureIsValid();
    }

    public boolean isOpening(Integer page) {
        return delegate.isOpening(page);
    }

    public boolean isClosing(Integer page) {
        return delegate.isClosing(page);
    }

}
