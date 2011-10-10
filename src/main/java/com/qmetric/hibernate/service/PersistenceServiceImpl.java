// Copyright (c) 2011, QMetric Group Limited. All rights reserved.

package com.qmetric.hibernate.service;

import com.qmetric.hibernate.HibernateQueryWrapper;
import com.qmetric.hibernate.PersistenceStrategy;
import com.qmetric.hibernate.QueryLimit;
import org.apache.commons.lang.Validate;
import org.springframework.orm.hibernate3.HibernateTemplate;

import java.util.List;

import static org.springframework.dao.support.DataAccessUtils.uniqueResult;

/**
 * Standardised database repository access implementation.
 */
public class PersistenceServiceImpl implements PersistenceService<PersistenceStrategy>
{
    private final HibernateTemplate hibernateTemplate;

    public PersistenceServiceImpl(final HibernateTemplate hibernateTemplate)
    {
        this.hibernateTemplate = hibernateTemplate;
    }

    @Override public final void create(final PersistenceStrategy model)
    {
        if (model != null && model.isCreatable())
        {
            saveOrUpdate(model);
        }
    }

    @Override public final void update(final PersistenceStrategy model)
    {
        if (model != null && model.isUpdateable())
        {
            saveOrUpdate(model);
        }
    }

    @Override public final void delete(final PersistenceStrategy model)
    {
        if (model != null && model.isDeletable())
        {
            hibernateTemplate.delete(model);
        }
    }

    @Override public void flush()
    {
        hibernateTemplate.flush();
    }

    public PersistenceStrategy findUnique(final HibernateQueryWrapper query)
    {
        Validate.notNull(query, "query cannot be null reference!");

        final QueryLimit limit = query.getLimit();
        //noinspection unchecked
        return uniqueResult((List<PersistenceStrategy>) hibernateTemplate.findByCriteria(query.getCriteria(), limit.getFirstResult(), limit.getMaxResults()));
    }

    public final List<PersistenceStrategy> find(final HibernateQueryWrapper query)
    {
        Validate.notNull(query, "query cannot be null reference!");

        final QueryLimit limit = query.getLimit();
        //noinspection unchecked
        return (List<PersistenceStrategy>) hibernateTemplate.findByCriteria(query.getCriteria(), limit.getFirstResult(), limit.getMaxResults());
    }

    // using merge and saveOrUpdate instead of save / update separately to safeguard against detached objects
    private void saveOrUpdate(final PersistenceStrategy model)
    {
        if (hibernateTemplate.contains(model))
        {
            hibernateTemplate.merge(model);
        }
        else
        {
            hibernateTemplate.saveOrUpdate(model);
        }
    }
}