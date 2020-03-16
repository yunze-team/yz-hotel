package com.yzly.core.domain.meit.dto;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.annotation.JSONField;
import lombok.*;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.persistence.*;
import java.util.Date;

/**
 * 美团接口请求返回实体
 * @author lazyb
 * @create 2019/12/23
 * @desc
 **/
@Document(collection = "meit_result")
@Data
@NoArgsConstructor
@ToString
public class MeitResult {

    @Id
    @JSONField(serialize = false)
    private ObjectId id;

    @JSONField(serialize = false)
    private String traceId;

    private Integer code;

    private String message;

    private Boolean success;

    private Object data;

    @JSONField(serialize = false)
    private JSONObject reqData;

    @CreatedDate
    @JSONField(serialize = false)
    private Date createdAt;

    @LastModifiedDate
    @JSONField(serialize = false)
    private Date updatedAt;

    @JSONField(serialize = false)
    private String req;

    @JSONField(serialize = false)
    private String resp;

    @JSONField(serialize = false)
    private String localTraceId;

    @JSONField(serialize = false)
    private String reqMethod;

}
