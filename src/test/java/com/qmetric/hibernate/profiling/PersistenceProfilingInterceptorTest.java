// Copyright (c) 2011, QMetric Group Limited. All rights reserved.

package com.qmetric.hibernate.profiling;

import com.qmetric.hibernate.PersistenceStrategy;
import com.qmetric.hibernate.model.AbstractPersistentObject;
import com.qmetric.hibernate.model.PersistentObjectStub;
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

import java.util.ArrayList;
import java.util.List;

import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(PowerMockRunner.class) @PrepareForTest({Appender.class, BeanUtils.class})
@SuppressStaticInitializationFor("com.qmetric.hibernate.profiling.PersistenceProfilingInterceptor")
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
    public void profileFindDisabledTrigger() throws Throwable
    {
        when(mockLogger.isTraceEnabled()).thenReturn(false);

        profiler.profileFind(mockCall);
        profiler.profileFindList(mockCall);

        verify(mockCall, times(2)).proceed();
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

        profiler.profileWrites(mockCall, new PersistentObjectStub());

        verify(mockCall).proceed();
        verify(mockLogger).trace(anyString());
    }

    @Test
    public void profileFindProfilingDomainObject() throws Throwable
    {
        when(mockLogger.isTraceEnabled()).thenReturn(true);
        when(mockCall.proceed()).thenReturn(new PersistentObjectStub());

        profiler.profileFind(mockCall);

        verify(mockCall).proceed();
        verify(mockLogger).trace(anyString());
    }

    @Test
    public void profileFindListProfilingDomainObjectList() throws Throwable
    {
        when(mockLogger.isTraceEnabled()).thenReturn(true);
        final List<PersistenceStrategy> models = new ArrayList<PersistenceStrategy>()
        {
            {
                this.add(new PersistentObjectStub());
            }
        };

        when(mockCall.proceed()).thenReturn(models);

        profiler.profileFindList(mockCall);

        verify(mockCall).proceed();
        verify(mockLogger).trace(anyString());
    }

    @Test
    public void logReflectionErrors() throws Throwable
    {
        when(mockLogger.isTraceEnabled()).thenReturn(true);
        final PersistenceStrategy model = new PersistentObjectStub();
        when(mockCall.proceed()).thenReturn(model);
        PowerMockito.mockStatic(BeanUtils.class);
        //noinspection ThrowableInstanceNeverThrown
        PowerMockito.when(BeanUtils.getProperty(model, "id")).thenThrow(new RuntimeException());

        profiler.profileFind(mockCall);

        verify(mockLogger, never()).trace(anyString());
        verify(mockLogger).error(anyString());
    }
}