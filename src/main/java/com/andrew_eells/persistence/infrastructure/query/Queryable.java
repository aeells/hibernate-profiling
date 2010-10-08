// Copyright (c) 2010, www.andrew-eells.com. All rights reserved.

package com.andrew_eells.persistence.infrastructure.query;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Indicates an object field that is queryable.
 */
@Documented
@Target({ElementType.FIELD, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface Queryable
{
    String value();

    boolean isCaseSensitive() default true;
}