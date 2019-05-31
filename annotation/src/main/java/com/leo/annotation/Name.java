package com.leo.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * <p>Date:2019/5/9.4:46 PM</p>
 * <p>Author:niu bao</p>
 * <p>Desc:</p>
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.CLASS)
public @interface Name {
    String name();
}
