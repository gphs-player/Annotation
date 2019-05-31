package com.leo.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * <p>Date:2019/5/9.5:11 PM</p>
 * <p>Author:niu bao</p>
 * <p>Desc:</p>
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.CLASS)
public @interface IActivity {
}
