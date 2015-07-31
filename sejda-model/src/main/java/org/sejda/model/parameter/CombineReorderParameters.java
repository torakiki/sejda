/*
 * Copyright 2015 by Edi Weissmann (edi.weissmann@gmail.com)
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
package org.sejda.model.parameter;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.sejda.model.input.FileIndexAndPage;
import org.sejda.model.parameter.base.MultiplePdfSourceSingleOutputParameters;
import org.sejda.model.parameter.base.SingleOutputTaskParameters;
import org.sejda.model.validation.constraint.NotEmpty;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;

/**
 * Parameters specifying a list of pdf sources and an ordered list of pages from each file, that should be combined into one pdf output
 * Allows pages to appear in a different order in the output than in the original source.
 */
public class CombineReorderParameters extends MultiplePdfSourceSingleOutputParameters implements SingleOutputTaskParameters {

    @NotEmpty
    @Valid
    private List<FileIndexAndPage> pages = new ArrayList<FileIndexAndPage>();

    private boolean copyFormFields = false;

    public void addPage(int fileIndex, int page) {
        pages.add(new FileIndexAndPage(fileIndex, page));
    }

    public List<FileIndexAndPage> getPages() {
        return pages;
    }

    public boolean isCopyFormFields() {
        return copyFormFields;
    }

    public void setCopyFormFields(boolean copyFormFields) {
        this.copyFormFields = copyFormFields;
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder().appendSuper(super.hashCode()).append(this.pages).append(this.copyFormFields).toHashCode();
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
        return new EqualsBuilder().appendSuper(super.equals(obj)).append(this.pages, other.pages).append(this.copyFormFields, other.copyFormFields).isEquals();
    }
}
