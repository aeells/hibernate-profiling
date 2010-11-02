// Copyright (c) 2010, www.andrew-eells.com. All rights reserved.

package com.andrew_eells.persistence.service;

import com.andrew_eells.persistence.infrastructure.AbstractPersistentObject;
import com.andrew_eells.persistence.infrastructure.PersistenceStrategy;
import com.andrew_eells.persistence.infrastructure.query.QuerySpecification;
import org.hibernate.NonUniqueResultException;
import org.hibernate.criterion.DetachedCriteria;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Matchers;
import org.springframework.orm.hibernate3.HibernateTemplate;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.mockito.Mockito.*;

public final class PersistenceServiceTest {
    private HibernateTemplate hibernateTemplate = mock(HibernateTemplate.class);
    private QuerySpecificationTranslator translator = mock(QuerySpecificationTranslator.class);

    private PersistenceServiceImpl persistenceService = new PersistenceServiceImpl(hibernateTemplate, translator);
    private PersistentObjectStub persistenceStrategy;

    @Before
    public void context() {
        persistenceStrategy = new PersistentObjectStub();
    }

    @Test
    public void shouldNotFailWhenAttemptingToPersistNull() {
        persistenceService.create(null);

        verify(hibernateTemplate, times(0)).saveOrUpdate(Matchers.<Object>anyObject());
    }

    @Test
    public void shouldNotPersistWhenCreateIsDisabled() {
        persistenceStrategy.create = false;

        persistenceService.create(persistenceStrategy);

        verify(hibernateTemplate, times(0)).saveOrUpdate(persistenceStrategy);
    }

    @Test
    public void shouldDeleteWhenDeleteIsEnabled() {
        persistenceStrategy.delete = true;

        persistenceService.delete(persistenceStrategy);

        verify(hibernateTemplate, times(1)).delete(persistenceStrategy);
    }

    @Test
    public void shouldNotFailWhenAttemptingToDeleteNull() {
        persistenceService.delete(null);

        verify(hibernateTemplate, times(0)).delete(Matchers.<Object>anyObject());
    }

    @Test
    public void shouldNotDeleteWhenDeleteIsDisabled() {
        persistenceStrategy.delete = false;

        persistenceService.delete(persistenceStrategy);

        verify(hibernateTemplate, times(0)).delete(persistenceStrategy);
    }

    @Test
    public void shouldUpdateWhenUpdateIsEnabled() {
        persistenceStrategy.update = true;

        persistenceService.update(persistenceStrategy);

        verify(hibernateTemplate, times(1)).saveOrUpdate(persistenceStrategy);
    }

    @Test
    public void shouldNotFailWhenAttemptingToUpdateNull() {
        persistenceService.update(null);

        verify(hibernateTemplate, times(0)).saveOrUpdate(Matchers.<Object>anyObject());
    }

    @Test
    public void shouldNotUpdateWhenUpdateIsDisabled() {
        persistenceStrategy.update = false;

        persistenceService.update(persistenceStrategy);

        verify(hibernateTemplate, times(0)).saveOrUpdate(persistenceStrategy);
    }

    @Test
    public void shouldPersistWhenCreateIsEnabled() {
        persistenceStrategy.create = true;

        persistenceService.create(persistenceStrategy);

        verify(hibernateTemplate, times(1)).saveOrUpdate(persistenceStrategy);
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowExceptionWithNullSpecificationToReadUnique() {
        persistenceService.readUnique(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowExceptionWithNullSpecificationToReadList() {
        persistenceService.readList(null);
    }

    @Test
    public void shouldFindUniquePersistentObject() {

        QuerySpecification querySpecification = mock(QuerySpecification.class);

        DetachedCriteria criteria = mock(DetachedCriteria.class);
        when(translator.translateSpecification(querySpecification)).thenReturn(criteria);

        List itemInList = Arrays.asList(new PersistentObjectStub());

        when(hibernateTemplate.findByCriteria(criteria)).thenReturn(itemInList);

        persistenceService.readUnique(querySpecification);

        verify(hibernateTemplate).findByCriteria(criteria);

    }

    @Test
    public void shouldSuccessfullyHandleNotFindingUniqueEntity() {
        QuerySpecification querySpecification = mock(QuerySpecification.class);

        DetachedCriteria criteria = mock(DetachedCriteria.class);
        when(translator.translateSpecification(querySpecification)).thenReturn(criteria);

        List itemInList = Collections.emptyList();

        when(hibernateTemplate.findByCriteria(criteria)).thenReturn(itemInList);

        PersistenceStrategy result = persistenceService.readUnique(querySpecification);

        verify(hibernateTemplate).findByCriteria(criteria);

        assertThat(result, equalTo(null)); // this show that it can return when list is null
    }

    @Test(expected = NonUniqueResultException.class)
    public void shouldThrowUniqueExceptionWhenReadUniqueReturnsMoreThanOneResult() {
        QuerySpecification querySpecification = mock(QuerySpecification.class);

        DetachedCriteria criteria = mock(DetachedCriteria.class);
        when(translator.translateSpecification(querySpecification)).thenReturn(criteria);

        List itemInList = Arrays.asList(new PersistentObjectStub(), new PersistentObjectStub());

        when(hibernateTemplate.findByCriteria(criteria)).thenReturn(itemInList);

        persistenceService.readUnique(querySpecification);
    }

    @Test
    public void shouldSuccessfullyHandleListResult() {
        QuerySpecification querySpecification = mock(QuerySpecification.class);

        DetachedCriteria criteria = mock(DetachedCriteria.class);
        when(translator.translateSpecification(querySpecification)).thenReturn(criteria);

        List itemInList = Collections.emptyList();

        when(hibernateTemplate.findByCriteria(criteria)).thenReturn(itemInList);

        List<PersistenceStrategy> result = persistenceService.readList(querySpecification);

        verify(hibernateTemplate).findByCriteria(criteria);
    }

    private class PersistentObjectStub extends AbstractPersistentObject {

        public boolean create;
        public boolean update;
        public boolean delete;

        @Override
        public boolean isCreate() {
            return create;
        }

        @Override
        public boolean isUpdate() {
            return update;
        }

        @Override
        public boolean isDelete() {
            return delete;
        }
    }

}