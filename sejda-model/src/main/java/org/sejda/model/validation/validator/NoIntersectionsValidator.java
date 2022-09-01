/*
 * Created on 11/ago/2011
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
package org.sejda.model.validation.validator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.sejda.model.pdf.page.PageRange;
import org.sejda.model.pdf.page.PageRangeSelection;
import org.sejda.model.validation.constraint.NoIntersections;

import java.util.ArrayList;
import java.util.List;

/**
 * Validator for a {@link NoIntersections} constraint to ensure page ranges in an input {@link PageRangeSelection} do not intersect.
 *
 * @author Andrea Vacondio
 */
public class NoIntersectionsValidator implements ConstraintValidator<NoIntersections, PageRangeSelection> {

    @Override
    public boolean isValid(PageRangeSelection value, ConstraintValidatorContext context) {
        if (value != null) {
            List<PageRange> ranges = new ArrayList<>(value.getPageSelection());
            for (int i = 0; i < ranges.size(); i++) {
                PageRange range = ranges.get(i);
                for (int j = i + 1; j < ranges.size(); j++) {
                    PageRange current = ranges.get(j);
                    if (range.intersects(current)) {
                        context.disableDefaultConstraintViolation();
                        context.buildConstraintViolationWithTemplate(
                                String.format("Invalid page ranges, found an intersection between %s and %s", range,
                                        current)).addPropertyNode("page ranges").addConstraintViolation();
                        return false;
                    }
                }
            }
        }
        return true;
    }

}