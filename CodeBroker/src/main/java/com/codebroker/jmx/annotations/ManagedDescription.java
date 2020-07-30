package com.codebroker.jmx.annotations;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * 管理BEAN描述注释。
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface ManagedDescription {

    String value();
}
