package com.yzly.core.domain.dotw;

import lombok.Data;
import lombok.ToString;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.util.Date;

/**
 * @author lazyb
 * @create 2019/12/3
 * @desc
 **/
@Data
@ToString
@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(name = "dotw_salutations_ids")
public class SalutationsIds {

    @Id
    @GeneratedValue
    private Long id;

    @Column(length = 10)
    private String code;

    @Column(length = 100)
    private String name;

    @Column(nullable = false, updatable = false, name = "created_at")
    @Temporal(TemporalType.TIMESTAMP)
    @CreatedDate
    private Date createdAt;

    @Column(nullable = false, name = "updated_at")
    @LastModifiedDate
    private Date updatedAt;

    public SalutationsIds(String code, String name) {
        this.code = code;
        this.name = name;
    }
}