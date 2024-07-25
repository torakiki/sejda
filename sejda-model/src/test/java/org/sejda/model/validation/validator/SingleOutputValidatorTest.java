/*
 * Created on 05 mag 2017
 * Copyright 2017 Sober Lemur S.r.l. and Sejda BV.
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
package org.sejda.model.validation.validator;

import jakarta.validation.ConstraintValidatorContext;
import jakarta.validation.ConstraintValidatorContext.ConstraintViolationBuilder;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.sejda.model.output.ExistingOutputPolicy;
import org.sejda.model.output.FileTaskOutput;
import org.sejda.model.parameter.SetPagesLabelParameters;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.contains;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * @author Andrea Vacondio
 */
public class SingleOutputValidatorTest {

    private final SingleOutputValidator victim = new SingleOutputValidator();

    @TempDir
    public Path folder;

    @Test
    public void testNull() {
        assertTrue(victim.isValid(null, null));
    }

    @Test
    public void testNullOutput() {
        assertFalse(victim.isValid(new SetPagesLabelParameters(), null));
    }

    @Test
    public void destinationExistsPolicyOverwrite() throws IOException {
        SetPagesLabelParameters params = new SetPagesLabelParameters();
        params.setOutput(new FileTaskOutput(Files.createTempFile(folder, null, ".pdf").toFile()));
        params.setExistingOutputPolicy(ExistingOutputPolicy.OVERWRITE);
        assertTrue(victim.isValid(params, null));
    }

    @Test
    public void destinationExistsPolicyRename() throws IOException {
        SetPagesLabelParameters params = new SetPagesLabelParameters();
        params.setOutput(new FileTaskOutput(Files.createTempFile(folder, null, ".pdf").toFile()));
        params.setExistingOutputPolicy(ExistingOutputPolicy.RENAME);
        assertTrue(victim.isValid(params, null));
    }

    @Test
    public void destinationExistsPolicyFail() throws IOException {
        ConstraintValidatorContext context = mock(ConstraintValidatorContext.class);
        ConstraintViolationBuilder builder = mock(ConstraintViolationBuilder.class);
        when(context.buildConstraintViolationWithTemplate(anyString())).thenReturn(builder);
        SetPagesLabelParameters params = new SetPagesLabelParameters();
        params.setOutput(new FileTaskOutput(Files.createTempFile(folder, null, ".pdf").toFile()));
        params.setExistingOutputPolicy(ExistingOutputPolicy.FAIL);
        assertFalse(victim.isValid(params, context));
        verify(context).buildConstraintViolationWithTemplate(contains("File destination already exists"));
    }

    @Test
    public void destinationExistsPolicySkip() throws IOException {
        ConstraintValidatorContext context = mock(ConstraintValidatorContext.class);
        ConstraintViolationBuilder builder = mock(ConstraintViolationBuilder.class);
        when(context.buildConstraintViolationWithTemplate(anyString())).thenReturn(builder);
        SetPagesLabelParameters params = new SetPagesLabelParameters();
        params.setOutput(new FileTaskOutput(Files.createTempFile(folder, null, ".pdf").toFile()));
        params.setExistingOutputPolicy(ExistingOutputPolicy.SKIP);
        assertFalse(victim.isValid(params, context));
        verify(context).buildConstraintViolationWithTemplate(contains("File destination already exists"));
    }
}
