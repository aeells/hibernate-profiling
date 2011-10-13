// Copyright (c) 2011, QMetric Group Limited. All rights reserved.

package com.qmetric.hibernate.service;

import com.qmetric.hibernate.Createable;
import com.qmetric.hibernate.Deleteable;
import com.qmetric.hibernate.Updateable;
import org.apache.commons.lang.Validate;
import org.hibernate.criterion.DetachedCriteria;
import org.springframework.orm.hibernate3.HibernateTemplate;

import java.util.List;

import static org.springframework.dao.support.DataAccessUtils.uniqueResult;

/**
 * Standardised database repository access implementation.
 */
public final class HibernateServiceImpl<T> implements HibernateService
{
    private final HibernateTemplate hibernateTemplate;

    public HibernateServiceImpl(final HibernateTemplate hibernateTemplate)
    {
        this.hibernateTemplate = hibernateTemplate;
    }

    @Override public final void create(final Createable model)
    {
        if (model != null && model.isCreateAllowed())
        {
            saveOrUpdate(model);
        }
    }

    @Override public final void update(final Updateable model)
    {
        if (model != null && model.isUpdateAllowed())
        {
            saveOrUpdate(model);
        }
    }

    @Override public final void delete(final Deleteable model)
    {
        if (model != null && model.isDeleteAllowed())
        {
            hibernateTemplate.delete(model);
        }
    }

    public final T findById(final Class daoClass, final String id)
    {
        Validate.notNull(daoClass, "class cannot be null!");
        Validate.notEmpty(id, "id cannot be empty!");

        //noinspection unchecked
        return (T) hibernateTemplate.get(daoClass, id);
    }

    public final T findUnique(final DetachedCriteria criteria)
    {
        Validate.notNull(criteria, "criteria cannot be null!");

        //noinspection unchecked
        return uniqueResult((List<T>) hibernateTemplate.findByCriteria(criteria));
    }

    public final T findFirstOrderedBy(final DetachedCriteria criteria)
    {
        Validate.notNull(criteria, "criteria cannot be null!");

        //noinspection unchecked
        return uniqueResult((List<T>) hibernateTemplate.findByCriteria(criteria, 0, 1));
    }

    public final List<T> find(final DetachedCriteria criteria)
    {
        Validate.notNull(criteria, "criteria cannot be null!");

        //noinspection unchecked
        return (List<T>) hibernateTemplate.findByCriteria(criteria);
    }

    public final List<T> find(final DetachedCriteria criteria, final int firstResult, final int maxResults)
    {
        Validate.notNull(criteria, "criteria cannot be null!");

        //noinspection unchecked
        return (List<T>) hibernateTemplate.findByCriteria(criteria, firstResult, maxResults);
    }

    // using merge and saveOrUpdate instead of save / update separately to safeguard against detached objects
    private void saveOrUpdate(final Object model)
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