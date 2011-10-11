// Copyright (c) 2011, QMetric Group Limited. All rights reserved.

package com.qmetric.hibernate.service;

import com.qmetric.hibernate.PersistenceStrategy;
import org.apache.commons.lang.Validate;
import org.hibernate.criterion.DetachedCriteria;
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

    public PersistenceStrategy findById(final Class daoClass, final String id)
    {
        Validate.notNull(daoClass, "class cannot be null!");
        Validate.notEmpty(id, "id cannot be empty!");

        //noinspection unchecked
        return (PersistenceStrategy) hibernateTemplate.get(daoClass, id);
    }

    public PersistenceStrategy findUnique(final DetachedCriteria criteria)
    {
        Validate.notNull(criteria, "criteria cannot be null!");

        //noinspection unchecked
        return uniqueResult((List<PersistenceStrategy>) hibernateTemplate.findByCriteria(criteria));
    }

    public final PersistenceStrategy findFirstOrderedBy(final DetachedCriteria criteria)
    {
        Validate.notNull(criteria, "criteria cannot be null!");

        //noinspection unchecked
        return uniqueResult((List<PersistenceStrategy>) hibernateTemplate.findByCriteria(criteria, 0, 1));
    }

    public final List<PersistenceStrategy> find(final DetachedCriteria criteria)
    {
        Validate.notNull(criteria, "criteria cannot be null!");

        //noinspection unchecked
        return (List<PersistenceStrategy>) hibernateTemplate.findByCriteria(criteria);
    }

    public final List<PersistenceStrategy> find(final DetachedCriteria criteria, final int firstResult, final int maxResults)
    {
        Validate.notNull(criteria, "criteria cannot be null!");

        //noinspection unchecked
        return (List<PersistenceStrategy>) hibernateTemplate.findByCriteria(criteria, firstResult, maxResults);
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