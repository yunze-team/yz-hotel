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
 * @author lazyb
 * @create 2019/12/20
 * @desc
 **/
@Data
@ToString
@NoArgsConstructor
@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(name = "meit_trace_log")
public class MeitTraceLog {

    @Id
    @GeneratedValue
    private Long id;

    @Column(length = 50)
    private String traceId;

    @Lob
    @Column
    private String encryptData;// 加密数据

    @Lob
    @Column
    private String reqData;

    @Column(nullable = false, updatable = false, name = "created_at")
    @Temporal(TemporalType.TIMESTAMP)
    @CreatedDate
    private Date createdAt;

    @Column(nullable = false, name = "updated_at")
    @LastModifiedDate
    private Date updatedAt;

}
