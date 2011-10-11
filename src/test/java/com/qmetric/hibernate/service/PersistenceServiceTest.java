// Copyright (c) 2011, QMetric Group Limited. All rights reserved.

package com.qmetric.hibernate.service;

import com.qmetric.hibernate.model.PersistentObjectStub;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.junit.Test;
import org.mockito.Matchers;
import org.mockito.Mockito;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.orm.hibernate3.HibernateTemplate;

import java.util.Arrays;

import static org.hibernate.criterion.DetachedCriteria.forClass;
import static org.hibernate.criterion.Restrictions.eq;
import static org.junit.Assert.assertNull;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public final class PersistenceServiceTest
{
    private HibernateTemplate hibernateTemplate = mock(HibernateTemplate.class);

    private PersistenceServiceImpl persistenceService = new PersistenceServiceImpl(hibernateTemplate);

    private PersistentObjectStub persistenceStrategy = new PersistentObjectStub();

    @Test
    public void shouldNotFailWhenAttemptingToPersistNull()
    {
        persistenceService.create(null);

        verify(hibernateTemplate, times(0)).saveOrUpdate(Matchers.<Object>anyObject());
    }

    @Test
    public void shouldNotPersistWhenCreateIsDisabled()
    {
        persistenceStrategy.create = false;

        persistenceService.create(persistenceStrategy);

        verify(hibernateTemplate, times(0)).saveOrUpdate(persistenceStrategy);
    }

    @Test
    public void shouldPersistWhenCreateIsEnabled()
    {
        persistenceStrategy.create = true;

        persistenceService.create(persistenceStrategy);

        verify(hibernateTemplate, times(1)).saveOrUpdate(persistenceStrategy);
    }

    @Test
    public void shouldUpdateWhenUpdateIsEnabled()
    {
        persistenceStrategy.update = true;

        persistenceService.update(persistenceStrategy);

        verify(hibernateTemplate, times(1)).saveOrUpdate(persistenceStrategy);
    }

    @Test
    public void shouldNotFailWhenAttemptingToUpdateNull()
    {
        persistenceService.update(null);

        verify(hibernateTemplate, times(0)).saveOrUpdate(Matchers.<Object>anyObject());
    }

    @Test
    public void shouldNotUpdateWhenUpdateIsDisabled()
    {
        persistenceStrategy.update = false;

        persistenceService.update(persistenceStrategy);

        verify(hibernateTemplate, times(0)).saveOrUpdate(persistenceStrategy);
    }

    @Test
    public void shouldMergeWhenObjectPresentInSessionCache()
    {
        persistenceStrategy.update = true;
        when(hibernateTemplate.contains(persistenceStrategy)).thenReturn(true);

        persistenceService.update(persistenceStrategy);

        verify(hibernateTemplate).merge(persistenceStrategy);
    }

    @Test
    public void shouldDeleteWhenDeleteIsEnabled()
    {
        persistenceStrategy.delete = true;

        persistenceService.delete(persistenceStrategy);

        verify(hibernateTemplate, times(1)).delete(persistenceStrategy);
    }

    @Test
    public void shouldNotFailWhenAttemptingToDeleteNull()
    {
        persistenceService.delete(null);

        verify(hibernateTemplate, times(0)).delete(Matchers.<Object>anyObject());
    }

    @Test
    public void shouldNotDeleteWhenDeleteIsDisabled()
    {
        persistenceStrategy.delete = false;

        persistenceService.delete(persistenceStrategy);

        verify(hibernateTemplate, times(0)).delete(persistenceStrategy);
    }

    @Test
    public void shouldFlushHibernateSession()
    {
        persistenceService.flush();

        verify(hibernateTemplate).flush();
    }

    @Test(expected = IllegalArgumentException.class)
    public void findByIdThrowsExceptionWithNullDaoClass()
    {
        persistenceService.findById(null, "1234");
    }

    @Test
    public void findById()
    {
        persistenceService.findById(PersistentObjectStub.class, "1234");

        verify(hibernateTemplate).get(PersistentObjectStub.class, "1234");
    }

    @Test
    public void findByIdShouldSuccessfullyHandleNotFindingAnything()
    {
        //noinspection unchecked
        when(hibernateTemplate.get(Mockito.<Class>any(), anyString())).thenReturn(null);

        assertNull(persistenceService.findById(PersistentObjectStub.class, "1234"));
        verify(hibernateTemplate).get(PersistentObjectStub.class, "1234");
    }

    @Test(expected = Exception.class)
    public void findUniqueShouldThrowExceptionWithNullDaoClass()
    {
        persistenceService.findUnique(null);
    }

    @Test(expected = IncorrectResultSizeDataAccessException.class)
    public void findUniqueShouldThrowUniqueExceptionWhenReturningMoreThanOneResult()
    {
        when(hibernateTemplate.findByCriteria(Mockito.<DetachedCriteria>any())).thenReturn(Arrays.asList(new PersistentObjectStub(), new PersistentObjectStub()));

        persistenceService.findUnique(forClass(PersistentObjectStub.class));
    }

    @Test
    public void findUnique()
    {
        final DetachedCriteria criteria = forClass(PersistentObjectStub.class).add(eq("fieldName", "a"));

        persistenceService.findUnique(criteria);

        verify(hibernateTemplate).findByCriteria(criteria);
    }

    @Test
    public void findFirstOrderedBy()
    {
        final DetachedCriteria criteria = forClass(PersistentObjectStub.class).addOrder(Order.asc("fieldName"));
        persistenceService.findFirstOrderedBy(criteria);

        verify(hibernateTemplate).findByCriteria(criteria, 0, 1);
    }

    @Test(expected = Exception.class)
    public void findShouldThrowExceptionWithNullDaoClass()
    {
        persistenceService.find(null);
    }

    @Test
    public void find()
    {
        final DetachedCriteria criteria = forClass(PersistentObjectStub.class).add(eq("fieldName", "a"));

        persistenceService.find(criteria);

        verify(hibernateTemplate).findByCriteria(criteria);
    }

    @Test
    public void findLimit()
    {
        final DetachedCriteria criteria = forClass(PersistentObjectStub.class).add(eq("fieldName", "a"));

        persistenceService.find(criteria, 10, 20);

        verify(hibernateTemplate).findByCriteria(criteria, 10, 20);
    }
}