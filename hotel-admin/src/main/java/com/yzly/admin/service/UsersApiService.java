package com.yzly.admin.service;

import com.yzly.admin.domain.ReturnT;
import com.yzly.admin.util.CookieUtil;
import com.yzly.admin.util.I18nUtil;
import com.yzly.admin.util.JacksonUtil;
import com.yzly.core.domain.admin.Users;
import com.yzly.core.service.admin.UsersService;
import com.yzly.core.util.PasswordEncryption;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.math.BigInteger;

/**
 * @author lazyb
 * @create 2020/1/17
 * @desc
 **/
@Service
public class UsersApiService {

    public static final String LOGIN_IDENTITY_KEY = "YZ_ADMIN_LOGIN_IDENTITY";

    @Autowired
    private UsersService usersService;


    public Users ifLogin(HttpServletRequest request, HttpServletResponse response) {
        String cookieToken = CookieUtil.getValue(request, LOGIN_IDENTITY_KEY);
        if (cookieToken != null) {
            Users cookieUser = null;
            try {
                cookieUser = parseToken(cookieToken);
            } catch (Exception e) {
                logout(request, response);
            }
            if (cookieUser != null) {
                Users dbUser = usersService.findByName(cookieUser.getName());
                if (dbUser != null) {
                    if (cookieUser.getPassword().equals(dbUser.getPassword())) {
                        return dbUser;
                    }
                }
            }
        }
        return null;
    }

    private Users parseToken(String tokenHex) {
        Users users = null;
        if (tokenHex != null) {
            String tokenJson = new String(new BigInteger(tokenHex, 16).toByteArray());      // username_password(md5)
            users = JacksonUtil.readValue(tokenJson, Users.class);
        }
        return users;
    }

    public ReturnT<String> logout(HttpServletRequest request, HttpServletResponse response) {
        CookieUtil.remove(request, response, LOGIN_IDENTITY_KEY);
        return ReturnT.SUCCESS;
    }

    private String makeToken(Users users){
        String tokenJson = JacksonUtil.writeValueAsString(users);
        String tokenHex = new BigInteger(tokenJson.getBytes()).toString(16);
        return tokenHex;
    }

    public ReturnT<String> login(HttpServletRequest request, HttpServletResponse response, String username, String password, boolean ifRemember){

        // param
        if (username==null || username.trim().length()==0 || password==null || password.trim().length()==0){
            return new ReturnT<String>(500, I18nUtil.getString("login_param_empty"));
        }

        // valid passowrd
        Users users = usersService.findByName(username);
        if (users == null) {
            return new ReturnT<String>(500, I18nUtil.getString("login_param_unvalid"));
        }
        if (!PasswordEncryption.BCRYPT.check(password, users.getPassword())) {
            return new ReturnT<String>(500, I18nUtil.getString("login_param_unvalid"));
        }

        String loginToken = makeToken(users);

        // do login
        CookieUtil.set(response, LOGIN_IDENTITY_KEY, loginToken, ifRemember);
        return ReturnT.SUCCESS;
    }

}
