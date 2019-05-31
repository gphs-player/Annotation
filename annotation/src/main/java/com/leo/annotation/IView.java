package com.leo.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * <p>Date:2019/5/9.5:12 PM</p>
 * <p>Author:niu bao</p>
 * <p>Desc:注解ID</p>
 */


@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface IView {
    int value() default 0;
}
