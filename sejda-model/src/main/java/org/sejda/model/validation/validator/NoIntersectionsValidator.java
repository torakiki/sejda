/*
 * Created on 11/ago/2011
 * Copyright 2011 by Andrea Vacondio (andrea.vacondio@gmail.com).
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
package org.sejda.model.validation.validator;

import java.util.ArrayList;
import java.util.List;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.sejda.model.pdf.page.PageRange;
import org.sejda.model.pdf.page.PageRangeSelection;
import org.sejda.model.validation.constraint.NoIntersections;

/**
 * Validator for a {@link NoIntersections} constraint to ensure page ranges in an input {@link PageRangeSelection} do not intersect.
 * 
 * @author Andrea Vacondio
 * 
 */
public class NoIntersectionsValidator implements ConstraintValidator<NoIntersections, PageRangeSelection> {

    public void initialize(NoIntersections constraintAnnotation) {
        // on purpose
    }

    public boolean isValid(PageRangeSelection value, ConstraintValidatorContext context) {
        if (value != null) {
            List<PageRange> ranges = new ArrayList<PageRange>(value.getPageSelection());
            for (int i = 0; i < ranges.size(); i++) {
                PageRange range = ranges.get(i);
                for (int j = i + 1; j < ranges.size(); j++) {
                    PageRange current = ranges.get(j);
                    if (range.intersects(current)) {
                        context.disableDefaultConstraintViolation();
                        context.buildConstraintViolationWithTemplate(
                                String.format("Invalid page ranges, found an intersection between %s and %s", range,
                                        current)).addNode("pdfMergeInput").addConstraintViolation();
                        return false;
                    }
                }
            }
        }
        return true;
    }

}