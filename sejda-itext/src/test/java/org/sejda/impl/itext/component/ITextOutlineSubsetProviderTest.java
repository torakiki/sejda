package org.sejda.impl.itext.component;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.sejda.model.exception.TaskException;

import com.lowagie.text.pdf.PdfReader;
import com.lowagie.text.pdf.SimpleBookmark;

/**
 * 
 * @author Andrea Vacondio
 * 
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest(SimpleBookmark.class)
public class ITextOutlineSubsetProviderTest {

    private PdfReader reader;

    @Before
    public void setUp() {
        reader = mock(PdfReader.class);
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
            if (reader != null) {
                reader.close();
            }
        }
    }

    @Test
    public void getOutlineUntillPageEmptyBookmarks() throws TaskException {
        PowerMockito.mockStatic(SimpleBookmark.class);
        PowerMockito.when(SimpleBookmark.getBookmark(reader)).thenReturn(null);
        ITextOutlineSubsetProvider victim = new ITextOutlineSubsetProvider(reader);
        verify(reader).getNumberOfPages();
        victim.startPage(0);
        Collection<Map<String, Object>> retList = victim.getOutlineUntillPage(1);
        assertNotNull(retList);
        assertTrue(retList.isEmpty());
    }

    @Test(expected = TaskException.class)
    public void getOutlineUntillPageNoStart() throws TaskException {
        PowerMockito.mockStatic(SimpleBookmark.class);
        PowerMockito.when(SimpleBookmark.getBookmark(reader)).thenReturn(null);
        ITextOutlineSubsetProvider victim = new ITextOutlineSubsetProvider(reader);
        verify(reader).getNumberOfPages();
        victim.getOutlineUntillPage(1);
    }

    @Test(expected = TaskException.class)
    public void getOutlineUntillPageStartGTEnd() throws TaskException {
        PowerMockito.mockStatic(SimpleBookmark.class);
        PowerMockito.when(SimpleBookmark.getBookmark(reader)).thenReturn(null);
        ITextOutlineSubsetProvider victim = new ITextOutlineSubsetProvider(reader);
        verify(reader).getNumberOfPages();
        victim.startPage(5);
        victim.getOutlineUntillPage(1);
    }
}
