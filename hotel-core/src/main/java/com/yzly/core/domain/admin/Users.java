package com.yzly.core.domain.admin;

import lombok.Data;
import lombok.ToString;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.util.Date;

/**
 * @author lazyb
 * @create 2019/11/25
 * @desc
 **/
@Data
@ToString
@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(name = "admin_users")
public class Users {

    @Id
    @GeneratedValue
    private Long id;

    @Column(length = 50)
    private String name;

    @Column(length = 200)
    private String password;

    @Column(nullable = false, updatable = false, name = "created_at")
    @Temporal(TemporalType.TIMESTAMP)
    @CreatedDate
    private Date createdAt;

    @Column(nullable = false, name = "updated_at")
    @LastModifiedDate
    private Date updatedAt;

    public Users(String name, String password) {
        this.name = name;
        this.password = password;
    }

}
