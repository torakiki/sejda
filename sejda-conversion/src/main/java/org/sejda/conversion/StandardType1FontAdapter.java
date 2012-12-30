/*
 * Created on 29/dic/2012
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
package org.sejda.conversion;

import org.sejda.model.pdf.StandardType1Font;

/**
 * Adapter for a {@link StandardType1Font} enum.
 * 
 * @author Andrea Vacondio
 * 
 */
public class StandardType1FontAdapter extends EnumAdapter<StandardType1Font> {

    public StandardType1FontAdapter(String userFriendlyName) {
        super(userFriendlyName, StandardType1Font.class, "font type");
    }

}
