/*
 * Created on 27/apr/2010
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
package org.sejda.model.parameter.base;

import org.sejda.model.output.ExistingOutputPolicy;
import org.sejda.model.output.TaskOutput;

/**
 * Model for a task parameters used during the task executions
 * 
 * @author Andrea Vacondio
 * 
 */
public interface TaskParameters {

    /**
     * @return output destination where the result of the manipulation is placed
     */
    TaskOutput getOutput();

    /**
     * @return the policy to use in case of an already existing output is found.
     */
    ExistingOutputPolicy getExistingOutputPolicy();

    /**
     * sets the policy to use in case of an already existing output is found.
     * 
     * @param existingOutputPolicy
     *            the policy to set
     */
    void setExistingOutputPolicy(ExistingOutputPolicy existingOutputPolicy);

    /**
     * @return if the task has to be executed in lenient mode, trying to overcome execution errors
     */
    boolean isLenient();

    /**
     * @param lenient
     *            true if the task has to be executed in lenient mode, trying to overcome execution errors
     */
    void setLenient(boolean lenient);
}
