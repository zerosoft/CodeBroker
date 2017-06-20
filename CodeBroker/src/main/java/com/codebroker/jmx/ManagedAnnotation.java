package com.codebroker.jmx;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * 管理Bean属性注释。
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface ManagedAnnotation {
	String value();

	boolean operation() default false;
}
