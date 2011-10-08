// Copyright (c) 2011, QMetric Group Limited. All rights reserved.

package com.qmetric.hibernate.service;

import com.qmetric.hibernate.PersistenceStrategy;
import com.qmetric.hibernate.model.PersistentObjectStub;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Matchers;
import org.mockito.Mockito;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.orm.hibernate3.HibernateTemplate;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public final class PersistenceServiceTest
{
    private HibernateTemplate hibernateTemplate = mock(HibernateTemplate.class);

    private PersistenceServiceImpl persistenceService = new PersistenceServiceImpl(hibernateTemplate);

    private PersistentObjectStub persistenceStrategy;

    @Before
    public void context()
    {
        persistenceStrategy = new PersistentObjectStub();
    }

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
    public void shouldPersistWhenCreateIsEnabled()
    {
        persistenceStrategy.create = true;

        persistenceService.create(persistenceStrategy);

        verify(hibernateTemplate, times(1)).saveOrUpdate(persistenceStrategy);
    }

    @Test
    public void shouldFlushHibernateSession()
    {
        persistenceService.flush();

        verify(hibernateTemplate).flush();
    }

    @Test(expected = IllegalArgumentException.class)
    public void findByPrimaryKeyThrowsExceptionWithNullDaoClass()
    {
        persistenceService.findByPrimaryKey(null, "id", "1234");
    }

    @Test(expected = IllegalArgumentException.class)
    public void findByForeignKeyThrowsExceptionWithNullDaoClass()
    {
        final PersistenceStrategy foreignKeyObject = new PersistentObjectStub();
        persistenceService.findByForeignKey(null, "reference", foreignKeyObject);
    }

    @Test(expected = IllegalArgumentException.class)
    public void findByForeignKeyThrowsExceptionWithIncorrectForeignKeyReference()
    {
        final PersistenceStrategy foreignKeyObject = new PersistentObjectStub();
        persistenceService.findByForeignKey(PersistentObjectStub.class, "no-field-with-this-name", foreignKeyObject);
    }

    @Test(expected = IllegalArgumentException.class)
    public void findUniqueThrowsExceptionWithNullDaoClass()
    {
        persistenceService.findUnique(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void findCollectionThrowsExceptionWithNullDaoClass()
    {
        persistenceService.findCollection(null);
    }

    @Test
    public void findByPrimaryKey()
    {
        final PersistenceStrategy primaryObject = new PersistentObjectStub();

        when(hibernateTemplate.findByCriteria(Mockito.<DetachedCriteria>any())).thenReturn(Arrays.asList(primaryObject));

        final PersistenceStrategy dao = persistenceService.findByPrimaryKey(PersistentObjectStub.class, "id", "1234");

        verify(hibernateTemplate).findByCriteria(Mockito.<DetachedCriteria>any());
        assertThat(primaryObject, equalTo(dao));
    }

    @Test
    public void shouldSuccessfullyHandleNotFindingByPrimaryKey()
    {
        when(hibernateTemplate.findByCriteria(Mockito.<DetachedCriteria>any())).thenReturn(Collections.emptyList());

        final PersistenceStrategy dao = persistenceService.findByPrimaryKey(PersistentObjectStub.class, "id", "1234");

        verify(hibernateTemplate).findByCriteria(Mockito.<DetachedCriteria>any());
        assertNull(dao); // this show that it can return when list is null
    }

    @Test(expected = IncorrectResultSizeDataAccessException.class)
    public void shouldThrowUniqueExceptionWhenFindUniqueReturnsMoreThanOneResult()
    {
        when(hibernateTemplate.findByCriteria(Mockito.<DetachedCriteria>any())).thenReturn(Arrays.asList(new PersistentObjectStub(), new PersistentObjectStub()));

        persistenceService.findUnique(PersistentObjectStub.class);
    }

    @Test
    public void findByForeignKey()
    {
        final PersistenceStrategy foreignKeyRef = new PersistentObjectStub();
        final PersistenceStrategy primaryObject = new PersistentObjectStub(foreignKeyRef);

        when(hibernateTemplate.findByCriteria(Mockito.<DetachedCriteria>any())).thenReturn(Arrays.asList(primaryObject));

        final Collection<PersistenceStrategy> daos = persistenceService.findByForeignKey(PersistentObjectStub.class, "foreignKeyRef", foreignKeyRef);

        verify(hibernateTemplate).findByCriteria(Mockito.<DetachedCriteria>any());
        assertThat(daos.contains(primaryObject), equalTo(true));
    }

    @Test
    public void findUnique()
    {
        final PersistenceStrategy a = new PersistentObjectStub("a");
        when(hibernateTemplate.findByCriteria(Mockito.<DetachedCriteria>any())).thenReturn(Arrays.asList(a));

        final PersistenceStrategy dao = persistenceService.findUnique(PersistentObjectStub.class, Restrictions.eq("reference", "a"));

        verify(hibernateTemplate).findByCriteria(Mockito.<DetachedCriteria>any());
        assertThat(dao, equalTo(a));
    }

    @Test
    public void findCollection()
    {
        final PersistenceStrategy a = new PersistentObjectStub("a");
        final PersistenceStrategy b = new PersistentObjectStub("b");
        when(hibernateTemplate.findByCriteria(Mockito.<DetachedCriteria>any())).thenReturn(Arrays.asList(a));

        final Collection<PersistenceStrategy> daos = persistenceService.findCollection(PersistentObjectStub.class, Restrictions.eq("reference", "a"));

        verify(hibernateTemplate).findByCriteria(Mockito.<DetachedCriteria>any());
        assertThat(daos.contains(a), equalTo(true));
        assertThat(daos.contains(b), equalTo(false));
    }

    @Test
    public void fullTableScan()
    {
        final PersistenceStrategy a = new PersistentObjectStub();
        final PersistenceStrategy b = new PersistentObjectStub();
        final List<PersistenceStrategy> objectStubs = Arrays.asList(a, b);
        when(hibernateTemplate.findByCriteria(Mockito.<DetachedCriteria>any())).thenReturn(objectStubs);

        final Collection<PersistenceStrategy> daos = persistenceService.findCollection(PersistentObjectStub.class); // no criteria

        verify(hibernateTemplate).findByCriteria(Mockito.<DetachedCriteria>any());
        assertThat(daos.containsAll(objectStubs), equalTo(true));
    }
}