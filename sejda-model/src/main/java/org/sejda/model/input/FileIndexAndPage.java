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
