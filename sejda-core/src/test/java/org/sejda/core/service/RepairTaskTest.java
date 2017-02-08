/*
 * Created on 06 feb 2017
 * Copyright 2017 by Andrea Vacondio (andrea.vacondio@gmail.com).
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
package org.sejda.core.service;

import java.io.IOException;

import org.junit.Ignore;
import org.junit.Test;
import org.sejda.model.output.ExistingOutputPolicy;
import org.sejda.model.parameter.RepairParameters;

/**
 * @author Andrea Vacondio
 *
 */
@Ignore
public abstract class RepairTaskTest extends BaseTaskTest<RepairParameters> {

    private RepairParameters parameters;

    @Test
    public void invalidKid() throws IOException {
        parameters = new RepairParameters();
        parameters.setCompress(false);
        parameters.addSource(customInput("pdf/invalid_kid.pdf"));
        parameters.setExistingOutputPolicy(ExistingOutputPolicy.OVERWRITE);
        testContext.directoryOutputTo(parameters);
        execute(parameters);
        testContext.assertTaskCompleted();
        testContext.assertPages(10);
    }

    @Test
    public void trunkated() throws IOException {
        parameters = new RepairParameters();
        parameters.setCompress(false);
        parameters.addSource(customInput("pdf/trunkated.pdf"));
        parameters.setExistingOutputPolicy(ExistingOutputPolicy.OVERWRITE);
        testContext.directoryOutputTo(parameters);
        execute(parameters);
        testContext.assertTaskCompleted();
        testContext.assertPages(11);
    }

    @Test
    public void failingTask() throws IOException {
        parameters = new RepairParameters();
        parameters.setCompress(false);
        parameters.addSource(customInput("pdf/invalid_page_stream.pdf"));
        parameters.setExistingOutputPolicy(ExistingOutputPolicy.OVERWRITE);
        testContext.directoryOutputTo(parameters);
        execute(parameters);
        testContext.assertEmptyMultipleOutput();
    }
}
