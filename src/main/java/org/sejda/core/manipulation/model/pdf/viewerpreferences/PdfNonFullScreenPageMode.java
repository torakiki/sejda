/*
 * Created on 20/set/2010
 * Copyright (C) 2010 by Andrea Vacondio (andrea.vacondio@gmail.com).
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License.
 * 
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 */
package org.sejda.core.manipulation.model.pdf.viewerpreferences;

/**
 * Possible values for the document’s page mode, specifying how to display the document on exiting full-screen mode.<br>
 * According to the reference: <i>This entry is meaningful only if the value of the PageMode entry in the catalog dictionary (see Section 3.6.1, “Document Catalog”) is FullScreen;
 * it is ignored otherwise. Default value: UseNone.</i><br>
 * Pdf reference 1.7, TABLE 8.1 Entries in a viewer preferences dictionary
 * 
 * @author Andrea Vacondio
 * 
 */
public enum PdfNonFullScreenPageMode {
    USE_NONE, USE_OUTLINES, USE_THUMNS, USE_OC;
}
