// Copyright (c) 2011, QMetric Group Limited. All rights reserved.

package com.qmetric.hibernate.profiling;

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
public final class HibernateProfilingInterceptor
{
    private static final Logger LOGGER = Logger.getLogger(HibernateProfilingInterceptor.class);

    private final DateTimeSource dateTimeSource;

    @Autowired public HibernateProfilingInterceptor(final DateTimeSource dateTimeSource)
    {
        this.dateTimeSource = dateTimeSource;
    }

    public void profileWrites(final ProceedingJoinPoint call, final Object model) throws Throwable
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

    public Object profileFind(final ProceedingJoinPoint call) throws Throwable
    {
        if (LOGGER.isTraceEnabled())
        {
            final DateTime start = dateTimeSource.now();
            final Object model = call.proceed();
            logProfileCall(call, model, (new Duration(start, dateTimeSource.now()).getMillis()));
            return model;
        }
        else
        {
            return call.proceed();
        }
    }

    public Object profileFindList(final ProceedingJoinPoint call) throws Throwable
    {
        if (LOGGER.isTraceEnabled())
        {
            final DateTime start = dateTimeSource.now();
            @SuppressWarnings({"unchecked"}) final List<Object> models = (List<Object>) call.proceed();
            logProfileCall(call, models, (new Duration(start, dateTimeSource.now()).getMillis()));
            return models;
        }
        else
        {
            return call.proceed();
        }
    }

    private void logProfileCall(final ProceedingJoinPoint call, final Object model, final long duration)
    {
        // model is null on login
        if (model != null && model.getClass().isAnnotationPresent(HibernateProfiled.class))
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

    private void logProfileCall(final ProceedingJoinPoint call, final List<Object> models, final long duration)
    {
        if (models != null && !models.isEmpty())
        {
            LOGGER.trace(new StringBuilder(StringUtils.substringBefore(call.getSignature().getDeclaringType().getSimpleName(), "$")).
                    append("|").append(call.getSignature().getName()).append("|").append(getClassNameAndPersistentIds(models)).append(duration));
        }
    }

    private String getClassNameAndPersistentId(final Object model) throws InvocationTargetException, NoSuchMethodException, IllegalAccessException
    {
        final String identifier = model.getClass().getAnnotation(HibernateProfiled.class).identifier();
        return new StringBuilder(model.getClass().getSimpleName()).append("|").append(BeanUtils.getProperty(model, identifier)).toString();
    }

    private String getClassNameAndPersistentIds(final List<Object> models)
    {
        final StringBuilder sb = new StringBuilder();
        for (final Object model : models)
        {
            if (model.getClass().isAnnotationPresent(HibernateProfiled.class))
            {
                if (sb.length() == 0)
                {
                    sb.append(model.getClass().getSimpleName()).append("|");
                }

                try
                {
                    sb.append(BeanUtils.getProperty(model, model.getClass().getAnnotation(HibernateProfiled.class).identifier())).append("|");
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