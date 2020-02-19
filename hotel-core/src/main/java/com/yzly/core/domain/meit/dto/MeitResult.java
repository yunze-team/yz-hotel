package com.yzly.core.domain.meit.dto;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.annotation.JSONField;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.util.Date;

/**
 * 美团接口请求返回实体
 * @author lazyb
 * @create 2019/12/23
 * @desc
 **/
@Data
@ToString
@NoArgsConstructor
@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(name = "meit_result")
public class MeitResult {

    @Id
    @GeneratedValue
    @JSONField(serialize = false)
    private Long id;

    @Column(length = 200)
    @JSONField(serialize = false)
    private String traceId;

    @Column
    private Integer code;

    @Column(length = 50)
    private String message;

    @Column
    private Boolean success;

    @Transient
    private Object data;

    @Transient
    @JSONField(serialize = false)
    private JSONObject reqData;

    @Column(nullable = false, updatable = false, name = "created_at")
    @Temporal(TemporalType.TIMESTAMP)
    @CreatedDate
    @JSONField(serialize = false)
    private Date createdAt;

    @Column(nullable = false, name = "updated_at")
    @LastModifiedDate
    @JSONField(serialize = false)
    private Date updatedAt;

    @Lob
    @Column(name = "req_data")
    @JSONField(serialize = false)
    private String req;

    @Lob
    @Column(name = "resp_data")
    @JSONField(serialize = false)
    private String resp;

}
