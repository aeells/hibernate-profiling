// Copyright (c) 2011, QMetric Group Limited. All rights reserved.

package com.qmetric.hibernate.infrastructure.profiling;

import com.qmetric.hibernate.PersistenceStrategy;
import com.qmetric.utilities.time.DateTimeSource;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.aspectj.lang.ProceedingJoinPoint;
import org.joda.time.DateTime;
import org.joda.time.Duration;
import org.springframework.beans.factory.annotation.Autowired;

import java.lang.reflect.InvocationTargetException;

// Log4j configuration (TRACE) triggers whether performance statistics are recorded
public final class PersistenceProfilingInterceptor
{
    private static final Logger LOGGER = Logger.getLogger(PersistenceProfilingInterceptor.class);

    private final DateTimeSource dateTimeSource;

    @Autowired public PersistenceProfilingInterceptor(final DateTimeSource dateTimeSource)
    {
        this.dateTimeSource = dateTimeSource;
    }

    public void profileWrites(final ProceedingJoinPoint call, final PersistenceStrategy model) throws Throwable
    {
        if (LOGGER.isTraceEnabled())
        {
            final DateTime start = dateTimeSource.now();
            call.proceed();
            logProfileCall(call, model, (new Duration(start, dateTimeSource.now()).getMillis()));
        }
        else
        {
            call.proceed();
        }
    }

    public Object profileReads(final ProceedingJoinPoint call) throws Throwable
    {
        if (LOGGER.isTraceEnabled())
        {
            final DateTime start = dateTimeSource.now();
            PersistenceStrategy model = (PersistenceStrategy) call.proceed();
            logProfileCall(call, model, (new Duration(start, dateTimeSource.now()).getMillis()));
            return model;
        }
        else
        {
            return call.proceed();
        }
    }

    private void logProfileCall(final ProceedingJoinPoint call, final PersistenceStrategy model, final long duration)
    {
        // model is null on login
        if (model != null && model.getClass().isAnnotationPresent(PersistenceProfiled.class))
        {
            try
            {
                LOGGER.trace(new StringBuilder(StringUtils.substringBefore(call.getSignature().getDeclaringType().getSimpleName(), "$")).append("|").
                        append(call.getSignature().getName()).append("|").append(getClassNameAndPersistentId(model)).append("|").append(duration));
            }
            catch (Exception e)
            {
                LOGGER.error("unable to profile class: " + model.getClass());
            }
        }
    }

    private String getClassNameAndPersistentId(final PersistenceStrategy model) throws InvocationTargetException, NoSuchMethodException, IllegalAccessException
    {
        final String identifier = model.getClass().getAnnotation(PersistenceProfiled.class).identifier();
        return new StringBuilder(model.getClass().getSimpleName()).append("|").append(BeanUtils.getProperty(model, identifier)).toString();
    }
}