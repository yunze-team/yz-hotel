package com.yzly.core.util;

import org.mindrot.jbcrypt.BCrypt;
import org.springframework.util.DigestUtils;

/**
 * @author lazyb
 * @create 2019/11/19
 * @desc
 **/
public enum PasswordEncryption {

    BCRYPT {
        public String encrypt(String content) {
            return BCrypt.hashpw(content, BCrypt.gensalt(4));
        }

        public String MD5(String content) {
            return DigestUtils.md5DigestAsHex(content.getBytes());
        }

        public boolean check(String input, String hashedContent) {
            return BCrypt.checkpw(input, hashedContent);
        }
    };

    private PasswordEncryption() {

    }

    public abstract String encrypt(String var1);

    public abstract String MD5(String var1);

    public abstract boolean check(String var1, String var2);

}
