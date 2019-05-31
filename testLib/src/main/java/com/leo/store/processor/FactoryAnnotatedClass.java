package com.leo.store.processor;

import com.leo.annotation.Factory;

import javax.lang.model.element.TypeElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.MirroredTypeException;

public class FactoryAnnotatedClass {

    private String qualifiedSuperClassName;
    private String simpleTypeName;
    private String id;
    private TypeElement annotatedClassElement;

    public FactoryAnnotatedClass(TypeElement classElement) throws Exception {
        this.annotatedClassElement = classElement;
        Factory annotation = classElement.getAnnotation(Factory.class);
        id = annotation.id();
        if (id == null || id.length() == 0){
            throw  new IllegalArgumentException(String.format("id() in @%s for class %s is null or empty! 这么牛逼的操作是不允许的!",Factory.class.getSimpleName(),classElement.getQualifiedName().toString()));
        }
        try {
            //已经编译的字节码文件
            System.out.println(">>> 已经编译的字节码文件 <<<");
            //获取注解type方法的全限定类名
            Class<?> clazz = annotation.type();
            qualifiedSuperClassName = clazz.getCanonicalName();
            simpleTypeName = clazz.getSimpleName();
        }catch (MirroredTypeException e){
            //尚未编译的源文件
            System.out.println(">>> 尚未编译的源文件 <<<");
            DeclaredType typeMirror = (DeclaredType) e.getTypeMirror();
            TypeElement element = (TypeElement) typeMirror.asElement();
            qualifiedSuperClassName = element.getQualifiedName().toString();
            simpleTypeName = element.getSimpleName().toString();
        }
    }


    public String getId() {
        return id;
    }

    /**
     * Factory#type() 指明的类型全路径修饰符
     * Get the full qualified name of the type specified in  {@link Factory#type()}.
     *
     * @return qualified name
     */
    public String getQualifiedFactoryGroupName() {
        return qualifiedSuperClassName;
    }


    /**
     * Factory#type() 指明的类型
     * Get the simple name of the type specified in  {@link Factory#type()}.
     *
     * @return qualified name
     */
    public String getSimpleFactoryGroupName() {
        return simpleTypeName;
    }

    /**
     * 使用了 @Factory 注解的元素
     */
    public TypeElement getTypeElement() {
        return annotatedClassElement;
    }

}
