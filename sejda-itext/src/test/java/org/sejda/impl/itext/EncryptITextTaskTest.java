/*
 * Created on 30/ott/2010
 *
 * Copyright 2010 by Andrea Vacondio (andrea.vacondio@gmail.com).
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
package org.sejda.impl.itext;

import org.sejda.core.service.EncryptTaskTest;
import org.sejda.model.parameter.EncryptParameters;
import org.sejda.model.task.Task;

/**
 * Encrypt task test for the itext implementation
 * 
 * @author Andrea Vacondio
 * 
 */
public class EncryptITextTaskTest extends EncryptTaskTest {

    @Override
    public Task<EncryptParameters> getTask() {
        return new EncryptTask();
    }

}
