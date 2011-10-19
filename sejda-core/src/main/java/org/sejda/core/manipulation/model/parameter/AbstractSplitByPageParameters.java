/*
 * Created on 03/ago/2011
 * Copyright 2010 by Andrea Vacondio (andrea.vacondio@gmail.com).
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
package org.sejda.core.manipulation.model.parameter;

import java.util.Set;

import org.sejda.core.manipulation.model.parameter.base.SinglePdfSourceMultipleOutputParameters;
import org.sejda.core.manipulation.model.pdf.page.PagesSelection;

/**
 * Skeletal implementation for a split by page parameter class.
 * 
 * @author Andrea Vacondio
 * 
 */
public abstract class AbstractSplitByPageParameters extends SinglePdfSourceMultipleOutputParameters implements
        PagesSelection {

    /**
     * @param upperLimit
     *            upper limit for the pages set.
     * @return the set of pages to split at. All pages are greater then 0 and lesser then upperLimit.
     */
    public abstract Set<Integer> getPages(int upperLimit);

}
