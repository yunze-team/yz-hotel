package com.yzly.api.annotations;

import java.lang.annotation.*;

/**
 * @Description:TODO
 * @Auther tanyuan
 * version V1.0
 **/
@Target(ElementType.TYPE)
@Documented
@Retention(RetentionPolicy.RUNTIME)
public @interface Struct {
    boolean isEnd() default false;
}
