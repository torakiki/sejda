/*
 * Created on 12/set/2014
 * Copyright 2014 by Andrea Vacondio (andrea.vacondio@gmail.com).
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

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import javax.validation.ConstraintValidatorContext;
import javax.validation.ConstraintValidatorContext.ConstraintViolationBuilder;
import javax.validation.ConstraintValidatorContext.ConstraintViolationBuilder.NodeBuilderDefinedContext;

import org.junit.Before;
import org.junit.Test;
import org.sejda.model.parameter.base.AbstractPdfOutputParameters;
import org.sejda.model.pdf.PdfVersion;

/**
 * @author Andrea Vacondio
 * 
 */
public class PdfVersionValidatorTest {
    private PdfVersionValidator victim = new PdfVersionValidator();
    private AbstractPdfOutputParameters params;

    @Before
    public void setUp() {
        params = mock(AbstractPdfOutputParameters.class);
    }

    @Test
    public void testNull() {
        assertTrue(victim.isValid(null, null));
    }

    @Test
    public void testNullValue() {
        when(params.getVersion()).thenReturn(null);
        assertTrue(victim.isValid(params, null));
    }

    @Test
    public void testValidValue() {
        when(params.getVersion()).thenReturn(PdfVersion.VERSION_1_5);
        when(params.getMinRequiredPdfVersion()).thenReturn(PdfVersion.VERSION_1_2);
        assertTrue(victim.isValid(params, null));
    }

    @Test
    public void testInvalidValue() {
        ConstraintValidatorContext context = mock(ConstraintValidatorContext.class);
        ConstraintViolationBuilder builder = mock(ConstraintViolationBuilder.class);
        when(context.buildConstraintViolationWithTemplate(anyString())).thenReturn(builder);
        NodeBuilderDefinedContext nodeBuilderContext = mock(NodeBuilderDefinedContext.class);
        when(builder.addNode(anyString())).thenReturn(nodeBuilderContext);
        when(params.getVersion()).thenReturn(PdfVersion.VERSION_1_2);
        when(params.getMinRequiredPdfVersion()).thenReturn(PdfVersion.VERSION_1_5);
        assertFalse(victim.isValid(params, context));
        verify(context).buildConstraintViolationWithTemplate(anyString());
    }

}
