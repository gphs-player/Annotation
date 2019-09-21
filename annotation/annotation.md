### 基本注解



`@SuppressWarnings`: 抑制警告

`@Deprecated	`：声明方法过时

`@SafeVarargs`：声明泛型可变参数的安全性，Java7新增

`@Override`：声明覆写方法



```java
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
```



### 元注解





