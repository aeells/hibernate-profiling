// Copyright (c) 2010, www.andrew-eells.com. All rights reserved.

package com.andrew_eells.persistence.service;

import com.andrew_eells.persistence.infrastructure.PersistenceStrategy;
import com.andrew_eells.persistence.infrastructure.query.QueryClauseOperator;
import com.andrew_eells.persistence.infrastructure.query.QueryKeyInfo;
import com.andrew_eells.persistence.infrastructure.query.QuerySpecification;
import com.andrew_eells.persistence.infrastructure.query.QuerySpecificationImpl;
import com.andrew_eells.persistence.infrastructure.query.SortKeyInfo;
import org.apache.commons.lang.Validate;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Disjunction;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.hibernate.criterion.SimpleExpression;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.hibernate3.HibernateTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

/**
 * Standardised database repository access implementation.
 */
@Service("persistenceService") @Transactional
public class PersistenceServiceImpl implements PersistenceService<PersistenceStrategy>
{
    private final HibernateTemplate hibernateTemplate;

    @Autowired
    public PersistenceServiceImpl(final HibernateTemplate hibernateTemplate)
    {
        this.hibernateTemplate = hibernateTemplate;
    }

    @Override public final void create(final PersistenceStrategy model)
    {
        if (model != null && model.isCreate())
        {
            saveOrUpdate(model);
        }
    }

    @Override public final void update(final PersistenceStrategy model)
    {
        if (model != null && model.isUpdate())
        {
            model.setLastModified(new DateTime());

            saveOrUpdate(model);
        }
    }

    @Override public final void delete(final PersistenceStrategy model)
    {
        if (model != null && model.isDelete())
        {
            hibernateTemplate.delete(model);
        }
    }

    @Override public final PersistenceStrategy readUnique(final QuerySpecification querySpecification)
    {
        Validate.notNull(querySpecification, "query specification should be non-null!");

        return QuerySpecificationImpl.uniqueElement((List<PersistenceStrategy>) hibernateTemplate.findByCriteria(translateSpecification(querySpecification)));
    }

    @Override public final List<PersistenceStrategy> readList(final QuerySpecification querySpecification)
    {
        Validate.notNull(querySpecification, "query specification should be non-null!");

        return hibernateTemplate.findByCriteria(translateSpecification(querySpecification));
    }

    private void saveOrUpdate(final PersistenceStrategy model)
    {
        hibernateTemplate.saveOrUpdate(model);
    }

    private DetachedCriteria translateSpecification(final QuerySpecification querySpecification)
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
            default:
                // do nothing
        }

        return expression;
    }
}