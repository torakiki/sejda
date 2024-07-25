package org.sejda.model;/*
 * Created on 31/08/22
 * Copyright 2022 Sober Lemur S.r.l. and Sejda BV
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

import jakarta.validation.Configuration;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.hibernate.validator.messageinterpolation.ParameterMessageInterpolator;
import org.sejda.commons.util.IOUtils;
import org.sejda.model.encryption.CipherBasedEncryptionAtRest;
import org.sejda.model.encryption.EncryptionAtRestPolicy;
import org.sejda.model.input.PdfFileSource;
import org.sejda.model.input.PdfStreamSource;
import org.sejda.model.input.TaskSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

/**
 * @author Andrea Vacondio
 */
public final class TestUtils {
    private static final Logger LOG = LoggerFactory.getLogger(TestUtils.class);
    private static final NotInstanceOf NOT_INSTANCE_OF = new NotInstanceOf();

    public static <T> void assertInvalidParameters(T parameters) {
        Validator VALIDATOR = new ValidatorHolder().getValidator();
        Set<ConstraintViolation<T>> violations = VALIDATOR.validate(parameters);
        for (ConstraintViolation<T> violation : violations) {
            LOG.debug("{}: {}", violation.getPropertyPath(), violation.getMessage());
        }
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

    /**
     * Class used to test instance of returning false.
     *
     * @author Andrea Vacondio
     */
    private static final class NotInstanceOf {
        // nothing
    }

    private static Cipher getCipher(int mode) {
        String salt = "9qZGubQY4B6Ra7GU5ZN9";
        String key = "MjxHL4QHjWqQt2qfYN6Z1whe6VJvJKfk3xfDBZJCgv0fqdksKkHhbrWy7Lqj9qNEZwA";
        return getCipher(salt, key, mode);
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

    private static SecretKeySpec keyToSpec(String salt, String key) throws NoSuchAlgorithmException {
        byte[] keyBytes = (salt + key).getBytes(StandardCharsets.UTF_8);
        MessageDigest sha = MessageDigest.getInstance("SHA-1");
        keyBytes = sha.digest(keyBytes);
        keyBytes = Arrays.copyOf(keyBytes, 16);
        return new SecretKeySpec(keyBytes, "AES");
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
}
