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
package org.sejda.model.input;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

/**
 * A page and the associated file, referenced by its index in the sources list
 */
public class FileIndexAndPage {
    
    private int fileIndex;
    private int page;

    public FileIndexAndPage(int fileIndex, int page) {
        this.fileIndex = fileIndex;
        this.page = page;
    }

    public int getFileIndex() {
        return fileIndex;
    }

    public int getPage() {
        return page;
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder().append(this.fileIndex).append(this.page).toHashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final FileIndexAndPage other = (FileIndexAndPage) obj;
        return new EqualsBuilder().append(this.fileIndex, other.fileIndex).append(this.page, other.page).isEquals();
    }

    @Override
    public String toString() {
        return String.format("%d:%d", fileIndex, page);
    }
}
