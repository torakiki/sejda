/*
 * Created on 22 gen 2016
 * Copyright 2015 by Andrea Vacondio (andrea.vacondio@gmail.com).
 * This file is part of Sejda.
 *
 * Sejda is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Sejda is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with Sejda.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.sejda.model.parameter;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.sejda.model.input.Source;
import org.sejda.model.output.SingleTaskOutput;
import org.sejda.model.parameter.base.AbstractPdfOutputParameters;
import org.sejda.model.parameter.base.MultipleSourceTaskParameter;
import org.sejda.model.parameter.base.SingleOutputTaskParameters;
import org.sejda.model.pdf.collection.InitialView;
import org.sejda.model.validation.constraint.NotEmpty;
import org.sejda.model.validation.constraint.SingleOutputAllowedExtensions;

/**
 * Parameters for a task that creates a collection of attachments from a list of PDF documents. See Chap 12.3.5 of PDF spec 32000-1:2008
 * 
 * @author Andrea Vacondio
 *
 */
@SingleOutputAllowedExtensions
public class AttachmentsCollectionParameters extends AbstractPdfOutputParameters
        implements SingleOutputTaskParameters, MultipleSourceTaskParameter {

    @NotNull
    private InitialView initialView = InitialView.TILES;
    @NotEmpty
    @Valid
    private final List<Source<?>> sourceList = new ArrayList<>();
    @Valid
    @NotNull
    private SingleTaskOutput output;

    public void addSources(Collection<? extends Source<?>> inputs) {
        sourceList.addAll(inputs);
    }

    /**
     * adds the input source to the source list.
     * 
     * @param input
     */
    @Override
    public void addSource(Source<?> input) {
        sourceList.add(input);
    }

    /**
     * @return an unmodifiable view of the source list
     */
    @Override
    public List<Source<?>> getSourceList() {
        return Collections.unmodifiableList(sourceList);
    }

    public InitialView getInitialView() {
        return initialView;
    }

    public void setInitialView(InitialView initialView) {
        this.initialView = initialView;
    }

    @Override
    public SingleTaskOutput getOutput() {
        return output;
    }

    @Override
    public void setOutput(SingleTaskOutput output) {
        this.output = output;
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder().appendSuper(super.hashCode()).append(initialView).append(output).append(sourceList)
                .toHashCode();
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof AttachmentsCollectionParameters)) {
            return false;
        }
        AttachmentsCollectionParameters parameter = (AttachmentsCollectionParameters) other;
        return new EqualsBuilder().appendSuper(super.equals(other)).append(initialView, parameter.getInitialView())
                .append(output, parameter.output).append(sourceList, parameter.getSourceList()).isEquals();
    }
}
