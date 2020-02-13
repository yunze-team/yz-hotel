package com.yzly.api.util.meit.international;

import org.apache.commons.codec.binary.Base64;
import org.junit.Test;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.security.Key;

import static org.junit.Assert.*;

/**
 * @author lazyb
 * @create 2020/2/13
 * @desc
 **/
public class AESUtilUsingCommonDecodecTest {

    // 加密算法
    public static final String CIPHER_ALGORITHM = "AES";

    // 加密密钥
    private static final byte[] keyValue = "meituanhotelopen".getBytes();

    public static Key toKey(byte[] key) throws Exception {
        SecretKey secretKey = new SecretKeySpec(key, CIPHER_ALGORITHM);
        return secretKey;
    }

    @Test
    public void encrypt() {
        try {
            Key k = toKey(keyValue);
            Cipher cipher = Cipher.getInstance(CIPHER_ALGORITHM);
            cipher.init(Cipher.ENCRYPT_MODE, k);
            byte[] encVal = cipher.doFinal("data".getBytes());
            String res = Base64.encodeBase64String(encVal);
            System.out.print(res);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void decrypt() {
    }
}