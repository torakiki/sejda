/*
 * Created on 19 ott 2016
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
package org.sejda.conversion;

import static org.apache.commons.io.FilenameUtils.getFullPath;
import static org.apache.commons.io.FilenameUtils.getName;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import org.sejda.commons.util.NumericalSortFilenameComparator;
import org.sejda.conversion.exception.ConversionException;
import org.sejda.model.input.PdfFileSource;

/**
 * Adapter accepting a file path or wildecards
 * 
 * @author Andrea Vacondio
 *
 */
public class WildcardsPdfFileSourceAdapter {

    private final List<PdfFileSource> sources = new ArrayList<>();

    public WildcardsPdfFileSourceAdapter(String path) {
        if (getName(path).equalsIgnoreCase("*.pdf")) {
            Path directory = Paths.get(getFullPath(path)).toAbsolutePath().normalize();

            if (!Files.isDirectory(directory)) {
                throw new ConversionException("Path '" + directory.toString() + "' is not an existing directory");
            }

            try (DirectoryStream<Path> dirStream = Files.newDirectoryStream(directory, "*.pdf")) {
                dirStream.forEach(p -> sources.add(PdfFileSource.newInstanceNoPassword(p.toFile())));
            } catch (IOException e) {
                throw new ConversionException("Unable to list PDF files in " + path, e);
            }
        } else {
            sources.add(new PdfFileSourceAdapter(path).getPdfFileSource());
        }
        sources.sort(Comparator.comparing(PdfFileSource::getSource, new NumericalSortFilenameComparator()));

    }

    /**
     * @return the pdfFileSources
     */
    public List<PdfFileSource> getPdfFileSources() {
        return sources;
    }
}
