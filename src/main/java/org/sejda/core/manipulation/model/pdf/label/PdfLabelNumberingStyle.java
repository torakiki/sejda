/*
 * Created on 02/gen/2011
 * Copyright 2010 by Andrea Vacondio (andrea.vacondio@gmail.com).
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
package org.sejda.core.manipulation.model.pdf.label;

/**
 * Possible values for a numbering style to be used for page labels.<br>
 * Pdf reference 1.7, TABLE 8.10 Entries in a page label dictionary
 * 
 * @author Andrea Vacondio
 * 
 */
public enum PdfLabelNumberingStyle {
    ARABIC,
    UPPERCASE_ROMANS,
    LOWERCASE_ROMANS,
    UPPERCASE_LETTERS,
    LOWERCASE_LETTERS,
    EMPTY;
}
