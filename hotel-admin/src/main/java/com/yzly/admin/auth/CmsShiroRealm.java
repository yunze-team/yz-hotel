package com.yzly.admin.auth;

import com.yzly.core.domain.admin.Users;
import com.yzly.core.service.admin.UsersService;
import lombok.extern.apachecommons.CommonsLog;
import org.apache.shiro.authc.*;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.HashSet;
import java.util.Set;

/**
 * @author lazyb
 * @create 2019/5/30
 * @desc
 **/
@CommonsLog
public class CmsShiroRealm extends AuthorizingRealm {

    @Autowired
    private UsersService usersService;

    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principalCollection) {
        log.info("权限认证方法：GatewayShiroRealm.doGetAuthenticationInfo()");
        String name = super.getAvailablePrincipal(principalCollection).toString();
        Users admin = usersService.findByName(name);
        if (admin != null) {
            SimpleAuthorizationInfo info = new SimpleAuthorizationInfo();
            Set<String> roleSet = new HashSet<>();
            roleSet.add("admin");
            info.setRoles(roleSet);
            return info;
        }
        return null;
    }

    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken authenticationToken) throws AuthenticationException {
        log.info("身份认证方法：GatewayShiroRealm.doGetAuthenticationInfo()");
        UsernamePasswordToken authToken = (UsernamePasswordToken) authenticationToken;
        String name = String.valueOf(authToken.getUsername());
        String pwd = String.valueOf(authToken.getPassword());
        Users admin = usersService.login(name, pwd);
        if (admin == null) {
            throw new AccountException("账号或密码不正确！");
        }
        return new SimpleAuthenticationInfo(name, pwd, getName());
    }

}
