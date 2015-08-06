package org.sejda.impl.itext;
import org.sejda.core.service.AlternateMixTaskTest;
import org.sejda.model.parameter.AlternateMixParameters;
import org.sejda.model.task.Task;

/*
 * Created on 25/dic/2010
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
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

/**
 * @author Andrea Vacondio
 *
 */
public class AlternateMixITextTaskTest extends AlternateMixTaskTest {

    public Task<AlternateMixParameters> getTask() {
        return new AlternateMixTask();
    }

}
