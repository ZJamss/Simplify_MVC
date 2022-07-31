package com.mvc.annotation;

import java.lang.annotation.*;

/**
 * @Program: simplify_mvc
 * @Description:
 * @Author: ZJamss
 * @Create: 2022-07-31 13:43
 **/
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Service {
    String value() default "";
}
