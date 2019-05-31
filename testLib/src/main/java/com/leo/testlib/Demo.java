package com.leo.testlib;

import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeSpec;

import javax.lang.model.element.Modifier;

/**
 * <p>Desc:JavaFile生成一个示例，输出到控制台</p>
 */
public class Demo {
    public static void main(String [] args){

        JavaFile javaFile = JavaFile.builder("com.walfud.howtojavapoet",
                // TypeSpec 代表一个类
                TypeSpec.classBuilder("Clazz")
                        // 给类添加一个属性
                        .addField(FieldSpec.builder(int.class, "mField", Modifier.PRIVATE)
                                .build())
                        // 给类添加一个方法
                        .addMethod(MethodSpec.methodBuilder("method")
                                .addModifiers(Modifier.PUBLIC)
                                .returns(void.class)
                                .addStatement("System.out.println(str)")
                                .build())
                        .build())
                .build();
        System.out.println(javaFile.toString());
    }
}
