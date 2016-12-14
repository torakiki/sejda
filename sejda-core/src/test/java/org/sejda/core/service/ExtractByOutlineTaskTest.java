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

import java.io.IOException;

import org.junit.Ignore;
import org.junit.Test;
import org.sejda.core.TestListenerFactory;
import org.sejda.core.TestListenerFactory.TestListenerFailed;
import org.sejda.core.notification.context.ThreadLocalNotificationContext;
import org.sejda.model.optimization.OptimizationPolicy;
import org.sejda.model.output.ExistingOutputPolicy;
import org.sejda.model.parameter.ExtractByOutlineParameters;
import org.sejda.model.pdf.PdfVersion;

@Ignore
public abstract class ExtractByOutlineTaskTest extends BaseTaskTest<ExtractByOutlineParameters> {

    private ExtractByOutlineParameters setUpParameters(int level) {
        return setUpParameters(level, "pdf/extract_by_outline_sample.pdf", null);
    }

    private ExtractByOutlineParameters setUpParameters(int level, String regEx) {
        return setUpParameters(level, "pdf/extract_by_outline_sample.pdf", regEx);
    }

    private ExtractByOutlineParameters setUpParameters(int level, String sourceFile, String regEx) {
        ExtractByOutlineParameters parameters = new ExtractByOutlineParameters(level);
        parameters.setMatchingTitleRegEx(regEx);
        parameters.setCompress(true);
        parameters.setVersion(PdfVersion.VERSION_1_6);
        parameters.addSource(customInput(sourceFile, "file1.pdf"));
        parameters.setOutputPrefix("[FILENUMBER]_[BOOKMARK_NAME_STRICT]");
        parameters.setExistingOutputPolicy(ExistingOutputPolicy.OVERWRITE);
        return parameters;
    }

    @Test
    public void testSplitDeeperLevel() throws IOException {
        ExtractByOutlineParameters parameters = setUpParameters(3);
        testContext.directoryOutputTo(parameters);
        execute(parameters);
        testContext
                .assertOutputContainsFilenames("1_Creating Assemblies.pdf", "2_Using Profiles.pdf",
                        "3_Using Profiles by OS.pdf", "4_Setting Source Code Control System.pdf", "5_Versioning.pdf",
                        "6_Using internal Repositories.pdf", "7_Installing Artifact in Remote Repository.pdf",
                        "8_Install 3rdParty jar to Remote Repository.pdf", "9_Preparing Releases.pdf",
                        "10_Performing Releases.pdf", "11_IntegrationTest with tomcat.pdf",
                        "12_Online webdevelopment with Jetty plugin.pdf",
                        "13_Online webdevelopment and automatic deployment with tomcat plugin.pdf")
                .assertOutputSize(13);
    }

    @Test
    public void testSplitAtTopLevel() throws IOException {
        ExtractByOutlineParameters parameters = setUpParameters(2);
        parameters.setIncludePageAfter(true);
        testContext.directoryOutputTo(parameters);
        execute(parameters);
        testContext.assertOutputContainsFilenames("1_Invoking Maven.pdf", "2_Creating a new Project jar.pdf",
                "3_Creating a new Project war.pdf", "4_Standard Project Structure.pdf", "5_Compiling.pdf",
                "6_Running Unit Tests  Code Coverage.pdf", "7_Packaging jar war.pdf",
                "8_Installing Artifact in Local Repository.pdf", "9_Installing 3rdParty jar in local Repository.pdf",
                "10_Cleaning Up.pdf", "11_Creating Eclipse Project Structure.pdf", "12_Maven Project file pomxml.pdf",
                "13_Adding Dependencies.pdf", "14_Adding Developers.pdf", "15_Setting Compiler Version.pdf",
                "16_Assemblies and Profiles.pdf", "17_Versioning Repositories and Releases.pdf", "18_WebDevelopment.pdf"

        ).assertOutputSize(18).assertPages("17_Versioning Repositories and Releases.pdf", 2);
    }

    @Test
    public void testBatchFilesWithConflictingOutputFiles() throws IOException {
        ExtractByOutlineParameters parameters = setUpParameters(2);
        parameters.addSource(customInput("pdf/extract_by_outline_sample.pdf", "file2.pdf"));
        parameters.setOutputPrefix("[BASENAME]_[FILENUMBER]_[BOOKMARK_NAME_STRICT]");
        testContext.directoryOutputTo(parameters);
        execute(parameters);
        testContext.assertOutputContainsFilenames("file1_1_Invoking Maven.pdf", "file2_1_Invoking Maven.pdf");
        testContext.assertOutputSize(36);
    }

    @Test
    public void testWithMatchingText() throws IOException {
        ExtractByOutlineParameters parameters = setUpParameters(3, "(Using)+.+");
        parameters.setIncludePageAfter(true);
        testContext.directoryOutputTo(parameters);
        execute(parameters);
        testContext.assertOutputSize(3).assertPages("1_Using Profiles.pdf", 2)
                .assertPages("2_Using Profiles by OS.pdf", 2).assertPages("3_Using internal Repositories.pdf", 2);
    }

    @Test
    public void testWithMatchingTextAndOptimization() throws IOException {
        ExtractByOutlineParameters parameters = setUpParameters(3, "(Using)+.+");
        parameters.setOptimizationPolicy(OptimizationPolicy.YES);
        parameters.setIncludePageAfter(true);
        testContext.directoryOutputTo(parameters);
        execute(parameters);
        testContext.assertOutputSize(3).assertPages("1_Using Profiles.pdf", 2)
                .assertPages("2_Using Profiles by OS.pdf", 2).assertPages("3_Using internal Repositories.pdf", 2);
    }

    @Test
    public void testNotMatchingregEx() throws IOException {
        ExtractByOutlineParameters parameters = setUpParameters(1, ".+(Chuck)+.+");
        testContext.directoryOutputTo(parameters);
        TestListenerFailed failListener = TestListenerFactory.newFailedListener();
        ThreadLocalNotificationContext.getContext().addListener(failListener);
        execute(parameters);
        assertTrue(failListener.isFailed());
    }

    @Test
    public void testNonExistingLevel() throws IOException {
        ExtractByOutlineParameters parameters = setUpParameters(4);
        testContext.directoryOutputTo(parameters);
        TestListenerFailed failListener = TestListenerFactory.newFailedListener();
        ThreadLocalNotificationContext.getContext().addListener(failListener);
        execute(parameters);
        assertTrue(failListener.isFailed());
    }

    @Test
    public void testIncludingPageAfterOff() throws IOException {
        ExtractByOutlineParameters parameters = setUpParameters(1, "pdf/payslip_with_bookmarks.pdf", null);
        testContext.directoryOutputTo(parameters);
        execute(parameters);
        testContext.assertOutputContainsFilenames("1_Employee One.pdf", "3_Employee Three.pdf", "2_Employee Two.pdf");
        testContext.assertOutputSize(3);
        testContext.assertPages("1_Employee One.pdf", 1);
    }

    @Test
    public void testIncludingPageAfterOn() throws IOException {
        ExtractByOutlineParameters parameters = setUpParameters(1, "pdf/payslip_with_bookmarks.pdf", null);
        parameters.setIncludePageAfter(true);
        testContext.directoryOutputTo(parameters);
        execute(parameters);
        testContext.assertOutputContainsFilenames("1_Employee One.pdf", "3_Employee Three.pdf", "2_Employee Two.pdf");
        testContext.assertOutputSize(3);
        testContext.assertPages("1_Employee One.pdf", 2);
    }

}
