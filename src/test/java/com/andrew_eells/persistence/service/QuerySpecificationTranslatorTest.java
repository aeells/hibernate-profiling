package com.andrew_eells.persistence.service;

import com.andrew_eells.persistence.infrastructure.model.AbstractPersistentObject;
import com.andrew_eells.persistence.infrastructure.query.QueryClause;
import com.andrew_eells.persistence.infrastructure.query.QueryClauseOperator;
import com.andrew_eells.persistence.infrastructure.query.QuerySpecification;
import com.andrew_eells.persistence.infrastructure.query.QuerySpecificationImpl;
import com.andrew_eells.persistence.infrastructure.query.QuerySpecificationOperator;
import com.andrew_eells.persistence.infrastructure.query.QueryTypeImpl;
import com.andrew_eells.persistence.infrastructure.query.Queryable;
import com.andrew_eells.persistence.infrastructure.query.SortKeyInfo;
import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Disjunction;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.hibernate.criterion.SimpleExpression;
import org.hibernate.impl.CriteriaImpl;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;

/**
 * The tests are based on toString signatures because Hibernate doesn't implement hashcode/equals, but does implement toString on their objects.
 */
public class QuerySpecificationTranslatorTest
{
    private QuerySpecificationTranslator translator;

    private static final String CUSTOMER_ID = "12345678";

    private static final String CUSTOMER_EMAIL = "firstname.lastname@andrew-eells.com";

    private static final Order NO_ORDER = null;

    private static final SortKeyInfo NO_SORT = null;

    @Before
    public void context()
    {
        translator = new QuerySpecificationTranslator();
    }

    @Test
    public void shouldTranslateEntityClassThroughToCriteria()
    {
        // preConditions
        DetachedCriteria actualDetachedCriteria = singleFieldPreConditionWith(NO_SORT);

        // expected value
        CriteriaImpl expectedCriteria = singleFieldDetachedCriteriaBasedOnEqualsWith(NO_ORDER);

        // call
        CriteriaImpl actualCriteria = (CriteriaImpl) actualDetachedCriteria.getExecutableCriteria(null);

        //assertion
        assertThat(actualCriteria.getEntityOrClassName(), equalTo(expectedCriteria.getEntityOrClassName()));
    }

    @Test
    public void shouldProduceCriteriaBasedOnEqualsOperator()
    {
        // preConditions
        DetachedCriteria actualDetachedCriteria = singleFieldPreConditionWith(NO_SORT);

        // expected value
        CriteriaImpl expectedCriteria = singleFieldDetachedCriteriaBasedOnEqualsWith(NO_ORDER);

        // call
        callClassAndAssert(actualDetachedCriteria, expectedCriteria);
    }

    @Test
    public void shouldProduceCriteriaBasedOnAndOperator()
    {
        // preConditions
        DetachedCriteria actualDetachedCriteria = multipleFieldPreConditionsWith(QuerySpecificationOperator.AND);

        // expected value
        DetachedCriteria expectedDetachedCriteria = DetachedCriteria.forEntityName(PersistentObjectStub.class.getName());
        expectedDetachedCriteria.add(Restrictions.eq(QueryTypeImpl.CUSTOMER_QUERY.getFieldName(), CUSTOMER_ID));
        expectedDetachedCriteria.add(Restrictions.eq(QueryTypeImpl.EMAIL_ADDRESS_QUERY.getFieldName(), CUSTOMER_EMAIL));
        CriteriaImpl expectedCriteria = (CriteriaImpl) expectedDetachedCriteria.getExecutableCriteria(null);

        // call
        callClassAndAssert(actualDetachedCriteria, expectedCriteria);
    }

    @Test
    public void shouldProduceCriteriaBasedOnOrOperator()
    {
        // preConditions
        DetachedCriteria actualDetachedCriteria = multipleFieldPreConditionsWith(QuerySpecificationOperator.OR);

        // expected value
        CriteriaImpl expectedCriteria = multipleFieldDetachedCriteriaBasedOnOrCondition();

        // call
        callClassAndAssert(actualDetachedCriteria, expectedCriteria);
    }

    @Test
    public void shouldProduceACriteriaBasedOnGreaterThanOperator()
    {
        // preConditions
        DetachedCriteria actualDetachedCriteria = singleFieldPreConditionBasedOn(QueryClauseOperator.GT);

        // expected value
        CriteriaImpl expectedCriteria = singleFieldDetachedCriteriaBasedOnGreaterThan();

        // call
        callClassAndAssert(actualDetachedCriteria, expectedCriteria);
    }

    @Test
    public void shouldProduceACriteriaBasedOnLessThanOperator()
    {
        // preConditions
        DetachedCriteria actualDetachedCriteria = singleFieldPreConditionBasedOn(QueryClauseOperator.LT);

        // expected value
        CriteriaImpl expectedCriteria = singleFieldDetachedCriteriaBasedOnLessThan();

        // call
        callClassAndAssert(actualDetachedCriteria, expectedCriteria);
    }

    @Test
    public void shouldProduceACriteriaBasedOnNotEqualOperator()
    {
        // preConditions
        DetachedCriteria actualDetachedCriteria = singleFieldPreConditionBasedOn(QueryClauseOperator.NOT_EQ);

        // expected value
        CriteriaImpl expectedCriteria = singleFieldDetachedCriteriaBasedOnNotEqual();

        // call
        callClassAndAssert(actualDetachedCriteria, expectedCriteria);
    }

    @Test
    public void shouldProduceACriteriaBasedOnGreaterThanEqualOperator()
    {
        // preConditions
        DetachedCriteria actualDetachedCriteria = singleFieldPreConditionBasedOn(QueryClauseOperator.GT_OR_EQ);

        // expected value
        CriteriaImpl expectedCriteria = singleFieldDetachedCriteriaBasedOnGreaterOrEqualTo();

        // call
        callClassAndAssert(actualDetachedCriteria, expectedCriteria);
    }

    @Test
    public void shouldProduceACriteriaBasedOnLessThanEqualOperator()
    {
        // preConditions
        DetachedCriteria actualDetachedCriteria = singleFieldPreConditionBasedOn(QueryClauseOperator.LT_OR_EQ);

        // expected value
        CriteriaImpl expectedCriteria = singleFieldDetachedCriteriaBasedOnLessOrEqualTo();

        callClassAndAssert(actualDetachedCriteria, expectedCriteria);
    }

    @Test
    public void shouldProduceACriteriaWithAscendingOrder()
    {
        // preConditions
        DetachedCriteria actualDetachedCriteria = singleFieldPreConditionWith(SortKeyInfo.ascending(QueryTypeImpl.CUSTOMER_QUERY.getFieldName()));

        // expected value
        CriteriaImpl expectedCriteria = singleFieldDetachedCriteriaBasedOnEqualsWith(Order.asc(QueryTypeImpl.CUSTOMER_QUERY.getFieldName()));

        // call
        CriteriaImpl actualCriteria = (CriteriaImpl) actualDetachedCriteria.getExecutableCriteria(null);

        // assertion
        assertThat(actualCriteria.iterateOrderings(), hasSameToStringSignatureOf(expectedCriteria.iterateOrderings()));
    }

    @Test
    public void shouldProduceACriteriaWithDescendingOrder()
    {
        // preConditions
        DetachedCriteria actualDetachedCriteria = singleFieldPreConditionWith(SortKeyInfo.descending(QueryTypeImpl.CUSTOMER_QUERY.getFieldName()));

        // expected value
        CriteriaImpl expectedCriteria = singleFieldDetachedCriteriaBasedOnEqualsWith(Order.desc(QueryTypeImpl.CUSTOMER_QUERY.getFieldName()));

        // call
        CriteriaImpl actualCriteria = (CriteriaImpl) actualDetachedCriteria.getExecutableCriteria(null);

        // assertion
        assertThat(actualCriteria.iterateOrderings(), hasSameToStringSignatureOf(expectedCriteria.iterateOrderings()));
    }

    private void callClassAndAssert(DetachedCriteria actualDetachedCriteria, CriteriaImpl expectedCriteria)
    {
        // call
        CriteriaImpl actualCriteria = (CriteriaImpl) actualDetachedCriteria.getExecutableCriteria(null);

        // assertion
        assertThat(actualCriteria.iterateExpressionEntries(), hasSameToStringSignatureOf(expectedCriteria.iterateExpressionEntries()));
    }

    private DetachedCriteria singleFieldPreConditionBasedOn(QueryClauseOperator operator)
    {
        QuerySpecification querySpecification =
                new QuerySpecificationImpl(PersistentObjectStub.class, new QueryClause(QueryTypeImpl.CUSTOMER_QUERY, CUSTOMER_ID, operator));

        return translator.translate(querySpecification);
    }

    private DetachedCriteria singleFieldPreConditionWith(SortKeyInfo sortKeyInfo)
    {
        QuerySpecification querySpecification;
        if (sortKeyInfo != null)
        {
            querySpecification = new QuerySpecificationImpl(PersistentObjectStub.class,
                                                            new QueryClause(QueryTypeImpl.CUSTOMER_QUERY, CUSTOMER_ID, QueryClauseOperator.EQ), sortKeyInfo);
        }
        else
        {
            querySpecification = new QuerySpecificationImpl(PersistentObjectStub.class,
                                                            new QueryClause(QueryTypeImpl.CUSTOMER_QUERY, CUSTOMER_ID, QueryClauseOperator.EQ));
        }

        return translator.translate(querySpecification);
    }

    private DetachedCriteria multipleFieldPreConditionsWith(QuerySpecificationOperator querySpecificationOperator)
    {
        final QueryClause nameQueryClause = new QueryClause(QueryTypeImpl.CUSTOMER_QUERY, CUSTOMER_ID, QueryClauseOperator.EQ);
        final QueryClause emailQueryClause = new QueryClause(QueryTypeImpl.EMAIL_ADDRESS_QUERY, CUSTOMER_EMAIL, QueryClauseOperator.EQ);
        final List<QueryClause> queryClauses = Arrays.asList(nameQueryClause, emailQueryClause);

        final QuerySpecification querySpecification = new QuerySpecificationImpl(PersistentObjectStub.class, queryClauses, querySpecificationOperator);

        return translator.translate(querySpecification);
    }

    private CriteriaImpl singleFieldDetachedCriteriaWith(Order order, SimpleExpression expression)
    {
        DetachedCriteria expectedDetachedCriteria = DetachedCriteria.forEntityName(PersistentObjectStub.class.getName());
        expectedDetachedCriteria.add(expression);
        CriteriaImpl expectedCriteria = (CriteriaImpl) expectedDetachedCriteria.getExecutableCriteria(null);

        if (order != null)
        {
            expectedCriteria.addOrder(order);
        }

        return expectedCriteria;
    }

    private CriteriaImpl singleFieldDetachedCriteriaBasedOnEqualsWith(Order order)
    {
        return singleFieldDetachedCriteriaWith(order, Restrictions.eq(QueryTypeImpl.CUSTOMER_QUERY.getFieldName(), CUSTOMER_ID));
    }

    private CriteriaImpl singleFieldDetachedCriteriaBasedOnGreaterThan()
    {
        return singleFieldDetachedCriteriaWith(Order.desc(QueryTypeImpl.CUSTOMER_QUERY.getFieldName()), Restrictions.gt(QueryTypeImpl.CUSTOMER_QUERY.getFieldName(), CUSTOMER_ID));
    }

    private CriteriaImpl singleFieldDetachedCriteriaBasedOnLessThan()
    {
        return singleFieldDetachedCriteriaWith(Order.desc(QueryTypeImpl.CUSTOMER_QUERY.getFieldName()), Restrictions.lt(QueryTypeImpl.CUSTOMER_QUERY.getFieldName(), CUSTOMER_ID));
    }

    private CriteriaImpl singleFieldDetachedCriteriaBasedOnNotEqual()
    {
        return singleFieldDetachedCriteriaWith(Order.desc(QueryTypeImpl.CUSTOMER_QUERY.getFieldName()), Restrictions.ne(QueryTypeImpl.CUSTOMER_QUERY.getFieldName(), CUSTOMER_ID));
    }

    private CriteriaImpl singleFieldDetachedCriteriaBasedOnLessOrEqualTo()
    {
        return singleFieldDetachedCriteriaWith(Order.desc(QueryTypeImpl.CUSTOMER_QUERY.getFieldName()), Restrictions.le(QueryTypeImpl.CUSTOMER_QUERY.getFieldName(), CUSTOMER_ID));
    }

    private CriteriaImpl singleFieldDetachedCriteriaBasedOnGreaterOrEqualTo()
    {
        return singleFieldDetachedCriteriaWith(Order.desc(QueryTypeImpl.CUSTOMER_QUERY.getFieldName()), Restrictions.ge(QueryTypeImpl.CUSTOMER_QUERY.getFieldName(), CUSTOMER_ID));
    }

    private CriteriaImpl multipleFieldDetachedCriteriaBasedOnOrCondition()
    {
        DetachedCriteria expectedDetachedCriteria = DetachedCriteria.forEntityName(PersistentObjectStub.class.getName());
        final Disjunction disjunction = Restrictions.disjunction();
        disjunction.add(Restrictions.eq(QueryTypeImpl.CUSTOMER_QUERY.getFieldName(), CUSTOMER_ID));
        disjunction.add(Restrictions.eq(QueryTypeImpl.EMAIL_ADDRESS_QUERY.getFieldName(), CUSTOMER_EMAIL));
        expectedDetachedCriteria.add(disjunction);

        return (CriteriaImpl) expectedDetachedCriteria.getExecutableCriteria(null);
    }

    private Matcher<Iterator> hasSameToStringSignatureOf(final Iterator expected)
    {

        return new BaseMatcher<Iterator>()
        {
            private List actualList;

            private List expectedList;

            @Override
            public boolean matches(Object actual)
            {

                actualList = createListFrom((Iterator) actual);
                expectedList = createListFrom(expected);

                return actualList.toString().equals(expectedList.toString());
            }

            @Override
            public void describeTo(Description description)
            {

                description.appendText("The toString of ");
                description.appendValue(actualList);
                description.appendText(" did not match that of ");
                description.appendValue(expectedList);
            }

            private List createListFrom(Iterator iterator)
            {
                final List result = new ArrayList();
                while (iterator.hasNext())
                {
                    //noinspection unchecked
                    result.add(iterator.next());
                }

                return result;
            }
        };
    }

    class PersistentObjectStub extends AbstractPersistentObject
    {
        @Queryable(value = QueryTypeImpl.PK_ID, isCaseSensitive = true)
        public String id;

        @Queryable(value = QueryTypeImpl.CUSTOMER_ID, isCaseSensitive = true)
        public String customerId;

        @Queryable(value = QueryTypeImpl.EMAIL_ADDRESS, isCaseSensitive = false)
        public String email;
    }
}
