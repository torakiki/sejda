/*
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
package org.sejda.core.service;

import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.io.InputStream;

import org.junit.Before;
import org.junit.Test;
import org.sejda.TestUtils;
import org.sejda.core.TestListenerFactory;
import org.sejda.core.TestListenerFactory.TestListenerFailed;
import org.sejda.core.context.DefaultSejdaContext;
import org.sejda.core.context.SejdaContext;
import org.sejda.core.notification.context.ThreadLocalNotificationContext;
import org.sejda.model.exception.TaskException;
import org.sejda.model.input.PdfStreamSource;
import org.sejda.model.output.ExistingOutputPolicy;
import org.sejda.model.parameter.ExtractByOutlineParameters;
import org.sejda.model.pdf.PdfVersion;
import org.sejda.model.task.Task;

public abstract class ExtractByOutlineTaskTest extends PdfOutEnabledTest implements
        TestableTask<ExtractByOutlineParameters> {

    private DefaultTaskExecutionService victim = new DefaultTaskExecutionService();

    private SejdaContext context = mock(DefaultSejdaContext.class);

    @Before
    public void setUp() {
        TestUtils.setProperty(victim, "context", context);
    }

    private ExtractByOutlineParameters setUpParameters(int level, String regEx) {
        ExtractByOutlineParameters parameters = new ExtractByOutlineParameters(level);
        parameters.setMatchingTitleRegEx(regEx);
        parameters.setCompress(true);
        parameters.setVersion(PdfVersion.VERSION_1_6);
        InputStream stream = getClass().getClassLoader().getResourceAsStream("pdf/extract_by_outline_sample.pdf");
        PdfStreamSource source = PdfStreamSource.newInstanceNoPassword(stream, "extract_by_outline_sample.pdf");
        parameters.setSource(source);
        parameters.setOutputPrefix("[FILENUMBER]_[BOOKMARK_NAME_STRICT]");
        parameters.setExistingOutputPolicy(ExistingOutputPolicy.OVERWRITE);
        return parameters;
    }

    @Test
    public void testSplitDeeperLevel() throws TaskException, IOException {
        ExtractByOutlineParameters parameters = setUpParameters(3, null);
        when(context.getTask(parameters)).thenReturn((Task) getTask());
        initializeNewStreamOutput(parameters);
        victim.execute(parameters);
        assertOutputContainsFilenames(
                "1_Creating Assemblies.pdf", "2_Using Profiles.pdf",
                "3_Using Profiles by OS.pdf", "4_Setting Source Code Control System.pdf", "5_Versioning.pdf",
                "6_Using internal Repositories.pdf", "7_Installing Artifact in Remote Repository.pdf",
                "8_Install 3rdParty jar to Remote Repository.pdf", "9_Preparing Releases.pdf",
                "10_Performing Releases.pdf", "11_IntegrationTest with tomcat.pdf",
                "12_Online webdevelopment with Jetty plugin.pdf",
                "13_Online webdevelopment and automatic deployment with tomcat plugin.pdf"
        );
        assertOutputContainsDocuments(13);
    }

    @Test
    public void testSplitAtTopLevel() throws TaskException, IOException {
        ExtractByOutlineParameters parameters = setUpParameters(2, null);
        when(context.getTask(parameters)).thenReturn((Task) getTask());
        initializeNewStreamOutput(parameters);
        victim.execute(parameters);
        assertOutputContainsFilenames(
                "1_Invoking Maven.pdf", "2_Creating a new Project jar.pdf", "3_Creating a new Project war.pdf",
                "4_Standard Project Structure.pdf", "5_Compiling.pdf", "6_Running Unit Tests  Code Coverage.pdf",
                "7_Packaging jar war.pdf", "8_Installing Artifact in Local Repository.pdf",
                "9_Installing 3rdParty jar in local Repository.pdf",
                "10_Cleaning Up.pdf", "11_Creating Eclipse Project Structure.pdf", "12_Maven Project file pomxml.pdf",
                "13_Adding Dependencies.pdf", "14_Adding Developers.pdf", "15_Setting Compiler Version.pdf",
                "16_Assemblies and Profiles.pdf", "17_Versioning Repositories and Releases.pdf",
                "18_WebDevelopment.pdf"

        );
        assertOutputContainsDocuments(18);
        assertNumberOfPages("17_Versioning Repositories and Releases.pdf", 2);
    }

    @Test
    public void testWithMatchingText() throws TaskException, IOException {
        ExtractByOutlineParameters parameters = setUpParameters(3, "(Using)+.+");
        when(context.getTask(parameters)).thenReturn((Task) getTask());
        initializeNewStreamOutput(parameters);
        victim.execute(parameters);
        assertOutputContainsFilenames(
                "1_Using Profiles.pdf", "2_Using Profiles by OS.pdf", "3_Using internal Repositories.pdf"
        );

        assertNumberOfPages("1_Using Profiles.pdf", 2);
        assertNumberOfPages("2_Using Profiles by OS.pdf", 2);
        assertNumberOfPages("3_Using internal Repositories.pdf", 2);
        assertOutputContainsDocuments(3);
    }

    @Test
    public void testNotMatchingregEx() throws TaskException {
        ExtractByOutlineParameters parameters = setUpParameters(1, ".+(Chuck)+.+");
        when(context.getTask(parameters)).thenReturn((Task) getTask());
        initializeNewStreamOutput(parameters);
        TestListenerFailed failListener = TestListenerFactory.newFailedListener();
        ThreadLocalNotificationContext.getContext().addListener(failListener);
        victim.execute(parameters);
        assertTrue(failListener.isFailed());
    }

    @Test
    public void testNonExistingLevel() throws TaskException {
        ExtractByOutlineParameters parameters = setUpParameters(4, null);
        when(context.getTask(parameters)).thenReturn((Task) getTask());
        initializeNewStreamOutput(parameters);
        TestListenerFailed failListener = TestListenerFactory.newFailedListener();
        ThreadLocalNotificationContext.getContext().addListener(failListener);
        victim.execute(parameters);
        assertTrue(failListener.isFailed());
    }

}
