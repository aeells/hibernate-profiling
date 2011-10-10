// Copyright (c) 2011, QMetric Group Limited. All rights reserved.

package com.qmetric.hibernate.service;

import com.qmetric.hibernate.HibernateQueryWrapper;
import com.qmetric.hibernate.NoOpPersistenceStrategy;
import com.qmetric.hibernate.PersistenceStrategy;
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
        //noinspection unchecked
        return query == null ? NoOpPersistenceStrategy.NO_OP_INSTANCE : uniqueResult((List<PersistenceStrategy>) hibernateTemplate.findByCriteria(query.getCriteria()));
    }

    public final List<PersistenceStrategy> find(final HibernateQueryWrapper query)
    {
        //noinspection unchecked
        return query == null ? NoOpPersistenceStrategy.NO_OP_COLLECTION : (List<PersistenceStrategy>) hibernateTemplate.findByCriteria(query.getCriteria());
    }

    // todo aeells - why are we doing merge and saveOrUpdate as opposed to save and update separately ?
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