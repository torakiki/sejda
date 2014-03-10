/*
 * Created on 13/set/2011
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
package org.sejda.impl.pdfbox.component;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.encryption.AccessPermission;
import org.junit.Before;
import org.junit.Test;
import org.sejda.core.Sejda;
import org.sejda.model.exception.TaskPermissionsException;
import org.sejda.model.pdf.encryption.PdfAccessPermission;

/**
 * @author Andrea Vacondio
 * 
 */
public class PDDocumentAccessPermissionTest {

    private PDDocumentAccessPermission victim;
    private AccessPermission permission;

    @Before
    public void setUp() {
        PDDocument document = mock(PDDocument.class);
        permission = mock(AccessPermission.class);
        when(document.getCurrentAccessPermission()).thenReturn(permission);
        victim = new PDDocumentAccessPermission(document);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testNull() {
        new PDDocumentAccessPermission(null);
    }

    @Test(expected = TaskPermissionsException.class)
    public void testNotOwner() throws TaskPermissionsException {
        when(permission.isOwnerPermission()).thenReturn(Boolean.FALSE);
        victim.ensureOwnerPermissions();
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

    @Test(expected = TaskPermissionsException.class)
    public void testNotPrint() throws TaskPermissionsException {
        when(permission.canPrint()).thenReturn(Boolean.FALSE);
        victim.ensurePermission(PdfAccessPermission.PRINT);
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

    @Test(expected = TaskPermissionsException.class)
    public void testNotDegradedPrint() throws TaskPermissionsException {
        when(permission.canPrintDegraded()).thenReturn(Boolean.FALSE);
        victim.ensurePermission(PdfAccessPermission.DEGRADATED_PRINT);
    }

    @Test
    public void testDegradedPrint() throws TaskPermissionsException {
        when(permission.canPrintDegraded()).thenReturn(Boolean.TRUE);
        victim.ensurePermission(PdfAccessPermission.DEGRADATED_PRINT);
    }

    @Test(expected = TaskPermissionsException.class)
    public void testNotAssemble() throws TaskPermissionsException {
        when(permission.canAssembleDocument()).thenReturn(Boolean.FALSE);
        victim.ensurePermission(PdfAccessPermission.ASSEMBLE);
    }

    @Test
    public void testAssemble() throws TaskPermissionsException {
        when(permission.canAssembleDocument()).thenReturn(Boolean.TRUE);
        victim.ensurePermission(PdfAccessPermission.ASSEMBLE);
    }

    @Test(expected = TaskPermissionsException.class)
    public void testNotAannotation() throws TaskPermissionsException {
        when(permission.canModifyAnnotations()).thenReturn(Boolean.FALSE);
        victim.ensurePermission(PdfAccessPermission.ANNOTATION);
    }

    @Test
    public void testAnnotation() throws TaskPermissionsException {
        when(permission.canModifyAnnotations()).thenReturn(Boolean.TRUE);
        victim.ensurePermission(PdfAccessPermission.ANNOTATION);
    }

    @Test(expected = TaskPermissionsException.class)
    public void testNotCopy() throws TaskPermissionsException {
        when(permission.canExtractContent()).thenReturn(Boolean.FALSE);
        victim.ensurePermission(PdfAccessPermission.COPY_AND_EXTRACT);
    }

    @Test
    public void testCopy() throws TaskPermissionsException {
        when(permission.canExtractContent()).thenReturn(Boolean.TRUE);
        victim.ensurePermission(PdfAccessPermission.COPY_AND_EXTRACT);
    }

    @Test(expected = TaskPermissionsException.class)
    public void testNotExtract() throws TaskPermissionsException {
        when(permission.canExtractForAccessibility()).thenReturn(Boolean.FALSE);
        victim.ensurePermission(PdfAccessPermission.EXTRACTION_FOR_DISABLES);
    }

    @Test
    public void testExtract() throws TaskPermissionsException {
        when(permission.canExtractForAccessibility()).thenReturn(Boolean.TRUE);
        victim.ensurePermission(PdfAccessPermission.EXTRACTION_FOR_DISABLES);
    }

    @Test(expected = TaskPermissionsException.class)
    public void testNotFillForm() throws TaskPermissionsException {
        when(permission.canFillInForm()).thenReturn(Boolean.FALSE);
        victim.ensurePermission(PdfAccessPermission.FILL_FORMS);
    }

    @Test
    public void testFillForm() throws TaskPermissionsException {
        when(permission.canFillInForm()).thenReturn(Boolean.TRUE);
        victim.ensurePermission(PdfAccessPermission.FILL_FORMS);
    }

    @Test(expected = TaskPermissionsException.class)
    public void testNotModify() throws TaskPermissionsException {
        when(permission.canModify()).thenReturn(Boolean.FALSE);
        victim.ensurePermission(PdfAccessPermission.MODIFY);
    }

    @Test
    public void testModify() throws TaskPermissionsException {
        when(permission.canModify()).thenReturn(Boolean.TRUE);
        victim.ensurePermission(PdfAccessPermission.MODIFY);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testNullPermission() throws TaskPermissionsException {
        victim.ensurePermission(null);
    }
}
