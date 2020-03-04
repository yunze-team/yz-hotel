package com.yzly.core.domain.dotw;

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
 * dotw报文收发日志实体
 * @author lazyb
 * @create 2020/3/5
 * @desc
 **/
@Document(collection = "dotw_xml_log")
@Data
@NoArgsConstructor
@ToString
public class DotwXmlLog {

    @Id
    private ObjectId id;

    private String reqXml;

    private String respXml;

    private JSONObject respData;

    private String traceId;

    private String third_trace_id;

    @CreatedDate
    private Date createdAt;

    @LastModifiedDate
    private Date updatedAt;

}
