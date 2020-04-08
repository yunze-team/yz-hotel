package com.yzly.api.annotations;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Documented
@Target({ElementType.FIELD, ElementType.TYPE})
public @interface Data {
    String value() default "";
}