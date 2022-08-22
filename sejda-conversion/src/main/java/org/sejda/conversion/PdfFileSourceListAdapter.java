/*
 * Created on Sep 3, 2011
 * Copyright 2010 by Eduard Weissmann (edi.weissmann@gmail.com).
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
package org.sejda.conversion;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.sejda.commons.util.NumericalSortFilenameComparator;
import org.sejda.conversion.exception.ConversionException;
import org.sejda.model.exception.SejdaRuntimeException;
import org.sejda.model.input.PdfFileSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathException;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;

import static java.util.stream.Collectors.toList;
import static org.apache.commons.io.FilenameUtils.getExtension;

/**
 * Adapter for a list of {@link PdfFileSource}s. Provides a filePath based constructor. Will parse xml, csv config file formats, and list a directory contents
 * 
 * @author Eduard Weissmann
 * 
 */
public class PdfFileSourceListAdapter {

    private static final Logger LOG = LoggerFactory.getLogger(PdfFileSourceListAdapter.class);

    private final PdfInputFilesSourceFactory parserFactory = new PdfInputFilesSourceFactory();
    private final List<PdfFileSource> fileSourceList = new ArrayList<>();
    private final File file;
    private Pattern pattern = Pattern.compile(".+");

    public PdfFileSourceListAdapter(String filePath) {
        file = new File(filePath);

        if (!file.exists()) {
            throw new ConversionException("File '" + file.getPath() + "' does not exist");
        }
    }

    public PdfFileSourceListAdapter filter(String filterRegExp) {
        if (StringUtils.isNotBlank(filterRegExp)) {
            LOG.debug("Applying regular expression: {}", filterRegExp);
            pattern = Pattern.compile(filterRegExp);
        }
        return this;
    }

    public List<PdfFileSource> getFileSourceList() {
        fileSourceList.addAll(parserFactory.createSource(file).getInputFiles(file));

        if (fileSourceList.isEmpty()) {
            throw new ConversionException("No input files specified in '" + file.getPath() + "'");
        }
        return fileSourceList;
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
            String extension = getExtension(file.getName());

            if (file.isDirectory()) {
                return new FolderFileSourceListParser(PdfFileSourceListAdapter.this.pattern);
            } else if (CSV_EXTENSION.equalsIgnoreCase(extension)) {
                return new CsvFileSourceListParser();
            } else if (XML_EXTENSION.equalsIgnoreCase(extension)) {
                return new XmlFileSourceListParser();
            }

            throw new SejdaRuntimeException("Cannot read input file names from config file '" + file.getName()
                    + "'. Unsupported file format: " + extension);
        }
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
    private static final Logger LOG = LoggerFactory.getLogger(AbstractPdfInputFilesSource.class);

    @Override
    public List<PdfFileSource> getInputFiles(File file) {
        List<String> filenames = parseFileNames(file);
        LOG.trace("Input files: '" + StringUtils.join(filenames, "', '") + "'");
        try {
            return PdfFileSourceAdapter.fromStrings(filenames);
        } catch (SejdaRuntimeException e) {
            throw new ConversionException("Invalid filename found: " + e.getMessage(), e);
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

    private Pattern pattern;

    FolderFileSourceListParser(Pattern pattern) {
        this.pattern = pattern;
    }

    @Override
    protected List<String> parseFileNames(File file) {

        return Arrays.asList(file.listFiles((dir, filename) -> {
            return StringUtils.equalsIgnoreCase(getExtension(filename), PDF_EXTENSION)
                    && pattern.matcher(filename).matches();
        })).stream().sorted(new NumericalSortFilenameComparator()).map(File::getAbsolutePath).collect(toList());
    }

}

/**
 * Produces the list of input files by parsing a csv file
 * 
 * @author Eduard Weissmann
 * 
 */
class CsvFileSourceListParser extends AbstractPdfInputFilesSource {
    private static final Logger LOG = LoggerFactory.getLogger(CsvFileSourceListParser.class);

    @Override
    protected List<String> parseFileNames(File file) {
        try {
            return doParseFileNames(file);
        } catch (Exception e) {
            LOG.error("Can't extract filesnames", e);
            throw new ConversionException(
                    "Can't extract filenames from '" + file.getName() + "'. Reason:" + e.getMessage(), e);
        }
    }

    protected List<String> doParseFileNames(File file) throws IOException {
        List<String> resultingFileNames = new ArrayList<>();

        List<String> lines = IOUtils.readLines(new FileInputStream(file), Charset.defaultCharset());
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
    private static final Logger LOG = LoggerFactory.getLogger(XmlFileSourceListParser.class);

    private XPathFactory xpathFactory = XPathFactory.newInstance();

    @Override
    protected List<String> parseFileNames(File file) {
        try {
            return doParseFileNames(file);
        } catch (Exception e) {
            LOG.error("Can't extract filenames", e);
            throw new ConversionException(
                    "Can't extract filenames from '" + file.getName() + "'. Reason:" + e.getMessage(), e);
        }
    }

    protected List<String> doParseFileNames(File file)
            throws IOException, SAXException, ParserConfigurationException, XPathException {
        DocumentBuilderFactory domFactory = DocumentBuilderFactory.newInstance();
        domFactory.setNamespaceAware(true);
        DocumentBuilder builder = domFactory.newDocumentBuilder();
        Document doc = builder.parse(file);

        List<String> result = new ArrayList<>();

        result.addAll(parseSingleFiles(doc));
        result.addAll(parseFileSets(doc, file));

        return result;
    }

    /**
     * Parse fileset definitions <filelist><fileset>[...]</fileset></filelist> ignoring the rest of the document
     * 
     * @param doc
     * @return a list of string matching the contents of the <filelist><fileset> tags in the document
     * @throws XPathExpressionException
     */
    private List<String> parseFileSets(Document doc, File configFile) throws XPathExpressionException {
        List<String> result = new ArrayList<>();

        NodeList nodeList = getNodeListMatchingXpath("//filelist/fileset/file", doc);
        for (int i = 0; i < nodeList.getLength(); i++) {
            Node node = nodeList.item(i);
            Node fileSet = node.getParentNode();

            String parentDirPath = nullSafeGetStringAttribute(fileSet, "dir");
            if (parentDirPath == null) {
                parentDirPath = configFile.getAbsoluteFile().getParent();
            }

            String filePath = extractFilePath(node);

            // warn if file in fileset is using absolute path mode
            if (FilenameUtils.getPrefixLength(filePath) > 0) {
                LOG.warn("File " + filePath + " in fileset "
                        + StringUtils.defaultIfBlank(nullSafeGetStringAttribute(fileSet, "dir"), "")
                        + " seems to be an absolute path. Will _not_ be resolved relative to the <fileset>, but as an absolute path. Normally you would want to use relative paths in a //filelist/fileset/file, and absolute paths in a //filelist/file.");
            }

            result.add(FilenameUtils.concat(parentDirPath, filePath));
        }

        return result;
    }

    private String extractFilePath(Node fileNode) {
        String password = nullSafeGetStringAttribute(fileNode, "password");
        String value = nullSafeGetStringAttribute(fileNode, "value");
        return value + (password == null ? "" : PdfFileSourceAdapter.PASSWORD_SEPARATOR_CHARACTER + password);
    }

    /**
     * Parse single file definitions <filelist><file>[...]</file></filelist> ignoring the rest of the document
     * 
     * @param doc
     * @return a list of string matching the contents of the <filelist><file> tags in the document
     * @throws XPathExpressionException
     */
    private List<String> parseSingleFiles(Document doc) throws XPathExpressionException {
        List<String> result = new ArrayList<>();

        NodeList nodeList = getNodeListMatchingXpath("//filelist/file", doc);
        for (int i = 0; i < nodeList.getLength(); i++) {
            result.add(extractFilePath(nodeList.item(i)));
        }

        return result;
    }

    private NodeList getNodeListMatchingXpath(String xpathString, Document doc) throws XPathExpressionException {
        return (NodeList) xpathFactory.newXPath().evaluate(xpathString, doc, XPathConstants.NODESET);
    }

    private static String nullSafeGetStringAttribute(Node node, String attributeName) {
        return Optional.ofNullable(node).map(Node::getAttributes).map(m -> m.getNamedItem(attributeName))
                .map(Node::getNodeValue).orElse(null);
    }
}
