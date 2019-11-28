package com.yzly.core.domain.dotw;

import lombok.Data;
import lombok.ToString;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.util.Date;

/**
 * Created by toby on 2019/11/21.
 */
@Data
@ToString
@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(name = "dotw_currency")
public class Currency {

    @Id
    @GeneratedValue
    private Long id;

    @Column(length = 50)
    private String name;

    @Column(length = 20)
    private String code;

    @Column(length = 200)
    private String fullName;

    @Column(nullable = false, updatable = false, name = "created_at")
    @Temporal(TemporalType.TIMESTAMP)
    @CreatedDate
    private Date createdAt;

    @Column(nullable = false, name = "updated_at")
    @LastModifiedDate
    private Date updatedAt;

    public Currency(String name, String code, String fullName) {
        this.name = name;
        this.code = code;
        this.fullName = fullName;
    }
}
