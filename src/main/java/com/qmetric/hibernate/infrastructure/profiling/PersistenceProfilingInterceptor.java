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
import java.util.List;

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

    public Object profileRead(final ProceedingJoinPoint call) throws Throwable
    {
        if (LOGGER.isTraceEnabled())
        {
            final DateTime start = dateTimeSource.now();
            final PersistenceStrategy model = (PersistenceStrategy) call.proceed();
            logProfileCall(call, model, (new Duration(start, dateTimeSource.now()).getMillis()));
            return model;
        }
        else
        {
            return call.proceed();
        }
    }

    public Object profileReads(final ProceedingJoinPoint call) throws Throwable
    {
        if (LOGGER.isTraceEnabled())
        {
            final DateTime start = dateTimeSource.now();
            @SuppressWarnings({"unchecked"}) final List<PersistenceStrategy> models = (List<PersistenceStrategy>) call.proceed();
            logProfileCall(call, models, (new Duration(start, dateTimeSource.now()).getMillis()));
            return models;
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
            catch (final Exception e)
            {
                LOGGER.error("unable to profile class: " + model.getClass());
            }
        }
    }

    private void logProfileCall(final ProceedingJoinPoint call, final List<PersistenceStrategy> models, final long duration)
    {
        if (models != null && !models.isEmpty())
        {
            LOGGER.trace(new StringBuilder(StringUtils.substringBefore(call.getSignature().getDeclaringType().getSimpleName(), "$")).
                    append("|").append(call.getSignature().getName()).append("|").append(getClassNameAndPersistentIds(models)).append(duration));
        }
    }

    private String getClassNameAndPersistentId(final PersistenceStrategy model) throws InvocationTargetException, NoSuchMethodException, IllegalAccessException
    {
        final String identifier = model.getClass().getAnnotation(PersistenceProfiled.class).identifier();
        return new StringBuilder(model.getClass().getSimpleName()).append("|").append(BeanUtils.getProperty(model, identifier)).toString();
    }

    private String getClassNameAndPersistentIds(final List<PersistenceStrategy> models)
    {
        final StringBuilder sb = new StringBuilder();
        for (final PersistenceStrategy model : models)
        {
            if (model.getClass().isAnnotationPresent(PersistenceProfiled.class))
            {
                if (sb.length() == 0)
                {
                    sb.append(model.getClass().getSimpleName()).append("|");
                }

                try
                {
                    sb.append(BeanUtils.getProperty(model, model.getClass().getAnnotation(PersistenceProfiled.class).identifier())).append("|");
                }
                catch (final Exception e)
                {
                    LOGGER.error("unable to profile class: " + model.getClass());
                }
            }
        }

        return sb.toString();
    }
}