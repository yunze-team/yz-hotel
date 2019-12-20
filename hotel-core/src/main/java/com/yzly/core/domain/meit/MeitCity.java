package com.yzly.core.domain.meit;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.util.Date;

/**
 * 美团城市列表
 * @author lazyb
 * @create 2019/12/20
 * @desc
 **/
@Data
@ToString
@NoArgsConstructor
@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(name = "meit_city")
public class MeitCity {

    @Id
    @GeneratedValue
    private Long id;

    @Column(length = 50)
    private String cityId;

    @Column(name = "name_cn", length = 100)
    private String nameCN;

    @Column(name = "name_en", length = 100)
    private String nameEN;

    @Column(length = 200)
    private String nameLong;

    @Column(nullable = false, updatable = false, name = "created_at")
    @Temporal(TemporalType.TIMESTAMP)
    @CreatedDate
    private Date createdAt;

    @Column(nullable = false, name = "updated_at")
    @LastModifiedDate
    private Date updatedAt;

}
