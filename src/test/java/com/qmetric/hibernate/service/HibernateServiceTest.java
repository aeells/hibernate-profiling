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

public final class HibernateServiceTest
{
    private HibernateTemplate hibernateTemplate = mock(HibernateTemplate.class);

    private HibernateServiceImpl hibernateService = new HibernateServiceImpl(hibernateTemplate);

    private PersistentObjectStub stub = new PersistentObjectStub();

    @Test
    public void shouldNotFailWhenAttemptingToPersistNull()
    {
        hibernateService.create(null);

        verify(hibernateTemplate, times(0)).saveOrUpdate(Matchers.<Object>anyObject());
    }

    @Test
    public void shouldNotPersistWhenCreateIsDisabled()
    {
        stub.create = false;

        hibernateService.create(stub);

        verify(hibernateTemplate, times(0)).saveOrUpdate(stub);
    }

    @Test
    public void shouldPersistWhenCreateIsEnabled()
    {
        stub.create = true;

        hibernateService.create(stub);

        verify(hibernateTemplate, times(1)).saveOrUpdate(stub);
    }

    @Test
    public void shouldUpdateWhenUpdateIsEnabled()
    {
        stub.update = true;

        hibernateService.update(stub);

        verify(hibernateTemplate, times(1)).saveOrUpdate(stub);
    }

    @Test
    public void shouldNotFailWhenAttemptingToUpdateNull()
    {
        hibernateService.update(null);

        verify(hibernateTemplate, times(0)).saveOrUpdate(Matchers.<Object>anyObject());
    }

    @Test
    public void shouldNotUpdateWhenUpdateIsDisabled()
    {
        stub.update = false;

        hibernateService.update(stub);

        verify(hibernateTemplate, times(0)).saveOrUpdate(stub);
    }

    @Test
    public void shouldMergeWhenObjectPresentInSessionCache()
    {
        stub.update = true;
        when(hibernateTemplate.contains(stub)).thenReturn(true);

        hibernateService.update(stub);

        verify(hibernateTemplate).merge(stub);
    }

    @Test
    public void shouldDeleteWhenDeleteIsEnabled()
    {
        stub.delete = true;

        hibernateService.delete(stub);

        verify(hibernateTemplate, times(1)).delete(stub);
    }

    @Test
    public void shouldNotFailWhenAttemptingToDeleteNull()
    {
        hibernateService.delete(null);

        verify(hibernateTemplate, times(0)).delete(Matchers.<Object>anyObject());
    }

    @Test
    public void shouldNotDeleteWhenDeleteIsDisabled()
    {
        stub.delete = false;

        hibernateService.delete(stub);

        verify(hibernateTemplate, times(0)).delete(stub);
    }

    @Test(expected = IllegalArgumentException.class)
    public void findByIdThrowsExceptionWithNullDaoClass()
    {
        hibernateService.findById(null, "1234");
    }

    @Test
    public void findById()
    {
        hibernateService.findById(PersistentObjectStub.class, "1234");

        verify(hibernateTemplate).get(PersistentObjectStub.class, "1234");
    }

    @Test
    public void findByIdShouldSuccessfullyHandleNotFindingAnything()
    {
        //noinspection unchecked
        when(hibernateTemplate.get(Mockito.<Class>any(), anyString())).thenReturn(null);

        assertNull(hibernateService.findById(PersistentObjectStub.class, "1234"));
        verify(hibernateTemplate).get(PersistentObjectStub.class, "1234");
    }

    @Test(expected = Exception.class)
    public void findUniqueShouldThrowExceptionWithNullDaoClass()
    {
        hibernateService.findUnique(null);
    }

    @Test(expected = IncorrectResultSizeDataAccessException.class)
    public void findUniqueShouldThrowUniqueExceptionWhenReturningMoreThanOneResult()
    {
        when(hibernateTemplate.findByCriteria(Mockito.<DetachedCriteria>any())).thenReturn(Arrays.asList(new PersistentObjectStub(), new PersistentObjectStub()));

        hibernateService.findUnique(forClass(PersistentObjectStub.class));
    }

    @Test
    public void findUnique()
    {
        final DetachedCriteria criteria = forClass(PersistentObjectStub.class).add(eq("fieldName", "a"));

        hibernateService.findUnique(criteria);

        verify(hibernateTemplate).findByCriteria(criteria);
    }

    @Test
    public void findFirstOrderedBy()
    {
        final DetachedCriteria criteria = forClass(PersistentObjectStub.class).addOrder(Order.asc("fieldName"));
        hibernateService.findFirstOrderedBy(criteria);

        verify(hibernateTemplate).findByCriteria(criteria, 0, 1);
    }

    @Test(expected = Exception.class)
    public void findShouldThrowExceptionWithNullDaoClass()
    {
        hibernateService.find(null);
    }

    @Test
    public void find()
    {
        final DetachedCriteria criteria = forClass(PersistentObjectStub.class).add(eq("fieldName", "a"));

        hibernateService.find(criteria);

        verify(hibernateTemplate).findByCriteria(criteria);
    }

    @Test
    public void findLimit()
    {
        final DetachedCriteria criteria = forClass(PersistentObjectStub.class).add(eq("fieldName", "a"));

        hibernateService.find(criteria, 10, 20);

        verify(hibernateTemplate).findByCriteria(criteria, 10, 20);
    }
}