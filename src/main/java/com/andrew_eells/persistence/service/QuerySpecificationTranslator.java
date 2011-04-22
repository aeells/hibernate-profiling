package com.andrew_eells.persistence.service;

import com.andrew_eells.persistence.infrastructure.query.QueryClauseOperator;
import com.andrew_eells.persistence.infrastructure.query.QueryKeyInfo;
import com.andrew_eells.persistence.infrastructure.query.QuerySpecification;
import com.andrew_eells.persistence.infrastructure.query.SortKeyInfo;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Disjunction;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.hibernate.criterion.SimpleExpression;

import java.util.Map;

public class QuerySpecificationTranslator
{

    public DetachedCriteria translate(final QuerySpecification querySpecification)
    {
        final Map<QueryKeyInfo, Object> queryParams = querySpecification.getQueryParams();

        DetachedCriteria queryCriteria = DetachedCriteria.forClass(querySpecification.getPersistentClass());

        switch (querySpecification.getQueryOperator())
        {
            case NONE:
                translateQueryParamsForAndOperator(queryParams, queryCriteria);
                break;
            case AND:
                translateQueryParamsForAndOperator(queryParams, queryCriteria);
                break;
            case OR:
                translateQueryParamsForOrOperator(queryParams, queryCriteria);
                break;
        }

        if (querySpecification.getSortKey() != null && querySpecification.getSortKey().getOrder() != SortKeyInfo.Order.NONE)
        {
            final SortKeyInfo sortKey = querySpecification.getSortKey();
            queryCriteria.addOrder(sortKey.getOrder() == SortKeyInfo.Order.ASC ? Order.asc(sortKey.getField()) : Order.desc(sortKey.getField()));
        }

        return queryCriteria;
    }

    private DetachedCriteria translateQueryParamsForAndOperator(final Map<QueryKeyInfo, Object> queryParams, final DetachedCriteria queryCriteria)
    {
        for (final QueryKeyInfo keyInfo : queryParams.keySet())
        {
            final Object value = queryParams.get(keyInfo);

            final Criterion expression = expressionForOperator(keyInfo.getOperator(), keyInfo.getKey(), value);
            // ensure value is not null otherwise hibernate will throw an NPE
            if (value != null && !keyInfo.isCaseSensitive() && expression instanceof SimpleExpression)
            {
                ((SimpleExpression) expression).ignoreCase();
            }

            queryCriteria.add(expression);
        }

        return queryCriteria;
    }

    private DetachedCriteria translateQueryParamsForOrOperator(final Map<QueryKeyInfo, Object> queryParams, final DetachedCriteria queryCriteria)
    {
        final Disjunction disjunction = Restrictions.disjunction();

        for (final QueryKeyInfo keyInfo : queryParams.keySet())
        {
            final Object value = queryParams.get(keyInfo);

            final Criterion expression = expressionForOperator(keyInfo.getOperator(), keyInfo.getKey(), value);
            // ensure value is not null otherwise hibernate will throw an NPE
            if (value != null && !keyInfo.isCaseSensitive() && expression instanceof SimpleExpression)
            {
                ((SimpleExpression) expression).ignoreCase();
            }

            disjunction.add(expression);
        }

        queryCriteria.add(disjunction);

        return queryCriteria;
    }

    private Criterion expressionForOperator(final QueryClauseOperator operator, final String propertyName, final Object value)
    {
        Criterion expression = null;
        switch (operator)
        {
            case EQ:
                expression = Restrictions.eq(propertyName, value);
                break;
            case GT:
                expression = Restrictions.gt(propertyName, value);
                break;
            case LT:
                expression = Restrictions.lt(propertyName, value);
                break;
            case LT_OR_EQ:
                expression = Restrictions.le(propertyName, value);
                break;
            case GT_OR_EQ:
                expression = Restrictions.ge(propertyName, value);
                break;
            case NOT_EQ:
                expression = Restrictions.ne(propertyName, value);
                break;
            case NOT_NULL:
                expression = Restrictions.isNotNull(propertyName);
                break;
        }

        return expression;
    }
}