package com.yzly.api.annotations;

import java.lang.annotation.*;

/**
 * @Description:TODO
 * @Auther tanyuan
 * version V1.0
 **/
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Target(ElementType.FIELD)
public @interface Header {
    HeaderType value() default HeaderType.BODY;
}


