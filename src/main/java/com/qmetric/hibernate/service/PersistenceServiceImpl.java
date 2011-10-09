// Copyright (c) 2011, QMetric Group Limited. All rights reserved.

package com.qmetric.hibernate.service;

import com.qmetric.hibernate.NoOpPersistenceStrategy;
import com.qmetric.hibernate.PersistenceStrategy;
import org.springframework.orm.hibernate3.HibernateTemplate;

import java.util.ArrayList;
import java.util.List;

import static org.springframework.dao.support.DataAccessUtils.uniqueResult;

/**
 * Standardised database repository access implementation.
 */
public class PersistenceServiceImpl implements PersistenceService<PersistenceStrategy>
{
    public static final PersistenceStrategy NO_OP_PERSISTENCE_STRATEGY = new NoOpPersistenceStrategy();

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

    public PersistenceStrategy findUnique(final PersistenceQuery query)
    {
        //noinspection unchecked
        return query == null ? NO_OP_PERSISTENCE_STRATEGY : uniqueResult((List<PersistenceStrategy>) hibernateTemplate.findByCriteria(query.getCriteria()));
    }

    public final List<PersistenceStrategy> find(final PersistenceQuery query)
    {
        //noinspection unchecked
        return query == null ? noOpCollection() : (List<PersistenceStrategy>) hibernateTemplate.findByCriteria(query.getCriteria());
    }

    private ArrayList<PersistenceStrategy> noOpCollection()
    {
        return new ArrayList<PersistenceStrategy>()
        {
            {
                this.add(NO_OP_PERSISTENCE_STRATEGY);
            }
        };
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