package com.yzly.core.domain.ctrip;

import com.alibaba.fastjson.JSONObject;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.persistence.Id;
import java.util.Date;

/**
 * 携程报文收发日志实体
 * @author lazyb
 * @create 2020/9/8
 * @desc
 **/
@Document(collection = "ctrip_xml_log")
@Data
@NoArgsConstructor
@ToString
public class CtripXmlLog {

    @Id
    private ObjectId id;

    private String requestName;

    private String reqXml;

    private String respXml;

    private JSONObject reqData;

    private JSONObject respData;

    private String traceId;

    private String echoToken;

    private String respId;

    @CreatedDate
    private Date createdAt;

    @LastModifiedDate
    private Date updatedAt;

}
