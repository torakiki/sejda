/*
 * Created on Jul 11, 2011
 * Copyright 2011 by Nero Couvalli (angelthepunisher@gmail.com).
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
package org.sejda.impl.pdfbox;

import org.sejda.core.service.SetMetadataTaskTest;
import org.sejda.model.parameter.SetMetadataParameters;
import org.sejda.model.task.Task;

/**
 * set metadata task test for the pdfbox implementation
 *
 * @author Nero Couvalli
 *
 */
public class SetMetadataPdfBoxTaskTest extends SetMetadataTaskTest {

    public Task<SetMetadataParameters> getTask() {
        return new SetMetadataTask();
    }

}