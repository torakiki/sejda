/* 
 * Created on 06/mar/2015
 * Copyright 2013-2014 Sober Lemur S.r.l. and Sejda BV.
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
package org.sejda.impl.sambox.component.split;

import org.sejda.core.support.prefix.model.NameGenerationRequest;
import org.sejda.model.parameter.AbstractSplitByPageParameters;
import org.sejda.model.split.NextOutputStrategy;
import org.sejda.model.split.SplitPages;
import org.sejda.sambox.pdmodel.PDDocument;

/**
 * Component providing split by pages functionalities.
 * 
 * @author Andrea Vacondio
 * @param <T>
 *            the type of parameters the splitter needs to have all the information necessary to perform the split.
 */
public class PagesPdfSplitter<T extends AbstractSplitByPageParameters> extends AbstractPdfSplitter<T> {

    private NextOutputStrategy splitPages;

    public PagesPdfSplitter(PDDocument document, T parameters, boolean optimize) {
        super(document, parameters, optimize, parameters.discardOutline());
        this.splitPages = new SplitPages(parameters.getPages(document.getNumberOfPages()));
    }

    @Override
    public NextOutputStrategy nextOutputStrategy() {
        return splitPages;
    }

    @Override
    public NameGenerationRequest enrichNameGenerationRequest(NameGenerationRequest request) {
        return request;
    }

}
