/*
 * Created on 12/set/2014
 * Copyright 2014 by Andrea Vacondio (andrea.vacondio@gmail.com).
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
