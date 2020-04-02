package com.yzly.core.domain.meit.query;

import lombok.Getter;
import lombok.Setter;

/**
 * @author lazyb
 * @create 2020/4/2
 * @desc
 **/
@Setter
@Getter
public class MeitResultQuery {

    private String traceId;
    private String startDate;
    private String endDate;
    private String reqMethod;

}
