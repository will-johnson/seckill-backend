package com.seen.seckillbackend.common.util;

import org.apache.tomcat.util.codec.binary.Base64;

import javax.crypto.*;
import javax.crypto.spec.SecretKeySpec;
import java.security.InvalidKeyException;
import java.security.Key;

public class AesCryption {

    private static Cipher cipher;
    private static Key key;

    static {
        // 生成KEY
        try {
            KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");
            keyGenerator.init(128);
            SecretKey secretKey = keyGenerator.generateKey();
            byte[] keyBytes = secretKey.getEncoded();

            // key转换
            key = new SecretKeySpec(keyBytes, "AES");
            cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static String encrypt(String src) {

        String code = null;
        try {
            cipher.init(Cipher.ENCRYPT_MODE, key);
            byte[] temp = cipher.doFinal(src.getBytes());
            code = Base64.encodeBase64String(temp);
        } catch (BadPaddingException | IllegalBlockSizeException | InvalidKeyException e) {
            e.printStackTrace();
        }
        return code;
    }

    public static String decrypt(String code) {
        String src = null;
        try {
            // 解密
            cipher.init(Cipher.DECRYPT_MODE, key);
            byte[] bytes = Base64.decodeBase64(code);
            byte[] bytes1 = cipher.doFinal(bytes);
            src = new String(bytes1);
        } catch (BadPaddingException | IllegalBlockSizeException | InvalidKeyException e) {
            e.printStackTrace();
        }
        return src;
    }

}
