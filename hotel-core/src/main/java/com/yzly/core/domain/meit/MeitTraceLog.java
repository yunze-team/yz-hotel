package com.yzly.core.domain.meit;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.persistence.*;
import java.util.Date;

/**
 * @author lazyb
 * @create 2019/12/20
 * @desc
 **/
@Document(collection = "meit_trace_log")
@Data
@NoArgsConstructor
@ToString
public class MeitTraceLog {

    @Id
    private ObjectId id;

    private String traceId;

    private String encryptData;// 加密数据

    private String reqData;

    @CreatedDate
    private Date createdAt;

    @LastModifiedDate
    private Date updatedAt;

}
