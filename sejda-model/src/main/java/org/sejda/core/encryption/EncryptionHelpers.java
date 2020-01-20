package org.sejda.core.encryption;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

public class EncryptionHelpers {

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
