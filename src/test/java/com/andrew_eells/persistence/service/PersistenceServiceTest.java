// Copyright (c) 2010, www.andrew-eells.com. All rights reserved.

package com.andrew_eells.persistence.service;

import com.andrew_eells.persistence.infrastructure.AbstractPersistentObjectImpl;
import com.andrew_eells.persistence.infrastructure.PersistenceSessionFactory;
import com.andrew_eells.persistence.infrastructure.PersistenceStrategy;
import com.andrew_eells.persistence.infrastructure.query.QueryClause;
import com.andrew_eells.persistence.infrastructure.query.QueryClauseOperator;
import com.andrew_eells.persistence.infrastructure.query.QuerySpecification;
import com.andrew_eells.persistence.infrastructure.query.QuerySpecificationImpl;
import com.andrew_eells.persistence.infrastructure.query.QuerySpecificationOperator;
import com.andrew_eells.persistence.infrastructure.query.QueryType;
import com.andrew_eells.persistence.infrastructure.query.Queryable;
import com.andrew_eells.persistence.infrastructure.query.SortKeyInfo;
import junit.framework.Assert;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Disjunction;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.SimpleExpression;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public final class PersistenceServiceTest
{
    private PersistenceSessionFactory mockSessionFactory = mock(PersistenceSessionFactory.class);

    private Session mockSession = mock(Session.class);

    private Criteria mockCriteria = mock(Criteria.class);

    private PersistenceServiceImpl persistenceService = new PersistenceServiceImpl(mockSessionFactory);

    public PersistenceServiceTest()
    {
        when(mockSessionFactory.getSession()).thenReturn(mockSession);
    }

    @Test
    public void createNull()
    {
        final MockPersistentObjectImpl mock = null;

        persistenceService.create(mock);

        verify(mockSessionFactory, times(0)).getSession();
        verify(mockSession, times(0)).saveOrUpdate(mock);
    }

    @Test
    public void createDisabled()
    {
        final MockPersistentObjectImpl mock = new MockPersistentObjectImpl();
        // do not set create to true

        persistenceService.create(mock);

        verify(mockSessionFactory, times(0)).getSession();
        verify(mockSession, times(0)).saveOrUpdate(mock);
    }

    @Test
    public void createEnabled()
    {
        final MockPersistentObjectImpl mock = new MockPersistentObjectImpl();
        mock.enableCreate();

        persistenceService.create(mock);

        verify(mockSessionFactory, times(1)).getSession();
        verify(mockSession, times(1)).saveOrUpdate(mock);
    }

    @Test(expected = IllegalArgumentException.class)
    public void readUniqueNull()
    {
        persistenceService.readUnique(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void readUniqueAndWithoutQueryParams()
    {
        final MockPersistentObjectImpl mock = Mockito.spy(new MockPersistentObjectImpl());

        when(mockSession.createCriteria(MockPersistentObjectImpl.class)).thenReturn(mockCriteria);
        when(mockCriteria.uniqueResult()).thenReturn(mock);

        final QuerySpecification querySpec = new QuerySpecificationImpl(MockPersistentObjectImpl.class, new ArrayList<QueryClause>(), QuerySpecificationOperator.AND);

        final PersistenceStrategy strategy = persistenceService.readUnique(querySpec);

        assertEquals("Unexpected persistence strategy obj", mock, strategy);

        verify(mockCriteria, times(0)).add(any(Criterion.class));
    }

    @Test(expected = IllegalArgumentException.class)
    public void readUniqueOrWithoutQueryParams()
    {
        final MockPersistentObjectImpl mock = Mockito.spy(new MockPersistentObjectImpl());

        when(mockSession.createCriteria(MockPersistentObjectImpl.class)).thenReturn(mockCriteria);
        when(mockCriteria.uniqueResult()).thenReturn(mock);

        final QuerySpecification querySpec = new QuerySpecificationImpl(MockPersistentObjectImpl.class, new ArrayList<QueryClause>(), QuerySpecificationOperator.OR);

        final PersistenceStrategy strategy = persistenceService.readUnique(querySpec);
    }

    @Test
    public void readUniqueWithSingularParamPassedToQuerySpecificationConstructor()
    {
        final MockPersistentObjectImpl mock = Mockito.spy(new MockPersistentObjectImpl());

        when(mockSession.createCriteria(MockPersistentObjectImpl.class)).thenReturn(mockCriteria);
        when(mockCriteria.uniqueResult()).thenReturn(mock);

        final QuerySpecification querySpec = new QuerySpecificationImpl(MockPersistentObjectImpl.class, new QueryClause(QueryType.CUSTOMER_ID, "12345678", QueryClauseOperator.EQ));

        final PersistenceStrategy strategy = persistenceService.readUnique(querySpec);

        assertEquals("Unexpected persistence strategy obj", mock, strategy);

        final ArgumentCaptor<SimpleExpression> captor = ArgumentCaptor.forClass(SimpleExpression.class);
        verify(mockCriteria).add(captor.capture());
        final SimpleExpression simpleExpression = captor.getValue();
        assertEquals("unexpected expression", "testCaseSensitiveCustomerId=12345678", simpleExpression.toString());
    }

    @Test
    public void readUniqueAndWithCaseSensitiveQueryParam()
    {
        final MockPersistentObjectImpl mock = Mockito.spy(new MockPersistentObjectImpl());

        when(mockSession.createCriteria(MockPersistentObjectImpl.class)).thenReturn(mockCriteria);
        when(mockCriteria.uniqueResult()).thenReturn(mock);

        QueryClause nameQueryClause = new QueryClause(QueryType.CUSTOMER_ID, "12345678", QueryClauseOperator.EQ);
        QueryClause emailQueryClause = new QueryClause(QueryType.EMAIL_ADDRESS, "firstname.lastname@andrew-eells.com", QueryClauseOperator.EQ);

        List<QueryClause> queryClauses = Arrays.asList(nameQueryClause, emailQueryClause);

        final QuerySpecification querySpec = new QuerySpecificationImpl(MockPersistentObjectImpl.class, queryClauses, QuerySpecificationOperator.AND);

        final PersistenceStrategy strategy = persistenceService.readUnique(querySpec);

        assertEquals("Unexpected persistence strategy obj", mock, strategy);

        final ArgumentCaptor<SimpleExpression> captor = ArgumentCaptor.forClass(SimpleExpression.class);

        verify(mockCriteria, times(2)).add(captor.capture());
        final List<SimpleExpression> expressions = captor.getAllValues();

        Assert.assertEquals("testCaseSensitiveCustomerId=12345678", expressions.get(0).toString());
        Assert.assertEquals("testCaseInSensitiveEmailAddress=firstname.lastname@andrew-eells.com", expressions.get(1).toString());

        Assert.assertFalse("Ignore case value expected", (Boolean) ReflectionTestUtils.getField(expressions.get(0), "ignoreCase"));
        Assert.assertTrue("Ignore Case value not expected", (Boolean) ReflectionTestUtils.getField(expressions.get(1), "ignoreCase"));
    }

    @Test
    public void readUniqueOrWithCaseSensitiveQueryParam()
    {
        final MockPersistentObjectImpl mock = Mockito.spy(new MockPersistentObjectImpl());

        when(mockSession.createCriteria(MockPersistentObjectImpl.class)).thenReturn(mockCriteria);
        when(mockCriteria.uniqueResult()).thenReturn(mock);

        QueryClause nameQueryClause = new QueryClause(QueryType.CUSTOMER_ID, "12345678", QueryClauseOperator.EQ);
        QueryClause emailQueryClause = new QueryClause(QueryType.EMAIL_ADDRESS, "firstname.lastname@andrew-eells.com", QueryClauseOperator.EQ);

        List<QueryClause> queryClauses = Arrays.asList(nameQueryClause, emailQueryClause);

        final QuerySpecification querySpec = new QuerySpecificationImpl(MockPersistentObjectImpl.class, queryClauses, QuerySpecificationOperator.OR);

        final PersistenceStrategy strategy = persistenceService.readUnique(querySpec);

        assertEquals("Unexpected persistence strategy obj", mock, strategy);

        final ArgumentCaptor<Disjunction> captor = ArgumentCaptor.forClass(Disjunction.class);
        verify(mockCriteria).add(captor.capture());
        final Disjunction disjunction = captor.getValue();
        assertEquals("unexpected expression", "(testCaseSensitiveCustomerId=12345678 or testCaseInSensitiveEmailAddress=firstname.lastname@andrew-eells.com)",
                     disjunction.toString());
        Assert.assertFalse("Ignore case value expected",
                           (Boolean) ReflectionTestUtils.getField(((List) ReflectionTestUtils.getField(disjunction, "criteria")).get(0), "ignoreCase"));
        Assert.assertTrue("Ignore Case value not expected",
                          (Boolean) ReflectionTestUtils.getField(((List) ReflectionTestUtils.getField(disjunction, "criteria")).get(1), "ignoreCase"));
    }

    @Test
    public void readUniqueAndWithCaseInSensitiveQueryParam()
    {
        final MockPersistentObjectImpl mock = Mockito.spy(new MockPersistentObjectImpl());

        when(mockSession.createCriteria(MockPersistentObjectImpl.class)).thenReturn(mockCriteria);
        when(mockCriteria.uniqueResult()).thenReturn(mock);

        List<QueryClause> queryClauses = Arrays.asList(new QueryClause(QueryType.EMAIL_ADDRESS, "a-user@andrew_eells.com", QueryClauseOperator.EQ));

        final QuerySpecification querySpec = new QuerySpecificationImpl(MockPersistentObjectImpl.class, queryClauses, QuerySpecificationOperator.AND);

        final PersistenceStrategy strategy = persistenceService.readUnique(querySpec);

        assertEquals("Unexpected persistence strategy obj", mock, strategy);

        final ArgumentCaptor<SimpleExpression> captor = ArgumentCaptor.forClass(SimpleExpression.class);
        verify(mockCriteria).add(captor.capture());
        final SimpleExpression simpleExpression = captor.getValue();
        assertEquals("unexpected expression", "testCaseInSensitiveEmailAddress=a-user@andrew_eells.com", simpleExpression.toString());
        Assert.assertTrue("Ignore case value unexpected", (Boolean) ReflectionTestUtils.getField(simpleExpression, "ignoreCase"));
    }

    @Test
    public void readUniqueWithSortAsc()
    {
        final MockPersistentObjectImpl mock = Mockito.spy(new MockPersistentObjectImpl());

        when(mockSession.createCriteria(MockPersistentObjectImpl.class)).thenReturn(mockCriteria);
        when(mockCriteria.uniqueResult()).thenReturn(mock);

        final QuerySpecification querySpec = new QuerySpecificationImpl(MockPersistentObjectImpl.class, new QueryClause(QueryType.CUSTOMER_ID, "12345678", QueryClauseOperator.EQ),
                                                                        SortKeyInfo.ascending("testCaseSensitiveCustomerId"));

        final PersistenceStrategy strategy = persistenceService.readUnique(querySpec);

        assertEquals("Unexpected persistence strategy obj", mock, strategy);

        final ArgumentCaptor<Order> captor = ArgumentCaptor.forClass(Order.class);
        verify(mockCriteria).addOrder(captor.capture());
        final Order order = captor.getValue();
        assertEquals("unexpected expression", "testCaseSensitiveCustomerId asc", order.toString());
    }

    @Test
    public void readUniqueWithSortDesc()
    {
        final MockPersistentObjectImpl mock = Mockito.spy(new MockPersistentObjectImpl());

        when(mockSession.createCriteria(MockPersistentObjectImpl.class)).thenReturn(mockCriteria);
        when(mockCriteria.uniqueResult()).thenReturn(mock);

        final QuerySpecification querySpec = new QuerySpecificationImpl(MockPersistentObjectImpl.class, new QueryClause(QueryType.CUSTOMER_ID, "12345678", QueryClauseOperator.EQ),
                                                                        SortKeyInfo.descending("testCaseSensitiveCustomerId"));

        final PersistenceStrategy strategy = persistenceService.readUnique(querySpec);

        assertEquals("Unexpected persistence strategy obj", mock, strategy);

        final ArgumentCaptor<Order> captor = ArgumentCaptor.forClass(Order.class);
        verify(mockCriteria).addOrder(captor.capture());
        final Order order = captor.getValue();
        assertEquals("unexpected expression", "testCaseSensitiveCustomerId desc", order.toString());
    }

    @Test(expected = IllegalArgumentException.class)
    public void readListNull()
    {
        persistenceService.readList(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void readListAndWithoutQueryParams()
    {
        final List<MockPersistentObjectImpl> mock = Arrays.asList(Mockito.spy(new MockPersistentObjectImpl()));

        when(mockSession.createCriteria(MockPersistentObjectImpl.class)).thenReturn(mockCriteria);
        when(mockCriteria.list()).thenReturn(mock);

        final QuerySpecification querySpec = new QuerySpecificationImpl(MockPersistentObjectImpl.class, new ArrayList<QueryClause>(), QuerySpecificationOperator.AND);

        final List<PersistenceStrategy> strategies = persistenceService.readList(querySpec);
    }

    @Test(expected = IllegalArgumentException.class)
    public void readListOrWithoutQueryParams()
    {
        final List<MockPersistentObjectImpl> mock = Arrays.asList(Mockito.spy(new MockPersistentObjectImpl()));

        when(mockSession.createCriteria(MockPersistentObjectImpl.class)).thenReturn(mockCriteria);
        when(mockCriteria.list()).thenReturn(mock);

        final QuerySpecification querySpec = new QuerySpecificationImpl(MockPersistentObjectImpl.class, new ArrayList<QueryClause>(), QuerySpecificationOperator.OR);

        final List<PersistenceStrategy> strategies = persistenceService.readList(querySpec);
    }

    @Test
    public void readListAndWithCaseSensitiveQueryParam()
    {
        final List<MockPersistentObjectImpl> mock = Arrays.asList(Mockito.spy(new MockPersistentObjectImpl()));

        when(mockSession.createCriteria(MockPersistentObjectImpl.class)).thenReturn(mockCriteria);
        when(mockCriteria.list()).thenReturn(mock);

        List<QueryClause> queryClauses = Arrays.asList(new QueryClause(QueryType.CUSTOMER_ID, "12345678", QueryClauseOperator.EQ));

        final QuerySpecification querySpec = new QuerySpecificationImpl(MockPersistentObjectImpl.class, queryClauses, QuerySpecificationOperator.AND);

        final List<PersistenceStrategy> strategies = persistenceService.readList(querySpec);

        assertEquals("Unexpected persistence strategy obj", mock.get(0), strategies.get(0));

        final ArgumentCaptor<SimpleExpression> captor = ArgumentCaptor.forClass(SimpleExpression.class);
        verify(mockCriteria).add(captor.capture());
        final SimpleExpression simpleExpression = captor.getValue();
        assertEquals("unexpected expression", "testCaseSensitiveCustomerId=12345678", simpleExpression.toString());
        Assert.assertFalse("Ignore case value unexpected", (Boolean) ReflectionTestUtils.getField(simpleExpression, "ignoreCase"));
    }

    @Test
    public void readListWithSingularParamPassedToQuerySpecificationConstructor()
    {
        final List<MockPersistentObjectImpl> mock = Arrays.asList(Mockito.spy(new MockPersistentObjectImpl()));

        when(mockSession.createCriteria(MockPersistentObjectImpl.class)).thenReturn(mockCriteria);
        when(mockCriteria.list()).thenReturn(mock);

        final QuerySpecification querySpec = new QuerySpecificationImpl(MockPersistentObjectImpl.class, new QueryClause(QueryType.CUSTOMER_ID, "12345678", QueryClauseOperator.EQ));

        final List<PersistenceStrategy> strategies = persistenceService.readList(querySpec);

        assertEquals("Unexpected persistence strategy obj", mock.get(0), strategies.get(0));

        final ArgumentCaptor<SimpleExpression> captor = ArgumentCaptor.forClass(SimpleExpression.class);
        verify(mockCriteria).add(captor.capture());
        final SimpleExpression simpleExpression = captor.getValue();
        assertEquals("unexpected expression", "testCaseSensitiveCustomerId=12345678", simpleExpression.toString());
    }

    @Test
    public void readListOrWithCaseSensitiveQueryParam()
    {
        final List<MockPersistentObjectImpl> mock = Arrays.asList(Mockito.spy(new MockPersistentObjectImpl()));

        when(mockSession.createCriteria(MockPersistentObjectImpl.class)).thenReturn(mockCriteria);
        when(mockCriteria.list()).thenReturn(mock);

        QueryClause nameQueryClause = new QueryClause(QueryType.CUSTOMER_ID, "12345678", QueryClauseOperator.EQ);
        QueryClause emailQueryClause = new QueryClause(QueryType.EMAIL_ADDRESS, "firstname.lastname@andrew-eells.com", QueryClauseOperator.EQ);

        List<QueryClause> queryClauses = Arrays.asList(nameQueryClause, emailQueryClause);

        final QuerySpecification querySpec = new QuerySpecificationImpl(MockPersistentObjectImpl.class, queryClauses, QuerySpecificationOperator.OR);

        final List<PersistenceStrategy> strategies = persistenceService.readList(querySpec);

        assertEquals("Unexpected persistence strategy obj", mock.get(0), strategies.get(0));

        final ArgumentCaptor<Disjunction> captor = ArgumentCaptor.forClass(Disjunction.class);
        verify(mockCriteria).add(captor.capture());
        final Disjunction disjunction = captor.getValue();
        assertEquals("unexpected expression", "(testCaseSensitiveCustomerId=12345678 or testCaseInSensitiveEmailAddress=firstname.lastname@andrew-eells.com)",
                     disjunction.toString());
        Assert.assertFalse("Ignore case value not expected",
                           (Boolean) ReflectionTestUtils.getField(((List) ReflectionTestUtils.getField(disjunction, "criteria")).get(0), "ignoreCase"));
        Assert.assertTrue("Ignore case value expected",
                          (Boolean) ReflectionTestUtils.getField(((List) ReflectionTestUtils.getField(disjunction, "criteria")).get(1), "ignoreCase"));
    }

    @Test
    public void readListAndWithCaseInSensitiveQueryParam()
    {
        final List<MockPersistentObjectImpl> mock = Arrays.asList(Mockito.spy(new MockPersistentObjectImpl()));

        when(mockSession.createCriteria(MockPersistentObjectImpl.class)).thenReturn(mockCriteria);
        when(mockCriteria.list()).thenReturn(mock);

        final List<QueryClause> queryClauses = Arrays.asList(new QueryClause(QueryType.EMAIL_ADDRESS, "a-user@andrew_eells.com", QueryClauseOperator.EQ));

        final QuerySpecification querySpec = new QuerySpecificationImpl(MockPersistentObjectImpl.class, queryClauses, QuerySpecificationOperator.AND);

        final List<PersistenceStrategy> strategies = persistenceService.readList(querySpec);

        assertEquals("Unexpected persistence strategy obj", mock.get(0), strategies.get(0));

        final ArgumentCaptor<SimpleExpression> captor = ArgumentCaptor.forClass(SimpleExpression.class);
        verify(mockCriteria).add(captor.capture());
        final SimpleExpression simpleExpression = captor.getValue();
        assertEquals("unexpected expression", "testCaseInSensitiveEmailAddress=a-user@andrew_eells.com", simpleExpression.toString());
        Assert.assertTrue("Ignore case value unexpected", (Boolean) ReflectionTestUtils.getField(simpleExpression, "ignoreCase"));
    }

    @Test
    public void readListWithSortAsc()
    {
        final List<MockPersistentObjectImpl> mock = Arrays.asList(Mockito.spy(new MockPersistentObjectImpl()));

        when(mockSession.createCriteria(MockPersistentObjectImpl.class)).thenReturn(mockCriteria);
        when(mockCriteria.list()).thenReturn(mock);

        final QuerySpecification querySpec =
                new QuerySpecificationImpl(MockPersistentObjectImpl.class, new QueryClause(QueryType.CUSTOMER_ID, "12345678", QueryClauseOperator.EQ), SortKeyInfo.ascending("testCaseSensitiveCustomerId"));

        final List<PersistenceStrategy> strategies = persistenceService.readList(querySpec);

        assertEquals("Unexpected persistence strategy obj", mock.get(0), strategies.get(0));

        final ArgumentCaptor<Order> captor = ArgumentCaptor.forClass(Order.class);
        verify(mockCriteria).addOrder(captor.capture());
        final Order order = captor.getValue();
        assertEquals("unexpected expression", "testCaseSensitiveCustomerId asc", order.toString());
    }

    @Test
    public void readListWithSortDesc()
    {
        final List<MockPersistentObjectImpl> mock = Arrays.asList(Mockito.spy(new MockPersistentObjectImpl()));

        when(mockSession.createCriteria(MockPersistentObjectImpl.class)).thenReturn(mockCriteria);
        when(mockCriteria.list()).thenReturn(mock);

        final QuerySpecification querySpec =
                new QuerySpecificationImpl(MockPersistentObjectImpl.class, new QueryClause(QueryType.CUSTOMER_ID, "12345678", QueryClauseOperator.EQ), SortKeyInfo.descending("testCaseSensitiveCustomerId"));

        final List<PersistenceStrategy> strategies = persistenceService.readList(querySpec);

        assertEquals("Unexpected persistence strategy obj", mock.get(0), strategies.get(0));

        final ArgumentCaptor<Order> captor = ArgumentCaptor.forClass(Order.class);
        verify(mockCriteria).addOrder(captor.capture());
        final Order order = captor.getValue();
        assertEquals("unexpected expression", "testCaseSensitiveCustomerId desc", order.toString());
    }

    @Test
    public void readListAndWithEQOperator()
    {
        List<SimpleExpression> expressions = readListAndWithSpecifiedOperator(QueryClauseOperator.EQ);

        Assert.assertEquals("testCaseSensitiveCustomerId=12345678", expressions.get(0).toString());
        Assert.assertEquals("testCaseInSensitiveEmailAddress=firstname.lastname@andrew-eells.com", expressions.get(1).toString());
    }

    @Test
    public void readListAndWithGTOperator()
    {
        List<SimpleExpression> expressions = readListAndWithSpecifiedOperator(QueryClauseOperator.GT);

        Assert.assertEquals("testCaseSensitiveCustomerId>12345678", expressions.get(0).toString());
        Assert.assertEquals("testCaseInSensitiveEmailAddress>firstname.lastname@andrew-eells.com", expressions.get(1).toString());
    }

    @Test
    public void readListAndWithLTOperator()
    {
        List<SimpleExpression> expressions = readListAndWithSpecifiedOperator(QueryClauseOperator.LT);

        Assert.assertEquals("testCaseSensitiveCustomerId<12345678", expressions.get(0).toString());
        Assert.assertEquals("testCaseInSensitiveEmailAddress<firstname.lastname@andrew-eells.com", expressions.get(1).toString());
    }

    @Test
    public void readListAndWithGTorEQOperator()
    {
        List<SimpleExpression> expressions = readListAndWithSpecifiedOperator(QueryClauseOperator.GT_OR_EQ);

        Assert.assertEquals("testCaseSensitiveCustomerId>=12345678", expressions.get(0).toString());
        Assert.assertEquals("testCaseInSensitiveEmailAddress>=firstname.lastname@andrew-eells.com", expressions.get(1).toString());
    }

    @Test
    public void readListAndWithLTorEQOperator()
    {
        List<SimpleExpression> expressions = readListAndWithSpecifiedOperator(QueryClauseOperator.LT_OR_EQ);

        Assert.assertEquals("testCaseSensitiveCustomerId<=12345678", expressions.get(0).toString());
        Assert.assertEquals("testCaseInSensitiveEmailAddress<=firstname.lastname@andrew-eells.com", expressions.get(1).toString());
    }

    @Test
    public void readListAndWithNotEQOperator()
    {
        List<SimpleExpression> expressions = readListAndWithSpecifiedOperator(QueryClauseOperator.NOT_EQ);

        Assert.assertEquals("testCaseSensitiveCustomerId<>12345678", expressions.get(0).toString());
        Assert.assertEquals("testCaseInSensitiveEmailAddress<>firstname.lastname@andrew-eells.com", expressions.get(1).toString());
    }

    @Test
    public void readListOrWithDifferentConstraintOperators()
    {
        final List<MockPersistentObjectImpl> mock = Arrays.asList(Mockito.spy(new MockPersistentObjectImpl()));

        when(mockSession.createCriteria(MockPersistentObjectImpl.class)).thenReturn(mockCriteria);
        when(mockCriteria.list()).thenReturn(mock);

        QueryClause nameQueryClause = new QueryClause(QueryType.CUSTOMER_ID, "12345678", QueryClauseOperator.GT);
        QueryClause emailQueryClause = new QueryClause(QueryType.EMAIL_ADDRESS, "firstname.lastname@andrew-eells.com", QueryClauseOperator.NOT_EQ);

        List<QueryClause> queryClauses = Arrays.asList(nameQueryClause, emailQueryClause);

        final QuerySpecification querySpec = new QuerySpecificationImpl(MockPersistentObjectImpl.class, queryClauses, QuerySpecificationOperator.OR);

        final List<PersistenceStrategy> strategies = persistenceService.readList(querySpec);

        assertEquals("Unexpected persistence strategy obj", mock.get(0), strategies.get(0));

        final ArgumentCaptor<Disjunction> captor = ArgumentCaptor.forClass(Disjunction.class);
        verify(mockCriteria).add(captor.capture());
        final Disjunction disjunction = captor.getValue();
        assertEquals("unexpected expression", "(testCaseSensitiveCustomerId>12345678 or testCaseInSensitiveEmailAddress<>firstname.lastname@andrew-eells.com)",
                     disjunction.toString());
        Assert.assertFalse("Ignore case value not expected",
                           (Boolean) ReflectionTestUtils.getField(((List) ReflectionTestUtils.getField(disjunction, "criteria")).get(0), "ignoreCase"));
        Assert.assertTrue("Ignore case value expected",
                          (Boolean) ReflectionTestUtils.getField(((List) ReflectionTestUtils.getField(disjunction, "criteria")).get(1), "ignoreCase"));
    }

    @Test
    public void deleteNull()
    {
        final MockPersistentObjectImpl mock = null;

        persistenceService.delete(mock);

        verify(mockSessionFactory, times(0)).getSession();
        verify(mockSession, times(0)).delete(mock);
    }

    @Test
    public void deleteDisabled()
    {
        final MockPersistentObjectImpl mock = new MockPersistentObjectImpl();
        // do not set delete to true

        persistenceService.delete(mock);

        verify(mockSessionFactory, times(0)).getSession();
        verify(mockSession, times(0)).delete(mock);
    }

    @Test
    public void deleteEnabled()
    {
        final MockPersistentObjectImpl mock = new MockPersistentObjectImpl();
        mock.enableDelete();

        persistenceService.delete(mock);

        verify(mockSessionFactory, times(1)).getSession();
        verify(mockSession, times(1)).delete(mock);
    }

    @Test
    public void updateNull()
    {
        final MockPersistentObjectImpl mock = null;

        final PersistenceStrategy obj = persistenceService.update(mock);
        assertNull("update should return null object!", obj);

        verify(mockSessionFactory, times(0)).getSession();
        verify(mockSession, times(0)).merge(mock);
    }

    @Test
    public void updateDisabled()
    {
        final MockPersistentObjectImpl mock = new MockPersistentObjectImpl();
        // do not set update to true

        final PersistenceStrategy obj = persistenceService.update(mock);
        assertNull("update should return null object!", obj);

        verify(mockSessionFactory, times(0)).getSession();
        verify(mockSession, times(0)).merge(mock);
    }

    @Test
    public void updateEnabled()
    {
        final MockPersistentObjectImpl mock = Mockito.spy(new MockPersistentObjectImpl());
        mock.enableUpdate();

        when(mockSession.merge(mock)).thenReturn(mock);

        final MockPersistentObjectImpl obj = (MockPersistentObjectImpl) persistenceService.update(mock);
        assertEquals("update should return original object!", mock, obj);

        verify(mockSessionFactory, times(1)).getSession();
        verify(mockSession, times(1)).merge(mock);
        verify(mock, times(1)).setLastModified((Date) anyObject());
    }

    private List<SimpleExpression> readListAndWithSpecifiedOperator(QueryClauseOperator queryClauseOperator)
    {
        final MockPersistentObjectImpl mock = Mockito.spy(new MockPersistentObjectImpl());

        when(mockSession.createCriteria(MockPersistentObjectImpl.class)).thenReturn(mockCriteria);
        when(mockCriteria.uniqueResult()).thenReturn(mock);

        QueryClause nameQueryClause = new QueryClause(QueryType.CUSTOMER_ID, "12345678", queryClauseOperator);
        QueryClause emailQueryClause = new QueryClause(QueryType.EMAIL_ADDRESS, "firstname.lastname@andrew-eells.com", queryClauseOperator);

        List<QueryClause> queryClauses = Arrays.asList(nameQueryClause, emailQueryClause);

        final QuerySpecification querySpec = new QuerySpecificationImpl(MockPersistentObjectImpl.class, queryClauses, QuerySpecificationOperator.AND);

        final PersistenceStrategy strategy = persistenceService.readUnique(querySpec);

        assertEquals("Unexpected persistence strategy obj", mock, strategy);

        final ArgumentCaptor<SimpleExpression> captor = ArgumentCaptor.forClass(SimpleExpression.class);

        verify(mockCriteria, times(2)).add(captor.capture());

        List<SimpleExpression> expressions = captor.getAllValues();

        Assert.assertFalse("Ignore case value expected", (Boolean) ReflectionTestUtils.getField(expressions.get(0), "ignoreCase"));
        Assert.assertTrue("Ignore Case value not expected", (Boolean) ReflectionTestUtils.getField(expressions.get(1), "ignoreCase"));

        return expressions;
    }

    class MockPersistentObjectImpl extends AbstractPersistentObjectImpl
    {
        @Queryable(value = QueryType.CUSTOMER_ID, isCaseSensitive = true)
        public String testCaseSensitiveCustomerId;

        @Queryable(value = QueryType.EMAIL_ADDRESS, isCaseSensitive = false)
        public String testCaseInSensitiveEmailAddress;

        public boolean create;

        public boolean update;

        public boolean delete;

        public void enableCreate()
        {
            this.create = true;
        }

        @Override public boolean isCreate()
        {
            return create;
        }

        public void enableUpdate()
        {
            this.update = true;
        }

        @Override public boolean isUpdate()
        {
            return update;
        }

        public void enableDelete()
        {
            this.delete = true;
        }

        @Override public boolean isDelete()
        {
            return delete;
        }
    }
}