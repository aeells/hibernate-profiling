// Copyright (c) 2010, www.andrew-eells.com. All rights reserved.

package com.andrew_eells.persistence.service;

import com.andrew_eells.persistence.infrastructure.PersistenceSessionFactory;
import com.andrew_eells.persistence.infrastructure.PersistenceStrategy;
import com.andrew_eells.persistence.infrastructure.query.QueryKeyInfo;
import com.andrew_eells.persistence.infrastructure.query.QuerySpecification;
import com.andrew_eells.persistence.infrastructure.query.SortKeyInfo;
import org.apache.commons.lang.Validate;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Disjunction;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.hibernate.criterion.SimpleExpression;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Standardised database repository access implementation.
 */
@Service("persistenceService") @Transactional
public class PersistenceServiceImpl implements PersistenceService<PersistenceStrategy>
{
    private PersistenceSessionFactory persistenceSessionFactory;

    @Autowired
    public PersistenceServiceImpl(final PersistenceSessionFactory persistenceSessionFactory)
    {
        this.persistenceSessionFactory = persistenceSessionFactory;
    }

    @Override public final void create(final PersistenceStrategy model)
    {
        if (model != null && model.isCreate())
        {
            final Session session = persistenceSessionFactory.getSession();

            session.saveOrUpdate(model);
        }
    }

    @Override public final PersistenceStrategy readUnique(final QuerySpecification querySpecification)
    {
        Validate.notNull(querySpecification, "query specification should be non-null!");
        final Criteria queryCriteria = translateSpecification(querySpecification);

        //noinspection unchecked
        return (PersistenceStrategy) queryCriteria.uniqueResult();
    }

    @Override public final List<PersistenceStrategy> readList(final QuerySpecification querySpecification)
    {
        Validate.notNull(querySpecification, "query specification should be non-null!");
        final Criteria queryCriteria = translateSpecification(querySpecification);

        //noinspection unchecked
        return (List<PersistenceStrategy>) queryCriteria.list();
    }

    @Override public final PersistenceStrategy update(final PersistenceStrategy model)
    {
        if (model != null && model.isUpdate())
        {
            final Session session = persistenceSessionFactory.getSession();

            model.setLastModified(new Date());

            // need to merge as opposed to saveOrUpdate as dozer has created a new object (with the same identifier) on the way back down
            // through the stack and hibernate will throw a NonUniqueObjectException
            // http://www.stevideter.com/2008/12/07/saveorupdate-versus-merge-in-hibernate/
            //noinspection unchecked
            return (PersistenceStrategy) session.merge(model);
        }
        else
        {
            return null;
        }
    }

    @Override public final void delete(final PersistenceStrategy model)
    {
        if (model != null && model.isDelete())
        {
            final Session session = persistenceSessionFactory.getSession();

            session.delete(model);
        }
    }

    private Criteria translateSpecification(final QuerySpecification querySpecification)
    {
        final Map<QueryKeyInfo, Object> queryParams = querySpecification.getQueryParams();
        final Session session = persistenceSessionFactory.getSession();
        final Criteria queryCriteria = session.createCriteria(querySpecification.getPersistentClass());

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

    private Criteria translateQueryParamsForAndOperator(final Map<QueryKeyInfo, Object> queryParams, final Criteria queryCriteria)
    {
        for (final QueryKeyInfo keyInfo : queryParams.keySet())
        {
            final Object value = queryParams.get(keyInfo);

            final SimpleExpression expression = Restrictions.eq(keyInfo.getKey(), value);
            // ensure value is not null otherwise hibernate will throw an NPE
            if (value != null && !keyInfo.isCaseSensitive())
            {
                expression.ignoreCase();
            }

            queryCriteria.add(expression);
        }

        return queryCriteria;
    }

    private Criteria translateQueryParamsForOrOperator(final Map<QueryKeyInfo, Object> queryParams, final Criteria queryCriteria)
    {
        final Disjunction disjunction = Restrictions.disjunction();

        for (final QueryKeyInfo keyInfo : queryParams.keySet())
        {
            final Object value = queryParams.get(keyInfo);

            final SimpleExpression expression = Restrictions.eq(keyInfo.getKey(), value);
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
}