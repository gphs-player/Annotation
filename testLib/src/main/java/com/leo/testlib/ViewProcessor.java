package com.leo.testlib;

import com.google.auto.service.AutoService;
import com.leo.annotation.IActivity;
import com.leo.annotation.IView;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterSpec;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;

import java.io.IOException;
import java.util.List;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;

/**
 * <p>Date:2019/5/9.5:13 PM</p>
 * <p>Author:niu bao</p>
 * <p>Desc:</p>
 */

//@AutoService(Processor.class)//或者手动创建META-INF目录
@SupportedSourceVersion(SourceVersion.RELEASE_8)
@SupportedAnnotationTypes({"com.leo.annotation.IActivity"})
public class ViewProcessor extends AbstractProcessor {

    private Elements elementUtils;
    private Types typeUtils;

    @Override
    public synchronized void init(ProcessingEnvironment processingEnvironment) {
        System.out.println("AAA===========");
        super.init(processingEnvironment);
        elementUtils = processingEnvironment.getElementUtils();
        typeUtils = processingEnvironment.getTypeUtils();
    }

    @Override
    public boolean process(Set<? extends TypeElement> set, RoundEnvironment roundEnvironment) {
        if (set != null) {
            Set<? extends Element> elements = roundEnvironment.getElementsAnnotatedWith(IActivity.class);
            if (elements != null) {
                //判断注解结点是否为Activity
                TypeElement typeElement = elementUtils.getTypeElement("android.app.Activity");
                for (Element element : elements) {
                    TypeMirror typeMirror = element.asType();
                    if (typeUtils.isSubtype(typeMirror, typeElement.asType())) {
                        TypeElement clzelement = (TypeElement) element;
                        System.err.println(">>> "+typeMirror.toString());//com.leo.annotation.MainActivity
                        ParameterSpec altlas = ParameterSpec.builder(ClassName.get(typeMirror), "activity").build();
                        MethodSpec.Builder builder = MethodSpec.methodBuilder("findById")
                                .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                                .returns(TypeName.VOID)
                                .addParameter(altlas);

                        List<? extends Element> allMembers = elementUtils.getAllMembers(clzelement);
                        for (Element member : allMembers) {
                            IView iView = member.getAnnotation(IView.class);
                            if (iView == null) continue;
                            System.out.println(">>> "+member.asType());//android.widget.TextView
                            builder.addStatement(String.format("activity.%s = (%s) activity.findViewById(%s)"
                                    , member.getSimpleName()//注解结点变量名称
                                    , ClassName.get(member.asType()).toString()//注解结点变量类型
                                    , iView.value()));
                        }
                        TypeSpec typeSpec = TypeSpec.classBuilder("ManagerFindBy" + element.getSimpleName())
                                .addModifiers(Modifier.PUBLIC, Modifier.FINAL)
                                .addMethod(builder.build())
                                .build();
                        JavaFile javaFile = JavaFile.builder("com.leo.view", typeSpec).build();
                        try {
                            javaFile.writeTo(processingEnv.getFiler());
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    } else {
                        throw new RuntimeException("IActivity 要用在Activity上");
                    }
                }
            }
            return true;
        }
        return false;
    }
}
