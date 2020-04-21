package com.yzly.core.domain.jielv.constants;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class Head {
    private String appKey;
    private String timestamp;
    private String sign;
    private String version;

}
