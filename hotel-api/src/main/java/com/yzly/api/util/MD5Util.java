package com.yzly.api.util;

import org.springframework.util.DigestUtils;


/**
 * @author lazyb
 * @create 2020/4/15
 * @desc
 **/
public class MD5Util {

    public static String toMD5(String plainText) {
        String md5 = DigestUtils.md5DigestAsHex(plainText.getBytes());
        return md5;
    }

}
