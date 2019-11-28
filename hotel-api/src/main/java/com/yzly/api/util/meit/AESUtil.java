package com.yzly.api.util.meit;

import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.security.Key;

public class AESUtil {

    /**
     * 用来进行加密的操作
     *
     * @param data
     * @return
     * @throws Exception
     */
    public String encrypt(String data) throws Exception {
        Key key = new SecretKeySpec(keyValue, KEY_ALGORITHM);
        Cipher c = Cipher.getInstance(KEY_ALGORITHM);// 创建密码器
        c.init(Cipher.ENCRYPT_MODE, key);// 初始化
        byte[] encVal = c.doFinal(data.getBytes());
        return new BASE64Encoder().encode(encVal);
    }

    /**
     * 用来进行解密的操作
     *
     * @param encryptedData
     * @return
     * @throws Exception
     */
    public String decrypt(String encryptedData) throws Exception {
        Key key = new SecretKeySpec(keyValue, KEY_ALGORITHM);
        Cipher c = Cipher.getInstance(KEY_ALGORITHM);
        c.init(Cipher.DECRYPT_MODE, key);
        byte[] decordedValue = new BASE64Decoder().decodeBuffer(encryptedData);
        byte[] decValue = c.doFinal(decordedValue);
        return new String(decValue);
    }


    // 加密算法
    private String KEY_ALGORITHM =  "xxx";

    //加密密钥
    private static final byte[] keyValue =    "xxx".getBytes();
}