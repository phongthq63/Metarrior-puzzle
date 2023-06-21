package com.bamisu.gamelib.encryption;

import org.apache.commons.codec.binary.Base64;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.util.Arrays;

/**
 * Created by Popeye on 9/29/2017.
 */
public class Encrypter {
    private static Encrypter ourInstance = new Encrypter();
    private Cipher dCipher;
    private Cipher eCipher;

    public static Encrypter getInstance() {
        return ourInstance;
    }

    private SecretKeySpec skey;

    private Encrypter() {//5BA5LFC135O1BD6L
        this.init("LRA31OMPCBC35GMR");
    }

    private void init(String key) {
        try {
            byte[] k;
            k = key.getBytes("UTF-8");
            k = Arrays.copyOf(k, 16); // use only first 128 bit
            skey = new SecretKeySpec(k, "AES");

            // encrypter
            eCipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
            eCipher.init(Cipher.ENCRYPT_MODE, skey);

            // decrypter
            dCipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
            dCipher.init(Cipher.DECRYPT_MODE, skey);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String encrypt(String value) {
        try {
            if (value == null)
                return null;
            byte[] utf8 = value.getBytes("utf-8");
            byte[] enc = eCipher.doFinal(utf8);
            // Encode bytes to base64 to get a string
            return new String(Base64.encodeBase64(enc), "UTF-8");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public String decrypt(String value) {
        try {

            if (value == null)
                return null;

            // Decode base64 to get bytes
            byte[] dec = Base64.decodeBase64(value.getBytes("UTF-8"));
            // Decrypt
            byte[] utf8 = dCipher.doFinal(dec);

            // Decode using utf-8
            return new String(utf8, "UTF8");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
