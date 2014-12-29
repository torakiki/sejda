package org.sejda.impl.itext.component;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.junit.After;
import org.junit.Test;
import org.sejda.impl.itext.util.ITextUtils;
import org.sejda.model.exception.TaskException;

import com.lowagie.text.pdf.PdfReader;

/**
 * 
 * @author Andrea Vacondio
 * 
 */
public class ITextOutlineSubsetProviderTest {

    private PdfReader reader;

    @After
    public void tearDown() {
        ITextUtils.nullSafeClosePdfReader(reader);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testFailingConstructor() {
        new ITextOutlineSubsetProvider(null);
    }

    @Test
    public void getOutlineUntillPage() throws IOException, TaskException {
        InputStream inputStream = null;
        try {
            inputStream = getClass().getClassLoader().getResourceAsStream("pdf/test_outline.pdf");
            reader = new PdfReader(inputStream);
            ITextOutlineSubsetProvider victim = new ITextOutlineSubsetProvider(reader);
            victim.startPage(2);
            Collection<Map<String, Object>> retList = victim.getOutlineUntillPage(2);
            assertNotNull(retList);
            assertFalse(retList.isEmpty());
        } finally {
            IOUtils.closeQuietly(inputStream);
        }
    }

    @Test
    public void getOutlineUntillPageEmptyBookmarks() throws TaskException, IOException {
        InputStream inputStream = null;
        try {
            inputStream = getClass().getClassLoader().getResourceAsStream("pdf/test_no_outline.pdf");
            reader = spy(new PdfReader(inputStream));
            ITextOutlineSubsetProvider victim = new ITextOutlineSubsetProvider(reader);
            verify(reader).getNumberOfPages();
            victim.startPage(0);
            Collection<Map<String, Object>> retList = victim.getOutlineUntillPage(1);
            assertNotNull(retList);
            assertTrue(retList.isEmpty());
        } finally {
            IOUtils.closeQuietly(inputStream);
        }
    }

    @Test(expected = TaskException.class)
    public void getOutlineUntillPageNoStart() throws TaskException, IOException {
        InputStream inputStream = null;
        try {
            inputStream = getClass().getClassLoader().getResourceAsStream("pdf/test_no_outline.pdf");
            reader = spy(new PdfReader(inputStream));
            ITextOutlineSubsetProvider victim = new ITextOutlineSubsetProvider(reader);
            verify(reader).getNumberOfPages();
            victim.getOutlineUntillPage(1);
        } finally {
            IOUtils.closeQuietly(inputStream);
        }
    }

    @Test(expected = TaskException.class)
    public void getOutlineUntillPageStartGTEnd() throws TaskException, IOException {
        InputStream inputStream = null;
        try {
            inputStream = getClass().getClassLoader().getResourceAsStream("pdf/test_no_outline.pdf");
            reader = spy(new PdfReader(inputStream));
            ITextOutlineSubsetProvider victim = new ITextOutlineSubsetProvider(reader);
            verify(reader).getNumberOfPages();
            victim.startPage(5);
            victim.getOutlineUntillPage(1);
            victim.getOutlineUntillPage(1);
        } finally {
            IOUtils.closeQuietly(inputStream);
        }
    }
}
