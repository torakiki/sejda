package org.sejda.core.manipulation.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.io.InputStream;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.sejda.core.TestUtils;
import org.sejda.core.exception.TaskException;
import org.sejda.core.manipulation.model.input.PdfStreamSource;
import org.sejda.core.manipulation.model.parameter.SetPagesTransitionParameters;
import org.sejda.core.manipulation.model.pdf.PdfVersion;
import org.sejda.core.manipulation.model.pdf.transition.PdfPageTransition;
import org.sejda.core.manipulation.model.pdf.transition.PdfPageTransitionStyle;
import org.sejda.core.manipulation.model.task.Task;

import com.itextpdf.text.pdf.PdfDictionary;
import com.itextpdf.text.pdf.PdfName;
import com.itextpdf.text.pdf.PdfReader;

@Ignore
public abstract class SetPagesTransitionsTaskTest extends PdfOutEnabledTest implements
        TestableTask<SetPagesTransitionParameters> {
    private DefaultTaskExecutionService victim = new DefaultTaskExecutionService();

    private TaskExecutionContext context = mock(DefaultTaskExecutionContext.class);
    private SetPagesTransitionParameters parameters;

    @Before
    public void setUp() {
        setUpParameters();
        TestUtils.setProperty(victim, "context", context);
    }

    /**
     * Set up of the set page labels parameters
     * 
     */
    private void setUpParameters() {
        parameters = new SetPagesTransitionParameters();
        parameters.setCompress(true);
        parameters.setVersion(PdfVersion.VERSION_1_6);
        parameters.putTransition(1, PdfPageTransition.newInstance(PdfPageTransitionStyle.BOX_OUTWARD, 1, 5));
        InputStream stream = getClass().getClassLoader().getResourceAsStream("pdf/test_file.pdf");
        PdfStreamSource source = PdfStreamSource.newInstanceNoPassword(stream, "test_file.pdf");
        parameters.setSource(source);
        parameters.setOverwrite(true);
    }

    @Test
    public void testExecute() throws TaskException, IOException {
        when(context.getTask(parameters)).thenReturn((Task) getTask());
        initializeNewFileOutput(parameters);
        victim.execute(parameters);
        PdfReader reader = getReaderFromResultFile();
        assertCreator(reader);
        assertEquals(4, reader.getNumberOfPages());
        PdfDictionary dictionary = reader.getPageN(1).getAsDict(PdfName.TRANS);
        assertEquals(PdfName.BOX, dictionary.get(PdfName.S));
        assertEquals(PdfName.O, dictionary.get(PdfName.M));
        assertNull(reader.getPageN(2).getAsDict(PdfName.TRANS));
        assertNull(reader.getPageN(3).getAsDict(PdfName.TRANS));
        assertNull(reader.getPageN(4).getAsDict(PdfName.TRANS));
        reader.close();
    }

    @Test
    public void testExecuteDefault() throws TaskException, IOException {
        TestUtils.setProperty(parameters, "defaultTransition",
                PdfPageTransition.newInstance(PdfPageTransitionStyle.SPLIT_HORIZONTAL_INWARD, 1, 5));
        when(context.getTask(parameters)).thenReturn((Task) getTask());
        initializeNewFileOutput(parameters);
        victim.execute(parameters);
        PdfReader reader = getReaderFromResultFile();
        assertCreator(reader);
        assertEquals(4, reader.getNumberOfPages());
        PdfDictionary dictionaryFirst = reader.getPageN(1).getAsDict(PdfName.TRANS);
        assertEquals(PdfName.BOX, dictionaryFirst.get(PdfName.S));
        assertEquals(PdfName.O, dictionaryFirst.get(PdfName.M));

        for (int i = 2; i < 5; i++) {
            PdfDictionary dic = reader.getPageN(i).getAsDict(PdfName.TRANS);
            assertEquals(PdfName.SPLIT, dic.get(PdfName.S));
            assertEquals(PdfName.H, dic.get(PdfName.DM));
            assertEquals(PdfName.I, dic.get(PdfName.M));
        }
        reader.close();
    }

    protected SetPagesTransitionParameters getParameters() {
        return parameters;
    }
}
