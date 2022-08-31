/*
 * Copyright 2022 Sober Lemur S.a.s. di Vacondio Andrea and Sejda BV
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
package org.sejda.tests;

import jakarta.validation.Configuration;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.hibernate.validator.messageinterpolation.ParameterMessageInterpolator;
import org.sejda.commons.util.IOUtils;
import org.sejda.commons.util.StringUtils;
import org.sejda.model.encryption.CipherBasedEncryptionAtRest;
import org.sejda.model.encryption.EncryptionAtRestPolicy;
import org.sejda.model.input.FileSource;
import org.sejda.model.input.PdfFileSource;
import org.sejda.model.input.PdfStreamSource;
import org.sejda.model.input.StreamSource;
import org.sejda.model.input.TaskSource;
import org.sejda.model.parameter.base.TaskParameters;
import org.sejda.sambox.pdmodel.PDDocument;
import org.sejda.sambox.pdmodel.PDPage;
import org.sejda.sambox.pdmodel.common.PDPageLabelRange;
import org.sejda.sambox.pdmodel.common.PDPageLabels;
import org.sejda.sambox.pdmodel.common.PDRectangle;
import org.sejda.sambox.pdmodel.interactive.annotation.PDAnnotationLink;
import org.sejda.sambox.pdmodel.interactive.documentnavigation.destination.PDPageDestination;
import org.sejda.sambox.text.PDFTextStripper;
import org.sejda.sambox.text.PDFTextStripperByArea;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.awt.Rectangle;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.fail;
import static org.sejda.commons.util.StringUtils.normalizeLineEndings;

/**
 * Test utilities
 *
 * @author Andrea Vacondio
 */
public final class TestUtils {

    private static final Logger LOG = LoggerFactory.getLogger(TestUtils.class);
    private static final NotInstanceOf NOT_INSTANCE_OF = new NotInstanceOf();

    private TestUtils() {
        // util
    }

    /**
     * Sets the given property to the given instance at the given value.
     *
     * @param instance
     * @param propertyName
     * @param propertyValue
     */
    public static void setProperty(Object instance, String propertyName, Object propertyValue) {
        Field field;
        try {
            field = instance.getClass().getDeclaredField(propertyName);
            field.setAccessible(true);
            field.set(instance, propertyValue);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new IllegalStateException(String.format("Unable to set field %s", propertyName), e);
        }
    }

    public static void assertInvalidParameters(TaskParameters parameters) {
        Validator VALIDATOR = new ValidatorHolder().getValidator();
        Set<ConstraintViolation<TaskParameters>> violations = VALIDATOR.validate(parameters);
        for (ConstraintViolation<TaskParameters> violation : violations) {
            LOG.debug("{}: {}", violation.getPropertyPath(), violation.getMessage());
        }
        assertFalse(violations.isEmpty());
    }

    public static void assertValidParameters(TaskParameters parameters) {
        Validator VALIDATOR = new ValidatorHolder().getValidator();
        Set<ConstraintViolation<TaskParameters>> violations = VALIDATOR.validate(parameters);
        assertFalse(violations.isEmpty());
    }

    private static class ValidatorHolder {

        private Validator validator;

        private ValidatorHolder() {
            Configuration<?> validationConfig = Validation.byDefaultProvider().configure();
            validationConfig.ignoreXmlConfiguration();
            validationConfig.messageInterpolator(new ParameterMessageInterpolator());
            ValidatorFactory factory = validationConfig.buildValidatorFactory();
            validator = factory.getValidator();
        }

        public Validator getValidator() {
            return validator;
        }

    }

    /**
     * Class used to test instance of returning false.
     *
     * @author Andrea Vacondio
     */
    private static final class NotInstanceOf {
        // nothing
    }

    /**
     * Test that the equals and hashCode implementations respect the general rules being reflexive, transitive and symmetric.
     *
     * @param <T>
     * @param eq1  equal instance
     * @param eq2  equal instance
     * @param eq3  equal instance
     * @param diff not equal instance
     */
    public static <T> void testEqualsAndHashCodes(T eq1, T eq2, T eq3, T diff) {
        // null safe
        assertNotEquals(null, eq1);

        // not instance of
        assertNotEquals(eq1, NOT_INSTANCE_OF);

        // reflexive
        assertEquals(eq1, eq1);
        assertEquals(eq1.hashCode(), eq1.hashCode());

        // symmetric
        assertEquals(eq1, eq2);
        assertEquals(eq2, eq1);
        assertEquals(eq1.hashCode(), eq2.hashCode());
        assertNotEquals(eq2, diff);
        assertNotEquals(diff, eq2);
        assertNotEquals(diff.hashCode(), eq2.hashCode());

        // transitive
        assertEquals(eq1, eq2);
        assertEquals(eq2, eq3);
        assertEquals(eq1, eq3);
        assertEquals(eq1.hashCode(), eq2.hashCode());
        assertEquals(eq2.hashCode(), eq3.hashCode());
        assertEquals(eq1.hashCode(), eq3.hashCode());
    }

    private static Cipher getCipher(int mode) {
        String salt = "9qZGubQY4B6Ra7GU5ZN9";
        String key = "MjxHL4QHjWqQt2qfYN6Z1whe6VJvJKfk3xfDBZJCgv0fqdksKkHhbrWy7Lqj9qNEZwA";
        return getCipher(salt, key, mode);
    }

    public static EncryptionAtRestPolicy getEncryptionAtRestPolicy() {
        return new CipherBasedEncryptionAtRest(TestUtils::getCipher);
    }

    private static File encryptedAtRestFile(TaskSource<?> source) throws IOException {
        return encryptedAtRest(source.getSeekableSource().asNewInputStream(), source.getName());
    }

    private static File encryptedAtRest(InputStream in, String name) {
        try {
            File file = org.sejda.model.util.IOUtils.createTemporaryBufferWithName(name);
            OutputStream out = getEncryptionAtRestPolicy().encrypt(new FileOutputStream(file));
            IOUtils.copy(in, out);
            IOUtils.closeQuietly(out);
            IOUtils.closeQuietly(in);

            return file;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static StreamSource encryptedAtRest(StreamSource source) throws IOException {
        File file = encryptedAtRestFile(source);
        StreamSource result = StreamSource.newInstance(new FileInputStream(file), source.getName());
        result.setEncryptionAtRestPolicy(getEncryptionAtRestPolicy());
        return result;
    }

    public static FileSource encryptedAtRest(FileSource source) throws IOException {
        File file = encryptedAtRest(source.getSeekableSource().asNewInputStream(), source.getName());
        FileSource result = FileSource.newInstance(file);
        result.setEncryptionAtRestPolicy(getEncryptionAtRestPolicy());
        return result;
    }

    public static PdfStreamSource encryptedAtRest(PdfStreamSource source) throws IOException {
        File file = encryptedAtRestFile(source);
        PdfStreamSource result = PdfStreamSource.newInstanceWithPassword(new FileInputStream(file), source.getName(),
                source.getPassword());
        result.setEncryptionAtRestPolicy(getEncryptionAtRestPolicy());
        return result;
    }

    public static PdfFileSource encryptedAtRest(PdfFileSource source) throws IOException {
        File file = encryptedAtRestFile(source);
        PdfFileSource result = PdfFileSource.newInstanceWithPassword(file, source.getPassword());
        result.setEncryptionAtRestPolicy(getEncryptionAtRestPolicy());
        return result;
    }


    public static String getPageText(PDPage page) throws IOException {
        PDFTextStripperByArea textStripper = new PDFTextStripperByArea();
        PDRectangle pageSize = page.getCropBox();
        Rectangle cropBoxRectangle = new Rectangle(0, 0, (int) pageSize.getWidth(), (int) pageSize.getHeight());
        if (page.getRotation() == 90 || page.getRotation() == 270) {
            cropBoxRectangle = new Rectangle(0, 0, (int) pageSize.getHeight(), (int) pageSize.getWidth());
        }
        textStripper.setSortByPosition(true);
        textStripper.addRegion("area1", cropBoxRectangle);
        textStripper.extractRegions(page);
        return textStripper.getTextForRegion("area1");
    }

    public static void withPageText(PDPage page, Consumer<String> callback) {
        try {
            callback.accept(getPageText(page));
        } catch (IOException e) {
            fail(e.getMessage());
        }
    }

    public static String getPageTextNormalized(PDPage page) throws IOException {
        return normalizeLineEndings(getPageText(page));
    }

    public static String getDocTextNormalized(PDDocument doc) throws IOException {
        return normalizeLineEndings(new PDFTextStripper().getText(doc));
    }

    public static void assertPageText(PDPage page, String text) {
        withPageText(page, pageText -> {
            assertEquals(text, pageText.replaceAll("[^A-Za-z0-9]", ""));
        });
    }

    public static void assertPageTextExact(PDPage page, String text) {
        withPageText(page, pageText -> {
            assertEquals(text, pageText);
        });
    }

    public static void assertPageTextExactLines(PDPage page, String text) {
        withPageText(page, pageText -> {
            assertEquals(normalizeLineEndings(text), normalizeLineEndings(pageText));
        });
    }

    public static void assertDocTextExactLines(PDDocument doc, String text) throws IOException {
        assertEquals((text), getDocTextNormalized(doc));
    }

    public static void assertPageTextContains(PDPage page, String text) {
        withPageText(page, pageText -> {
            pageText = StringUtils.normalizeWhitespace(pageText);
            // ignores whitespace
            pageText = pageText.replaceAll("\\s", "");
            assertThat(pageText, containsString(text.replaceAll("\\s", "")));
        });
    }

    public static void assertPageTextDoesNotContain(PDPage page, String text) {
        withPageText(page, pageText -> {
            pageText = StringUtils.normalizeWhitespace(pageText);
            // ignores whitespace
            pageText = pageText.replaceAll("\\s", "");
            assertThat(pageText, not(containsString(text.replaceAll("\\s", ""))));
        });
    }

    public static <T> java.util.List<T> getAnnotationsOf(PDPage page, Class<T> clazz) {
        return iteratorToList(
                page.getAnnotations().stream().filter(clazz::isInstance).map(a -> (T) a).iterator());
    }

    public static <T> List<T> iteratorToList(Iterator<T> iterator) {
        List<T> result = new ArrayList<>();
        while (iterator.hasNext()) {
            result.add(iterator.next());
        }
        return result;
    }

    public static void assertPDRectanglesEqual(PDRectangle expected, PDRectangle actual) {
        assertEquals(expected.getLowerLeftX(), actual.getLowerLeftX(), 0.1);
        assertEquals(expected.getLowerLeftY(), actual.getLowerLeftY(), 0.1);
        assertEquals( expected.getWidth(), actual.getWidth(), 0.1);
        assertEquals( expected.getHeight(), actual.getHeight(), 0.1);
    }

    public static void assertPageDestination(PDAnnotationLink link, PDPage expectedPage) throws IOException {
        PDPage actualPage = ((PDPageDestination) link.getDestination()).getPage();
        assertEquals(expectedPage, actualPage);
    }

    public static void assertPageLabelIndexesAre(PDPageLabels labels, Integer... expected) {
        assertThat(labels.getLabels().keySet(), is(new HashSet<>(Arrays.asList(expected))));
    }

    public static void assertPageLabelRangeIs(PDPageLabels labels, int startPage, PDPageLabelRange expected) {
        PDPageLabelRange actual = labels.getPageLabelRange(startPage);
        assertNotNull( actual, "No page label range found at index: " + startPage + ". " + labels.getLabels().keySet());
        assertThat("Difference at index: " + startPage, actual.getCOSObject().toString(),
                is(expected.getCOSObject().toString()));
    }

    public static void assertPageLabelRangeIsDefault(PDPageLabels labels, int startPage) {
        PDPageLabelRange defaultLabel = new PDPageLabelRange();
        defaultLabel.setStyle(PDPageLabelRange.STYLE_DECIMAL);
        assertPageLabelRangeIs(labels, startPage, defaultLabel);
    }

    public static void assertPageLabelRangeIs(PDPageLabels labels, int startPage, String style) {
        assertPageLabelRangeIs(labels, startPage, new PDPageLabelRange(style, null, null));
    }

    public static void assertPageLabelRangeIs(PDPageLabels labels, int startPage, String style, String prefix,
            Integer start) {
        assertPageLabelRangeIs(labels, startPage, new PDPageLabelRange(style, prefix, start));
    }

    private static SecretKeySpec keyToSpec(String salt, String key) throws NoSuchAlgorithmException {
        byte[] keyBytes = (salt + key).getBytes(StandardCharsets.UTF_8);
        MessageDigest sha = MessageDigest.getInstance("SHA-1");
        keyBytes = sha.digest(keyBytes);
        keyBytes = Arrays.copyOf(keyBytes, 16);
        return new SecretKeySpec(keyBytes, "AES");
    }

    public static Cipher getCipher(String salt, String key, int mode) {
        try {
            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
            cipher.init(mode, keyToSpec(salt, key));

            return cipher;
        } catch (GeneralSecurityException e) {
            throw new RuntimeException(e);
        }
    }
}
