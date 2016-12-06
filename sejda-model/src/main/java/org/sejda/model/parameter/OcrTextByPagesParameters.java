/*
 * Created on 06 dic 2016
 * Copyright 2015 by Andrea Vacondio (andrea.vacondio@gmail.com).
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
package org.sejda.model.parameter;

import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

/**
 * Parameter class to extract text by pages performing OCR
 * 
 * @author Andrea Vacondio
 *
 */
public class OcrTextByPagesParameters extends ExtractTextByPagesParameters {

    public final Set<Locale> languages = new HashSet<>();

    /**
     * Adds a language o the list of possible languages of the text found in the document. This can help the OCR engine to return a more accurate result.
     * 
     * @param language
     */
    public void addLanguage(Locale language) {
        this.languages.add(language);
    }

    /**
     * @return Languages that can be fed to the OCR engine to return a more accurate result
     */
    public Set<Locale> getLanguages() {
        return languages;
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder().appendSuper(super.hashCode()).append(languages).toHashCode();
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof OcrTextByPagesParameters)) {
            return false;
        }
        OcrTextByPagesParameters parameter = (OcrTextByPagesParameters) other;
        return new EqualsBuilder().appendSuper(super.equals(other)).append(languages, parameter.languages).isEquals();
    }
}
