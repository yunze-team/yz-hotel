package com.yzly.api.util.meit.international;

import org.apache.commons.codec.binary.Base64;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.security.Key;

/**
 * @author lazyb
 * @create 2019/12/17
 * @desc
 **/
public class AESUtilUsingCommonDecodec {

    // 加密算法
    public static final String CIPHER_ALGORITHM = "AES";

    // 加密密钥
    private static final byte[] keyValue = "meituanhotelopen".getBytes();

    /**
     * 转换密钥
     * @param key 二进制密钥
     * @return Key 密钥
     * @throws Exception
     */
    public static Key toKey(byte[] key) throws Exception {
        SecretKey secretKey = new SecretKeySpec(key, CIPHER_ALGORITHM);
        return secretKey;
    }

    /**
     * 加密数据
     * @param data 待加密数据
     * @return String 加密后的数据
     * @throws Exception
     */
    public static String encrypt(String data) throws Exception {
        Key k = toKey(keyValue);
        Cipher cipher = Cipher.getInstance(CIPHER_ALGORITHM);
        cipher.init(Cipher.ENCRYPT_MODE, k);
        byte[] encVal = cipher.doFinal(data.getBytes());
        return Base64.encodeBase64String(encVal);
    }

    /**
     * 解密数据
     * @param data 待解密数据
     * @return String 解密后的数据
     * @throws Exception
     */
    public static String decrypt(String data) throws Exception {
        Key k = toKey(keyValue);
        Cipher cipher = Cipher.getInstance(CIPHER_ALGORITHM);
        cipher.init(Cipher.DECRYPT_MODE, k);
        byte[] decordedValue = Base64.decodeBase64(data);
        byte[] decValue = cipher.doFinal(decordedValue);
        return new String(decValue);
    }

}
