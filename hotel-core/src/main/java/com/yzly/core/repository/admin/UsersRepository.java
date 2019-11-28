package com.yzly.core.repository.admin;

import com.yzly.core.domain.admin.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * @author lazyb
 * @create 2019/11/25
 * @desc
 **/
@Repository
public interface UsersRepository extends JpaRepository<Users, Long> {

    Users findByName(String name);

}
