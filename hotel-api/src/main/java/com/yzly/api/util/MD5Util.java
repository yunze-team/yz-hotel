package com.yzly.api.util;

import java.security.MessageDigest;

/**
 * @author lazyb
 * @create 2020/4/15
 * @desc
 **/
public class MD5Util {

    public static String toMD5(String plainText) {
        StringBuffer buffer = new StringBuffer("");
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(plainText.getBytes());
            byte b[] = md.digest();
            int i;
            for (int offset = 0; offset < b.length; offset++) {
                i = b[offset];
                if (i < 0) {
                    i += 256;
                }
                if (i < 16) {
                    buffer.append("0");
                } else {
                    buffer.append(Integer.toHexString(i));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return buffer.toString();
    }

}
