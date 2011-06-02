// Copyright (c) 2011, QMetric Group Limited. All rights reserved.

package com.qmetric.hibernate.infrastructure.query;

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

    QueryableField fieldType() default QueryableField.GENERAL;

    boolean isCaseSensitive() default true;
}