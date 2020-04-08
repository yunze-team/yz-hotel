package com.yzly.api.annotations;

import java.lang.annotation.*;

/**
 * @Description:TODO
 * @Auther tanyuan
 * version V1.0
 **/
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Target({ElementType.FIELD})
public @interface Fields {
    int length() default -1;
    int scale() default -1;
    String type() default "string";
}
