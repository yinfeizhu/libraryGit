package org.fly.common.anno;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Target(ElementType.METHOD)//方法注解
@Retention(RUNTIME)//运行时注解
public @interface Log {
}
