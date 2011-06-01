// Copyright (c) 2011, www.andrew-eells.com. All rights reserved.

package com.andrew_eells.persistence.infrastructure.profiling;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Indicates this class will be intercepted and profiled when passed to the Persistence framework.
 */
@Documented
@Target({ElementType.TYPE, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface PersistenceProfiled
{
    String identifier() default "id";
}