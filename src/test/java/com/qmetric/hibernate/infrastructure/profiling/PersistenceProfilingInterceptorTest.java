// Copyright (c) 2011, QMetric Group Limited. All rights reserved.

package com.qmetric.hibernate.infrastructure.profiling;

import com.qmetric.hibernate.PersistenceStrategy;
import com.qmetric.hibernate.infrastructure.model.AbstractPersistentObject;
import com.qmetric.utilities.time.DateTimeSource;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.log4j.Appender;
import org.apache.log4j.Logger;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.core.classloader.annotations.SuppressStaticInitializationFor;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.reflect.Whitebox;

import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(PowerMockRunner.class) @PrepareForTest({Appender.class, BeanUtils.class})
@SuppressStaticInitializationFor("com.qmetric.hibernate.infrastructure.profiling.PersistenceProfilingInterceptor")
public final class PersistenceProfilingInterceptorTest
{
    private final ProceedingJoinPoint mockCall = mock(ProceedingJoinPoint.class);

    private final Logger mockLogger = mock(Logger.class);

    private final Signature mockSignature = mock(Signature.class);

    private final PersistenceStrategy mockPersistenceStrategy = mock(PersistenceStrategy.class);

    private final DateTimeSource mockDateTimeSource = mock(DateTimeSource.class);

    private PersistenceProfilingInterceptor profiler = new PersistenceProfilingInterceptor(mockDateTimeSource);

    @Before
    public void initialise() throws Throwable
    {
        Whitebox.setInternalState(PersistenceProfilingInterceptor.class, mockLogger);

        when(mockCall.getSignature()).thenReturn(mockSignature);
        when(mockSignature.getDeclaringType()).thenReturn(Class.class);
        when(mockCall.proceed()).thenReturn(mockPersistenceStrategy);
    }

    @Test
    public void profileWriteDisabledTrigger() throws Throwable
    {
        when(mockLogger.isTraceEnabled()).thenReturn(false);

        profiler.profileWrites(mockCall, null);

        verify(mockCall).proceed();
        verify(mockLogger, never()).trace(anyString());
    }

    @Test
    public void profileReadDisabledTrigger() throws Throwable
    {
        when(mockLogger.isTraceEnabled()).thenReturn(false);

        profiler.profileReads(mockCall);

        verify(mockCall).proceed();
        verify(mockLogger, never()).trace(anyString());
    }

    @Test
    public void profileWriteNonProfilingDomainObject() throws Throwable
    {
        when(mockLogger.isTraceEnabled()).thenReturn(true);

        profiler.profileWrites(mockCall, new AbstractPersistentObject() {});

        verify(mockCall).proceed();
        verify(mockLogger, never()).trace(anyString());
    }

    @Test
    public void profileWriteProfilingDomainObject() throws Throwable
    {
        when(mockLogger.isTraceEnabled()).thenReturn(true);

        profiler.profileWrites(mockCall, new ProfiledDomainObject());

        verify(mockCall).proceed();
        verify(mockLogger).trace(anyString());
    }

    @Test
    public void profileReadProfilingDomainObject() throws Throwable
    {
        when(mockLogger.isTraceEnabled()).thenReturn(true);
        when(mockCall.proceed()).thenReturn(new ProfiledDomainObject());

        profiler.profileReads(mockCall);

        verify(mockCall).proceed();
        verify(mockLogger).trace(anyString());
    }

    @Test
    public void logReflectionErrors() throws Throwable
    {
        when(mockLogger.isTraceEnabled()).thenReturn(true);
        final PersistenceStrategy model = new ProfiledDomainObject();
        when(mockCall.proceed()).thenReturn(model);
        PowerMockito.mockStatic(BeanUtils.class);
        //noinspection ThrowableInstanceNeverThrown
        PowerMockito.when(BeanUtils.getProperty(model, "id")).thenThrow(new RuntimeException());

        profiler.profileReads(mockCall);

        verify(mockLogger, never()).trace(anyString());
        verify(mockLogger).error(anyString());
    }

    @PersistenceProfiled class ProfiledDomainObject extends AbstractPersistentObject
    {
        ProfiledDomainObject()
        {

        }
    }
}