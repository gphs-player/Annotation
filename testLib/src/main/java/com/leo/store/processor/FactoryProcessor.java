package com.leo.store.processor;

import com.google.auto.service.AutoService;
import com.leo.annotation.Factory;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import javax.tools.Diagnostic;

/**
 * <p>Date:2019/5/10.11:08 AM</p>
 * <p>Author:niu bao</p>
 * <p>Desc:</p>
 */
//@AutoService(Processor.class)
public class FactoryProcessor extends AbstractProcessor {

    private Types typeUtils;
    private Elements elementUtils;
    private Filer filer;
    private Messager messager;

    private Map<String, FactoryGroupedClasses> factoryClasses =
            new LinkedHashMap<String, FactoryGroupedClasses>();

    @Override
    public synchronized void init(ProcessingEnvironment processingEnvironment) {
        super.init(processingEnvironment);
        typeUtils = processingEnv.getTypeUtils();

        elementUtils = processingEnv.getElementUtils();
        filer = processingEnv.getFiler();
        messager = processingEnv.getMessager();
    }

    @Override
    public boolean process(Set<? extends TypeElement> set, RoundEnvironment roundEnvironment) {
        //拿到所有的Factory注解类
        Set<? extends Element> elementsAnnotatedWith = roundEnvironment.getElementsAnnotatedWith(Factory.class);
        for (Element element : elementsAnnotatedWith) {//加这个注解的可能是class，method，field，必须做检测
            if (element.getKind() != ElementKind.CLASS) {
                error(element, "@%s 注解只能用在类的声明.", Factory.class.getSimpleName());
                return true;
            }
            //判定之后可以强转为ElementKind
            TypeElement typeElement = (TypeElement) element;
            try {
                FactoryAnnotatedClass factoryAnnotatedClass = new FactoryAnnotatedClass(typeElement);
                if (!isValidClass(factoryAnnotatedClass)) {
                    return true;//错误信息已经打印过了，直接退出
                }

                FactoryGroupedClasses factoryClass = factoryClasses.get(factoryAnnotatedClass.getQualifiedFactoryGroupName());
                if (factoryClass == null) {
                    String qualifiedFactoryGroupName = factoryAnnotatedClass.getQualifiedFactoryGroupName();
                    factoryClass = new FactoryGroupedClasses(qualifiedFactoryGroupName);
                    factoryClasses.put(qualifiedFactoryGroupName, factoryClass);
                }
                factoryClass.add(factoryAnnotatedClass);
            } catch (IllegalArgumentException e) {
                error(typeElement, e.getMessage());
                return true;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        try {
            for (FactoryGroupedClasses factoryClasses : factoryClasses.values()) {
                factoryClasses.generateCode(elementUtils, filer);
            }
            factoryClasses.clear();
        } catch (IOException e) {
            error(null, e.getMessage());
        }
        return true;
    }

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        Set<String> set = new LinkedHashSet<>();
        set.add(Factory.class.getCanonicalName());
        return set;
    }

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latestSupported();
    }


    private void error(Element e, String msg, Object... args) {
        //注解处理中要用Messager报告错误相关信息，因为它是提供出去的，抛出异常的操作是不合适的。
        messager.printMessage(Diagnostic.Kind.ERROR, String.format(msg, args), e);
    }


    private boolean isValidClass(FactoryAnnotatedClass item) {
        TypeElement classElement = item.getTypeElement();
        //必须public修饰
        if (!classElement.getModifiers().contains(Modifier.PUBLIC)) {
            error(classElement, String.format("类  %s 不是public修饰的", classElement.getQualifiedName().toString()));
            return false;
        }
        //不能使用抽象类
        if (classElement.getModifiers().contains(Modifier.ABSTRACT)) {
            error(classElement, "The class %s is abstract. You can't annotate abstract classes with @%",
                    classElement.getQualifiedName().toString(), Factory.class.getSimpleName());
            return false;
        }
        //必须是@Factory.type()指定的类的子类
        TypeElement supperClassElement = elementUtils.getTypeElement(item.getQualifiedFactoryGroupName());
        if (supperClassElement.getKind() == ElementKind.INTERFACE) {
            //必须实现指定好的接口
            if (!classElement.getInterfaces().contains(supperClassElement.asType())) {
                error(classElement, "The class %s annotated with @%s must implement the interface %s",
                        classElement.getQualifiedName().toString(), Factory.class.getSimpleName(),
                        item.getQualifiedFactoryGroupName());
                return false;
            }
        } else {
            TypeElement currentClass = classElement;
            while (true) {
                TypeMirror superClassType = currentClass.getSuperclass();
                if (superClassType.getKind() == TypeKind.NONE) {
                    error(classElement, "The class %s annotated with @%s must inherit from %s",
                            classElement.getQualifiedName().toString(), Factory.class.getSimpleName(),
                            item.getQualifiedFactoryGroupName());
                    return false;
                }
                if (superClassType.toString().equals(item.getQualifiedFactoryGroupName())) {
                    // Required super class found
                    break;
                }
                currentClass = (TypeElement) typeUtils.asElement(superClassType);
            }

        }
        for (Element enclosedElement : classElement.getEnclosedElements()) {
            if (enclosedElement.getKind() == ElementKind.CONSTRUCTOR) {
                ExecutableElement constructorElement = (ExecutableElement) enclosedElement;
                if (constructorElement.getParameters().size() == 0 && constructorElement.getModifiers()
                        .contains(Modifier.PUBLIC)) {
                    // 可以有空构造实例化，确保能生成代码
                    return true;
                }
            }

        }
        //没有发现空构造
        error(classElement, "The class %s must provide an public empty default constructor",
                classElement.getQualifiedName().toString());
        return false;
    }
}
