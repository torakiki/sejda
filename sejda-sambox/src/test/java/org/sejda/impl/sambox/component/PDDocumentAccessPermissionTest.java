/*
 * Created on 13/set/2011
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
package org.sejda.impl.sambox.component;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.sejda.core.Sejda;
import org.sejda.model.exception.TaskPermissionsException;
import org.sejda.model.pdf.encryption.PdfAccessPermission;
import org.sejda.sambox.pdmodel.PDDocument;
import org.sejda.sambox.pdmodel.encryption.AccessPermission;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * @author Andrea Vacondio
 */
public class PDDocumentAccessPermissionTest {

    private PDDocumentAccessPermission victim;
    private AccessPermission permission;

    @BeforeEach
    public void setUp() {
        PDDocument document = mock(PDDocument.class);
        permission = mock(AccessPermission.class);
        when(document.getCurrentAccessPermission()).thenReturn(permission);
        victim = new PDDocumentAccessPermission(document);
    }

    @Test
    public void testNull() {
        assertThrows(IllegalArgumentException.class, () -> new PDDocumentAccessPermission(null));
    }

    @Test
    public void testNotOwner() {
        when(permission.isOwnerPermission()).thenReturn(Boolean.FALSE);
        assertThrows(TaskPermissionsException.class, () -> victim.ensureOwnerPermissions());
    }

    @Test
    public void testOwner() throws TaskPermissionsException {
        when(permission.isOwnerPermission()).thenReturn(Boolean.TRUE);
        victim.ensureOwnerPermissions();
    }

    @Test
    public void testOwnerUnethical() throws TaskPermissionsException {
        when(permission.isOwnerPermission()).thenReturn(Boolean.FALSE);
        System.setProperty(Sejda.UNETHICAL_READ_PROPERTY_NAME, "true");
        victim.ensureOwnerPermissions();
        System.setProperty(Sejda.UNETHICAL_READ_PROPERTY_NAME, "false");
    }

    @Test
    public void testNotPrint() {
        when(permission.canPrint()).thenReturn(Boolean.FALSE);
        assertThrows(TaskPermissionsException.class, () -> victim.ensurePermission(PdfAccessPermission.PRINT));
    }

    @Test
    public void testPrint() throws TaskPermissionsException {
        when(permission.canPrint()).thenReturn(Boolean.TRUE);
        victim.ensurePermission(PdfAccessPermission.PRINT);
    }

    @Test
    public void testPrintUnethical() throws TaskPermissionsException {
        System.setProperty(Sejda.UNETHICAL_READ_PROPERTY_NAME, "true");
        when(permission.canPrint()).thenReturn(Boolean.FALSE);
        victim.ensurePermission(PdfAccessPermission.PRINT);
        System.setProperty(Sejda.UNETHICAL_READ_PROPERTY_NAME, "false");
    }

    @Test
    public void testNotDegradedPrint() {
        when(permission.canPrintDegraded()).thenReturn(Boolean.FALSE);
        assertThrows(TaskPermissionsException.class,
                () -> victim.ensurePermission(PdfAccessPermission.DEGRADATED_PRINT));
    }

    @Test
    public void testDegradedPrint() throws TaskPermissionsException {
        when(permission.canPrintDegraded()).thenReturn(Boolean.TRUE);
        victim.ensurePermission(PdfAccessPermission.DEGRADATED_PRINT);
    }

    @Test
    public void testNotAssemble() {
        when(permission.canAssembleDocument()).thenReturn(Boolean.FALSE);
        assertThrows(TaskPermissionsException.class, () -> victim.ensurePermission(PdfAccessPermission.ASSEMBLE));
    }

    @Test
    public void testAssemble() throws TaskPermissionsException {
        when(permission.canAssembleDocument()).thenReturn(Boolean.TRUE);
        victim.ensurePermission(PdfAccessPermission.ASSEMBLE);
    }

    @Test
    public void testNotAannotation() {
        when(permission.canModifyAnnotations()).thenReturn(Boolean.FALSE);
        assertThrows(TaskPermissionsException.class, () -> victim.ensurePermission(PdfAccessPermission.ANNOTATION));
    }

    @Test
    public void testAnnotation() throws TaskPermissionsException {
        when(permission.canModifyAnnotations()).thenReturn(Boolean.TRUE);
        victim.ensurePermission(PdfAccessPermission.ANNOTATION);
    }

    @Test
    public void testNotCopy() {
        when(permission.canExtractContent()).thenReturn(Boolean.FALSE);
        assertThrows(TaskPermissionsException.class,
                () -> victim.ensurePermission(PdfAccessPermission.COPY_AND_EXTRACT));
    }

    @Test
    public void testCopy() throws TaskPermissionsException {
        when(permission.canExtractContent()).thenReturn(Boolean.TRUE);
        victim.ensurePermission(PdfAccessPermission.COPY_AND_EXTRACT);
    }

    @Test
    public void testNotExtract() {
        when(permission.canExtractForAccessibility()).thenReturn(Boolean.FALSE);
        assertThrows(TaskPermissionsException.class,
                () -> victim.ensurePermission(PdfAccessPermission.EXTRACTION_FOR_DISABLES));
    }

    @Test
    public void testExtract() throws TaskPermissionsException {
        when(permission.canExtractForAccessibility()).thenReturn(Boolean.TRUE);
        victim.ensurePermission(PdfAccessPermission.EXTRACTION_FOR_DISABLES);
    }

    @Test
    public void testNotFillForm() {
        when(permission.canFillInForm()).thenReturn(Boolean.FALSE);
        assertThrows(TaskPermissionsException.class, () -> victim.ensurePermission(PdfAccessPermission.FILL_FORMS));
    }

    @Test
    public void testFillForm() throws TaskPermissionsException {
        when(permission.canFillInForm()).thenReturn(Boolean.TRUE);
        victim.ensurePermission(PdfAccessPermission.FILL_FORMS);
    }

    @Test
    public void testNotModify() {
        when(permission.canModify()).thenReturn(Boolean.FALSE);
        assertThrows(TaskPermissionsException.class, () -> victim.ensurePermission(PdfAccessPermission.MODIFY));
    }

    @Test
    public void testModify() throws TaskPermissionsException {
        when(permission.canModify()).thenReturn(Boolean.TRUE);
        victim.ensurePermission(PdfAccessPermission.MODIFY);
    }

    @Test
    public void testNullPermission() {
        assertThrows(IllegalArgumentException.class, () -> victim.ensurePermission(null));
    }
}
