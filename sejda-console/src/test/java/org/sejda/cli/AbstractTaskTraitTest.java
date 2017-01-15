/*
 * Created on Aug 29, 2011
 * Copyright 2010 by Eduard Weissmann (edi.weissmann@gmail.com).
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
package org.sejda.cli;

import java.util.Collection;
import java.util.stream.Collectors;

import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.sejda.cli.command.TestableTask;

/**
 * Abstract base class for tests for task traits
 * 
 * @author Eduard Weissmann
 * 
 */
@RunWith(Parameterized.class)
public abstract class AbstractTaskTraitTest extends AbstractTaskTest {

    public AbstractTaskTraitTest(TestableTask testableTask) {
        super(testableTask);
    }

    public static <T> Collection<Object[]> asParameterizedTestData(Collection<T> items) {
        return items.stream().map(i -> new Object[] { i }).collect(Collectors.toList());
    }
}
