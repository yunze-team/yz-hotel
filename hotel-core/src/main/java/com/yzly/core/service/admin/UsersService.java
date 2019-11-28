package com.yzly.core.service.admin;

import com.yzly.core.domain.admin.Users;
import com.yzly.core.repository.admin.UsersRepository;
import com.yzly.core.util.PasswordEncryption;
import lombok.extern.apachecommons.CommonsLog;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author lazyb
 * @create 2019/11/25
 * @desc
 **/
@Service
@CommonsLog
public class UsersService {

    @Autowired
    private UsersRepository usersRepository;

    public Users register(String name, String password) {
        Users users = usersRepository.findByName(name);
        if (users != null) {
            log.error("name is exit");
            return null;
        }
        users = new Users(name, PasswordEncryption.BCRYPT.encrypt(password));
        users = usersRepository.save(users);
        return users;
    }

    public Users login(String name, String password) {
        Users users = usersRepository.findByName(name);
        if (users == null) {
            log.error("name is error");
            return null;
        }
        if (!PasswordEncryption.BCRYPT.check(password, users.getPassword())) {
            log.error("password is error");
            return null;
        }
        return users;
    }

    public Users findByName(String name) {
        return usersRepository.findByName(name);
    }

}
