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

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.encryption.AccessPermission;
import org.sejda.core.exception.TaskPermissionsException;
import org.sejda.core.manipulation.model.pdf.encryption.PdfAccessPermission;

/**
 * Wrapper around {@link AccessPermission} providing convenient methods to check permissions.
 * 
 * @author Andrea Vacondio
 * 
 */
public class PDDocumentAccessPermission {

    private AccessPermission permissions;

    PDDocumentAccessPermission(PDDocument document) {
        if (document == null) {
            throw new IllegalArgumentException("Unable to get permissions from null instance.");
        }
        this.permissions = document.getCurrentAccessPermission();
    }

    /**
     * Ensures that owner permissions are available.
     * 
     * @throws TaskPermissionsException
     *             if not owner permissions are granted.
     */
    public void ensureOwnerPermissions() throws TaskPermissionsException {
        if (!permissions.isOwnerPermission()) {
            throw new TaskPermissionsException("Owner permission is required.");
        }
    }

    /**
     * Ensures that the required permission is granted
     * 
     * @param required
     * @throws TaskPermissionsException
     *             if not granted.
     */
    public void ensurePermission(PdfAccessPermission required) throws TaskPermissionsException {
        if (!ForwardingPdfAccessPermission.valueFromPdfAccessPermission(required).isAllowed(permissions)) {
            throw new TaskPermissionsException(String.format("Permission %s is not granted.", required));
        }
    }

    /**
     * enum representing a mapping between a {@link PdfAccessPermission} and it's corresponding method on an {@link AccessPermission} instance.
     * 
     * @author Andrea Vacondio
     * 
     */
    private enum ForwardingPdfAccessPermission {
        MODIFY(PdfAccessPermission.MODIFY) {
            @Override
            boolean isAllowed(AccessPermission permissions) {
                return permissions.canModify();
            }
        },
        ASSEMBLE(PdfAccessPermission.ASSEMBLE) {
            @Override
            boolean isAllowed(AccessPermission permissions) {
                return permissions.canAssembleDocument();
            }
        },
        COPY_AND_EXTRACT(PdfAccessPermission.COPY_AND_EXTRACT) {
            @Override
            boolean isAllowed(AccessPermission permissions) {
                return permissions.canExtractContent();
            }
        },
        DEGRADATED_PRINT(PdfAccessPermission.DEGRADATED_PRINT) {
            @Override
            boolean isAllowed(AccessPermission permissions) {
                return permissions.canPrintDegraded();
            }
        },
        EXTRACTION_FOR_DISABLES(PdfAccessPermission.EXTRACTION_FOR_DISABLES) {
            @Override
            boolean isAllowed(AccessPermission permissions) {
                return permissions.canExtractForAccessibility();
            }
        },
        FILL_FORMS(PdfAccessPermission.FILL_FORMS) {
            @Override
            boolean isAllowed(AccessPermission permissions) {
                return permissions.canFillInForm();
            }
        },
        PRINT(PdfAccessPermission.PRINT) {
            @Override
            boolean isAllowed(AccessPermission permissions) {
                return permissions.canPrint();
            }
        },
        ANNOTATION(PdfAccessPermission.ANNOTATION) {
            @Override
            boolean isAllowed(AccessPermission permissions) {
                return permissions.canModifyAnnotations();
            }
        };

        private PdfAccessPermission permission;

        private ForwardingPdfAccessPermission(PdfAccessPermission permission) {
            this.permission = permission;
        }

        abstract boolean isAllowed(AccessPermission permissions);

        static ForwardingPdfAccessPermission valueFromPdfAccessPermission(PdfAccessPermission accessPermission) {
            for (ForwardingPdfAccessPermission current : ForwardingPdfAccessPermission.values()) {
                if (current.permission == accessPermission) {
                    return current;
                }
            }
            throw new IllegalArgumentException(String.format("No Forwarding access permission found for %s",
                    accessPermission));
        }
    }
}
