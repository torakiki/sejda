/*
 * Created on 09/mar/2012
 * Copyright 2011 by Andrea Vacondio (andrea.vacondio@gmail.com).
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
package org.sejda.model.output;

import java.io.IOException;

/**
 * Double-dispatch interface to dispatch to the correct implementation of a {@link TaskOutput}.
 * 
 * @author Andrea Vacondio
 * 
 */
public interface TaskOutputDispatcher {

    /**
     * writes to a {@link FileTaskOutput} destination.
     * 
     * @param output
     * @throws IOException
     */
    void dispatch(FileTaskOutput output) throws IOException;

    /**
     * writes to a {@link DirectoryTaskOutput} destination.
     * 
     * @param output
     * @throws IOException
     */
    void dispatch(DirectoryTaskOutput output) throws IOException;

    void dispatch(FileOrDirectoryTaskOutput output) throws IOException;
}
