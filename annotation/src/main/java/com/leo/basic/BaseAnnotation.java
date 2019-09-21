package com.leo.basic;

/**
 * <p>Date:2019-09-19.09:35</p>
 * <p>Desc:</p>
 */
@SuppressWarnings("unused")
public class BaseAnnotation<T> {

    @Deprecated
    public void nothing() { }

    @SafeVarargs
    public final void safeVars(T... args) {
        for (T arg : args) {
            System.out.println(arg.getClass().getName());
        }
    }

    @Override
    public int hashCode() {
        return 0;
    }
}
