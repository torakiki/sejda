/*
 * Created on Oct 11, 2011
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

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.junit.Test;
import org.sejda.cli.command.TestableTask;
import org.sejda.model.exception.SejdaRuntimeException;

import com.lexicalscope.jewel.cli.Option;

/**
 * 
 * Test verifying that short names are not repeated for each task cli interface
 * 
 * @author Eduard Weissmann
 * 
 */
public class OptionDecriptionAndShortNameTest extends AcrossAllTasksTraitTest {

    public OptionDecriptionAndShortNameTest(TestableTask testableTask) {
        super(testableTask);
    }

    @Test
    public void descriptionsHaveOptionalityInformation() {

        for (MethodAndOption eachMethod : extractOptionAnnotations()) {
            String description = eachMethod.getOption().description();
            boolean hasOptionalityInfo = StringUtils.endsWith(description, "(optional)")
                    || StringUtils.endsWith(description, "(required)");
            if (!hasOptionalityInfo) {
                throw new SejdaRuntimeException(
                        getTaskName() + " is missing optionality information [(optional) or (required)] on "
                                + eachMethod.getMethodName());
            }
        }
    }

    @Test
    public void shortNamesAreMandatory() {

        for (MethodAndOption eachMethod : extractOptionAnnotations()) {
            if (eachMethod.getNonBlankShortNames().isEmpty() && eachMethod.isNotBooleanFlag()) {
                throw new SejdaRuntimeException(
                        getTaskName() + " has missing short name on " + eachMethod.getMethodName());
            }
        }
    }

    @Test
    public void shortNamesAreUnique() {
        Map<String, Method> shortNamesMapping = new HashMap<String, Method>();

        for (MethodAndOption eachMethod : extractOptionAnnotations()) {

            for (String eachShortName : eachMethod.getNonBlankShortNames()) {
                if (shortNamesMapping.containsKey(eachShortName)) {
                    throw new SejdaRuntimeException(getTaskName() + " has duplicate short names: '" + eachShortName
                            + "' defined on " + eachMethod.getMethodName() + "() and on "
                            + shortNamesMapping.get(eachShortName).getName() + "()");
                }
                shortNamesMapping.put(eachShortName, eachMethod.getMethod());
            }
        }
    }

    private Collection<MethodAndOption> extractOptionAnnotations() {
        Collection<MethodAndOption> result = new ArrayList<OptionDecriptionAndShortNameTest.MethodAndOption>();

        Class<?> cliCommandClass = testableTask.getCommand().getCliArgumentsClass();

        for (Method eachMethod : cliCommandClass.getMethods()) {
            final Option optionAnnotation = eachMethod.getAnnotation(Option.class);

            if (optionAnnotation == null) {
                continue;
            }

            result.add(new MethodAndOption(eachMethod, optionAnnotation));
        }

        return result;
    }

    class MethodAndOption {
        private final Method method;
        private final Option option;

        /**
         * @param method
         * @param option
         */
        MethodAndOption(Method method, Option option) {
            super();
            this.method = method;
            this.option = option;
        }

        /**
         * @return true if the Option defined by the method is not a flag that has only true/false values (these are known to always be optional options)
         */
        public boolean isNotBooleanFlag() {
            return !method.getReturnType().equals(boolean.class);
        }

        /**
         * @return name of the method
         */
        public String getMethodName() {
            return getMethod().getName();
        }

        public Collection<String> getNonBlankShortNames() {
            Collection<String> result = new ArrayList<String>(Arrays.asList(getOption().shortName()));
            result.remove("");
            return result;
        }

        Method getMethod() {
            return method;
        }

        Option getOption() {
            return option;
        }
    }
}
