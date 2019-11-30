package com.yzly.core.domain.jltour;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.util.Date;

/**
 * @author lazyb
 * @create 2019/12/1
 * @desc
 **/
@Data
@ToString
@NoArgsConstructor
@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(name = "jl_hotel")
public class JLHotel {

    @Id
    @GeneratedValue
    private Long id;

    @Column(length = 50)
    private String hid;

    @Column(length = 200)
    private String name;

    @Column(length = 500)
    private String address;

    @Column(length = 200)
    private String tel;

    @Column(length = 100)
    private String longitude;

    @Column(length = 100)
    private String latitude;

    @Column(nullable = false, updatable = false, name = "created_at")
    @Temporal(TemporalType.TIMESTAMP)
    @CreatedDate
    private Date createdAt;

    @Column(nullable = false, name = "updated_at")
    @LastModifiedDate
    private Date updatedAt;

}
