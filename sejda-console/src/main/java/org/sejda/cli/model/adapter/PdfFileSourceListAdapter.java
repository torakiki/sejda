/*
 * Created on Sep 3, 2011
 * Copyright 2010 by Eduard Weissmann (edi.weissmann@gmail.com).
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
package org.sejda.cli.model.adapter;

import java.io.File;
import java.io.FileInputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathException;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathFactory;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Transformer;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.sejda.cli.exception.ArgumentValidationException;
import org.sejda.core.exception.SejdaRuntimeException;
import org.sejda.core.manipulation.model.input.PdfFileSource;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 * 
 * @author Eduard Weissmann
 * 
 */
public class PdfFileSourceListAdapter {

    private final PdfInputFilesSourceFactory parserFactory = new PdfInputFilesSourceFactory();
    private final List<PdfFileSource> fileSourceList = new ArrayList<PdfFileSource>();

    public PdfFileSourceListAdapter(String filePath) {
        File file = new File(filePath);

        if (!file.exists()) {
            throw new ArgumentValidationException("File '" + file.getPath() + "' does not exist");
        }

        fileSourceList.addAll(parserFactory.createSource(file).getInputFiles(file));

        if (fileSourceList.isEmpty()) {
            throw new ArgumentValidationException("No input files specified in '" + filePath + "'");
        }
    }

    public List<PdfFileSource> getFileSourceList() {
        return fileSourceList;
    }
}

/**
 * Factory for {@link PdfInputFilesSource}s. Depending on input {@link File} (folder, csv file, xml file), a different source will be created.
 * 
 * @author Eduard Weissmann
 * 
 */
class PdfInputFilesSourceFactory {
    private static final String XML_EXTENSION = "xml";
    private static final String CSV_EXTENSION = "csv";

    PdfInputFilesSource createSource(File file) {
        String extension = FilenameUtils.getExtension(file.getName());

        if (file.isDirectory()) {
            return new FolderFileSourceListParser();
        } else if (CSV_EXTENSION.equalsIgnoreCase(extension)) {
            return new CsvFileSourceListParser();
        } else if (XML_EXTENSION.equalsIgnoreCase(extension)) {
            return new XmlFileSourceListParser();
        }

        throw new SejdaRuntimeException("Cannot read input file names from config file '" + file.getName()
                + "'. Unsupported file format: " + extension);
    }
}

/**
 * Source for {@link PdfFileSource} input files
 * 
 * @author Eduard Weissmann
 * 
 */
interface PdfInputFilesSource {

    List<PdfFileSource> getInputFiles(File file);
}

/**
 * Abstract base class of {@link PdfInputFilesSource}s
 * 
 * @author Eduard Weissmann
 * 
 */
abstract class AbstractPdfInputFilesSource implements PdfInputFilesSource {

    public List<PdfFileSource> getInputFiles(File file) {
        List<String> filenames = parseFileNames(file);
        try {
            return PdfFileSourceAdapter.fromStrings(filenames);
        } catch (SejdaRuntimeException e) {
            throw new ArgumentValidationException("Invalid filename found: " + e.getMessage(), e);
        }
    }

    protected abstract List<String> parseFileNames(File file);
}

/**
 * Produces the list of input files by listing a directory for files with pdf extension
 * 
 * @author Eduard Weissmann
 * 
 */
class FolderFileSourceListParser extends AbstractPdfInputFilesSource {

    protected static final String PDF_EXTENSION = "pdf";

    @Override
    protected List<String> parseFileNames(File file) {
        List<File> files = Arrays.asList(file.listFiles(new FilenameFilter() {

            public boolean accept(File dir, String filename) {
                String extension = FilenameUtils.getExtension(filename);
                return StringUtils.equalsIgnoreCase(extension, PDF_EXTENSION);
            }

        }));

        List<String> filenames = new ArrayList<String>();
        CollectionUtils.collect(files, new Transformer() {

            public Object transform(Object input) {
                return ((File) input).getAbsolutePath();
            }

        }, filenames);

        Collections.sort(filenames);

        return filenames;
    }

}

/**
 * Produces the list of input files by parsing a csv file
 * 
 * @author Eduard Weissmann
 * 
 */
class CsvFileSourceListParser extends AbstractPdfInputFilesSource {

    @Override
    protected List<String> parseFileNames(File file) {
        try {
            return doParseFileNames(file);
        } catch (Exception e) {
            throw new ArgumentValidationException("Can't extract filenames from '" + file.getName() + "'. Reason:"
                    + e.getMessage(), e);
        }
    }

    protected List<String> doParseFileNames(File file) throws IOException {
        List<String> resultingFileNames = new ArrayList<String>();

        List<String> lines = IOUtils.readLines(new FileInputStream(file));
        for (String eachLine : lines) {
            String[] splitLine = StringUtils.split(eachLine.toString(), ",");
            resultingFileNames.addAll(Arrays.asList(splitLine));
        }

        return resultingFileNames;
    }
}

/**
 * Produces the list of input files by parsing a xml file
 * 
 * @author Eduard Weissmann
 * 
 */
class XmlFileSourceListParser extends AbstractPdfInputFilesSource {

    @Override
    protected List<String> parseFileNames(File file) {
        try {
            return doParseFileNames(file);
        } catch (Exception e) {
            throw new ArgumentValidationException("Can't extract filenames from '" + file.getName() + "'. Reason:"
                    + e.getMessage(), e);
        }
    }

    protected List<String> doParseFileNames(File file) throws IOException, SAXException, ParserConfigurationException,
            XPathException {
        DocumentBuilderFactory domFactory = DocumentBuilderFactory.newInstance();
        domFactory.setNamespaceAware(true);
        DocumentBuilder builder = domFactory.newDocumentBuilder();
        Document doc = builder.parse(file);

        XPathFactory factory = XPathFactory.newInstance();
        XPath xpath = factory.newXPath();
        XPathExpression expr = xpath.compile("//filelist/file/@value");

        List<String> result = new ArrayList<String>();
        NodeList nodeList = (NodeList) expr.evaluate(doc, XPathConstants.NODESET);
        for (int i = 0; i < nodeList.getLength(); i++) {
            result.add(nodeList.item(i).getNodeValue());
        }

        return result;
    }
}
