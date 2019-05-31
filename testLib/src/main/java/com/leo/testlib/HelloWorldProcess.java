package com.leo.testlib;

import com.google.auto.service.AutoService;
import com.leo.annotation.Hello;
import com.leo.annotation.Name;
import com.squareup.javapoet.AnnotationSpec;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeSpec;

import javax.annotation.processing.*;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import java.io.IOException;
import java.util.Date;
import java.util.LinkedHashSet;
import java.util.Set;
@AutoService(Processor.class)//自动生成META-INF
@SupportedAnnotationTypes("com.leo.annotation.Hello")//要处理的注解
@SupportedSourceVersion(SourceVersion.RELEASE_8)
public class HelloWorldProcess extends AbstractProcessor {

    private Filer filer;

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
        filer = processingEnv.getFiler();
    }

    //主逻辑处理
    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        for (TypeElement annotation : annotations) {
            if (annotation.getQualifiedName().toString().equals(Hello.class.getCanonicalName())) {
                /*/////////////////////////////////////////*/
                /*                  类的生成                */
                /*/////////////////////////////////////////*/
                //0.构造
                MethodSpec constructor = MethodSpec.constructorBuilder()
                        .addModifiers(Modifier.PUBLIC)
                        .addParameter(String.class, "greeting")
                        .addStatement("this.$N = $N", "greeting", "greeting")
                        .build();
                //1.添加一个main方法，简单实现
                MethodSpec main = MethodSpec.methodBuilder("main")
                        .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                        .returns(void.class)
                        .addParameter(String[].class, "param")
                        //1)对于无需导包的语句，可以直接addCode
                        .addCode(""
                                + " int total = 0;\n"
                                + " for(int i =0; i< 10; i++){\n"
                                + " \ttotal+=i;\n"
                                +"}\n"
                        )
                        //2)需要导包的语句，addStatement
                        .addStatement("$T.out.println($S)", System.class, "Hello!JavaPoet! ")
                        .build();
                //2.添加语句的流控制方式
                MethodSpec test = MethodSpec.methodBuilder("test")
                        .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                        .returns(void.class)
                        .addParameter(String.class, "str")
                        .addStatement("int result = 0")
                        .beginControlFlow("for (int i = 0; i < 10; i++)")
                        .addStatement("result += i")
                        .endControlFlow()
                        .build();
                //3.1 $L占位符
                MethodSpec getCount = createLMethod("getCount", 0, 5, "+=");
                //3.2 $T占位符
                MethodSpec getDate = createTMethod("getDate");
                MethodSpec getCustom = createTMethodCustom("getCustom");

                //3.3 $N占位符
                MethodSpec[] customMethod = getCustomMethod();

                //4.参数设置,ParameterSpec类和addParameter都能实现，方式不一样
                ParameterSpec android = ParameterSpec.builder(String.class, "android")
                        .addModifiers(Modifier.FINAL)
                        .build();
                MethodSpec welcome = MethodSpec.methodBuilder("welcome")
                        .addParameter(android)
                        .addParameter(String.class, "robot", Modifier.FINAL)
                        .build();
                //5. 字段
                FieldSpec leo = FieldSpec.builder(String.class, "leo", Modifier.PRIVATE)
                        .initializer("$S + $L","Lollipop",5.0d)
                        .build();

                TypeSpec helloWorld = TypeSpec.classBuilder("HelloWorld")
                        .addModifiers(Modifier.PUBLIC, Modifier.FINAL)
                        .addField(String.class,"greeting",Modifier.PRIVATE)
                        .addField(leo)
                        .addMethod(constructor)//构造
                        .addMethod(main)//
                        .addMethod(test)
                        .addMethod(getCount)
                        .addMethod(getDate)
                        .addMethod(getCustom)
                        .addMethod(customMethod[0])
                        .addMethod(customMethod[1])
                        .addMethod(welcome)
                        .build();
                /*////////////////////////////////////////*/
                /*////////////////接口的生成////////////////*/
                /*////////////////////////////////////////*/
                TypeSpec iHello = TypeSpec.interfaceBuilder("IHello")
                        .addModifiers(Modifier.PUBLIC)
                        .addField(
                                FieldSpec.builder(String.class,"CONSTANT")
                                        .addModifiers(Modifier.PUBLIC,Modifier.STATIC,Modifier.FINAL)
                                        .initializer("$S","change")
                                        .build()
                        ).addMethod(
                                MethodSpec.methodBuilder("hello")
                                        .addModifiers(Modifier.PUBLIC,Modifier.ABSTRACT)
                                        .build()
                        ).build();
                /*////////////////////////////////////////////////*/
                /*////////////////重写方法和注解的生成////////////////*/
                /*////////////////////////////////////////////////*/
                MethodSpec sayHello = MethodSpec.methodBuilder("sayHello")
                        .addAnnotation(
                                AnnotationSpec.builder(Name.class)
                                        .addMember("name","$S","leo")
                                        .build()
                        )
                        .build();
                TypeSpec override = TypeSpec.classBuilder("World")
                        .addMethod(sayHello)
                        .addMethod(
                                MethodSpec.methodBuilder("toString")
                                        .addAnnotation(Override.class)
                                        .addModifiers(Modifier.PUBLIC)
                                        .addStatement("return $S", "Hello, World!")
                                        .returns(String.class)
                                        .build()
                        )
                        .build();




                //生成文件
                try {

                    JavaFile javaFile = JavaFile.builder("com.leo", helloWorld)
                            .addFileComment("这个文件自动生成，不要瞎搞。")
                            .build();
                    javaFile.writeTo(filer);

                    JavaFile javaInter = JavaFile.builder("com.leo", iHello)
                            .build();
                    javaInter.writeTo(filer);
                    JavaFile overrideFile = JavaFile.builder("com.leo", override)
                            .build();
                    overrideFile.writeTo(filer);

                } catch (IOException e) {
                    e.printStackTrace();
                    System.out.println("写入文件出错了！！！");
                }
            }
        }
        return true;
    }

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        Set<String> annotation = new LinkedHashSet<>();
        annotation.add(Hello.class.getCanonicalName());
//        return annotation;
        //1.通过注解@SupportedAnnotationTypes("com.leo.annotation.Hello")实现
        //2.覆写此方法
        return super.getSupportedAnnotationTypes();
    }

    @Override
    public SourceVersion getSupportedSourceVersion() {
        //1.@SupportedSourceVersion(SourceVersion.RELEASE_8)注解方式也能实现
        //2.覆写此方法
        return SourceVersion.latest();
    }
    ///////////////////////////////////////////////////////////////////////////
    // 占位符的使用，javaapoet提供的
    ///////////////////////////////////////////////////////////////////////////

    //$L  字面常量
    private MethodSpec createLMethod(String name,int start,int end,String op){
        return MethodSpec.methodBuilder(name)
                .returns(int.class)
                .addModifiers(Modifier.PRIVATE,Modifier.STATIC)
                .addStatement("int result = 0")
                .beginControlFlow("for (int i = $L; i < $L; i++)",start,end)
                .addStatement("result $L i",op)
                .endControlFlow()
                .addStatement("return result ")
                .build();
    }
    //$S String占位符

    //$T 类型（Types） 自动导包
    private MethodSpec createTMethod(String name){
        return MethodSpec.methodBuilder(name)
                .addModifiers(Modifier.PRIVATE,Modifier.STATIC)
                .returns(Date.class)
                .addStatement("return new $T()",Date.class)
                .build();
    }
    private MethodSpec createTMethodCustom(String name){
        ClassName custom = ClassName.get("com.leo.annotation", "Leo");
        ClassName list = ClassName.get("java.util", "List");
        ClassName arrayList = ClassName.get("java.util", "ArrayList");
        ParameterizedTypeName typeName = ParameterizedTypeName.get(list, custom);
        return MethodSpec.methodBuilder(name)
                .addModifiers(Modifier.PRIVATE,Modifier.STATIC)
                .returns(typeName)
                .addStatement("$T result = new $T()",typeName,arrayList)
                .addStatement("result.add(new $T())",custom)
                .addStatement("result.add(new $T())",custom)
                .addStatement("return result")
                .build();
    }

    //$N 命名(Names) 一般指方法名，变量名等
    private MethodSpec[] getCustomMethod(){
        MethodSpec hexDigit = MethodSpec.methodBuilder("hexDigit")
                .addParameter(int.class, "i")
                .returns(char.class)
                .addStatement("return (char)(i < 10 ? i+ '0' : i - 10 +'a')")
                .build();
        MethodSpec byteToHex = MethodSpec.methodBuilder("byteToHex")
                .addParameter(int.class,"b")
                .returns(String.class)
                .addStatement("char[] result = new char[2]")
                .addStatement("result[0] = $N((b >>> 4) & 0xf)",hexDigit)
                .addStatement("result[1] = $N(b & 0xf)",hexDigit)
                .addStatement("return new String(result)")
                .build();
        return new MethodSpec[]{hexDigit,byteToHex};

    }
}
