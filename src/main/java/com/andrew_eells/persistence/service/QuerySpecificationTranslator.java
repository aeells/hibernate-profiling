package com.andrew_eells.persistence.service;

import com.andrew_eells.persistence.infrastructure.query.QueryClauseOperator;
import com.andrew_eells.persistence.infrastructure.query.QueryKeyInfo;
import com.andrew_eells.persistence.infrastructure.query.QuerySpecification;
import com.andrew_eells.persistence.infrastructure.query.SortKeyInfo;
import org.hibernate.criterion.*;

import java.util.Map;

public class QuerySpecificationTranslator {

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

            final SimpleExpression expression = expressionForOperator(keyInfo.getOperator(), keyInfo.getKey(), value);
            // ensure value is not null otherwise hibernate will throw an NPE
            if (value != null && !keyInfo.isCaseSensitive())
            {
                expression.ignoreCase();
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

            final SimpleExpression expression = expressionForOperator(keyInfo.getOperator(), keyInfo.getKey(), value);
            // ensure value is not null otherwise hibernate will throw an NPE
            if (value != null && !keyInfo.isCaseSensitive())
            {
                expression.ignoreCase();
            }

            disjunction.add(expression);
        }

        queryCriteria.add(disjunction);

        return queryCriteria;
    }

    private SimpleExpression expressionForOperator(final QueryClauseOperator operator, final String propertyName, final Object value)
    {
        SimpleExpression expression = null;
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
        }

        return expression;
    }
}
