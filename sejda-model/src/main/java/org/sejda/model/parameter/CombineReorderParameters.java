/*
 * Copyright 2015 by Edi Weissmann (edi.weissmann@gmail.com)
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
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.sejda.model.parameter;

import java.util.ArrayList;
import java.util.List;

import javax.validation.constraints.NotNull;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.sejda.model.input.FileIndexAndPage;
import org.sejda.model.outline.OutlinePolicy;
import org.sejda.model.parameter.base.MultiplePdfSourceSingleOutputParameters;
import org.sejda.model.pdf.form.AcroFormPolicy;
import org.sejda.model.rotation.Rotation;
import org.sejda.model.validation.constraint.NotEmpty;
import org.sejda.model.validation.constraint.SingleOutputAllowedExtensions;

/**
 * Parameters specifying a list of pdf sources and an ordered list of pages from each file, that should be combined into one pdf output
 * Allows pages to appear in a different order in the output than in the original source.
 */
@SingleOutputAllowedExtensions
public class CombineReorderParameters extends MultiplePdfSourceSingleOutputParameters {

    @NotEmpty
    private List<FileIndexAndPage> pages = new ArrayList<FileIndexAndPage>();

    @NotNull
    private AcroFormPolicy acroFormPolicy = AcroFormPolicy.MERGE_RENAMING_EXISTING_FIELDS;

    @NotNull
    private OutlinePolicy outlinePolicy = OutlinePolicy.RETAIN;

    public void addPage(int fileIndex, int page) {
        pages.add(new FileIndexAndPage(fileIndex, page));
    }

    public void addPage(int fileIndex, int page, Rotation rotation) {
        pages.add(new FileIndexAndPage(fileIndex, page, rotation));
    }

    public List<FileIndexAndPage> getPages() {
        return pages;
    }

    public AcroFormPolicy getAcroFormPolicy() {
        return this.acroFormPolicy;
    }

    /**
     * The policy that the merge task should use when handling interactive forms (AcroForms)
     * 
     * @param acroFormPolicy
     */
    public void setAcroFormPolicy(AcroFormPolicy acroFormPolicy) {
        this.acroFormPolicy = acroFormPolicy;
    }

    public OutlinePolicy getOutlinePolicy() {
        return outlinePolicy;
    }

    public void setOutlinePolicy(OutlinePolicy outlinePolicy) {
        this.outlinePolicy = outlinePolicy;
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder().appendSuper(super.hashCode()).append(this.pages).append(this.acroFormPolicy)
                .append(outlinePolicy).toHashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final CombineReorderParameters other = (CombineReorderParameters) obj;
        return new EqualsBuilder().appendSuper(super.equals(obj)).append(this.pages, other.pages)
                .append(this.acroFormPolicy, other.acroFormPolicy)
                .append(this.outlinePolicy, other.outlinePolicy).isEquals();
    }
}
