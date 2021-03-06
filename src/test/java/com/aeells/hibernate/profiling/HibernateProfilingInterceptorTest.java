/**
 * Copyright (c) 2012 Andrew Eells
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"),
 * to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense,
 * and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER
 * DEALINGS IN THE SOFTWARE.
 */

package com.aeells.hibernate.profiling;

import com.aeells.hibernate.model.AbstractPersistentObject;
import com.aeells.hibernate.model.PersistentObjectStub;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.log4j.Appender;
import org.apache.log4j.Logger;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.reflect.Whitebox;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.core.classloader.annotations.SuppressStaticInitializationFor;
import org.powermock.modules.junit4.PowerMockRunner;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(PowerMockRunner.class) @PrepareForTest({Appender.class, BeanUtils.class})
@SuppressStaticInitializationFor("com.aeells.hibernate.profiling.HibernateProfilingInterceptor")
public final class HibernateProfilingInterceptorTest
{
    private final ProceedingJoinPoint mockCall = mock(ProceedingJoinPoint.class);

    private final Logger mockLogger = mock(Logger.class);

    private final Signature mockSignature = mock(Signature.class);

    private final Object mockPersistenceStrategy = mock(Object.class);

    private HibernateProfilingInterceptor profiler = new HibernateProfilingInterceptor();

    @Before
    public void initialise() throws Throwable
    {
        Whitebox.setInternalState(HibernateProfilingInterceptor.class, mockLogger);

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
        final List<Object> models = new ArrayList<Object>()
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
        final Object model = new PersistentObjectStub();
        when(mockCall.proceed()).thenReturn(model);
        PowerMockito.mockStatic(BeanUtils.class);
        //noinspection ThrowableInstanceNeverThrown
        PowerMockito.when(BeanUtils.getProperty(model, "id")).thenThrow(new RuntimeException());

        profiler.profileFind(mockCall);

        verify(mockLogger, never()).trace(anyString());
        verify(mockLogger).error(anyString());
    }
}