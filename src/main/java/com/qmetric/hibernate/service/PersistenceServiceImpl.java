// Copyright (c) 2011, QMetric Group Limited. All rights reserved.

package com.qmetric.hibernate.service;

import com.qmetric.hibernate.PersistenceStrategy;
import com.qmetric.hibernate.infrastructure.query.QuerySpecification;
import com.qmetric.hibernate.infrastructure.query.QuerySpecificationImpl;
import com.qmetric.hibernate.infrastructure.query.QueryType;
import com.qmetric.hibernate.infrastructure.query.QueryClause;
import org.apache.commons.lang.Validate;
import org.springframework.dao.support.DataAccessUtils;
import org.springframework.orm.hibernate3.HibernateTemplate;

import java.io.Serializable;
import java.util.List;

import static com.qmetric.hibernate.infrastructure.query.QueryClauseOperator.EQ;

/**
 * Standardised database repository access implementation.
 */
public class PersistenceServiceImpl implements PersistenceService<PersistenceStrategy>
{
    private final HibernateTemplate hibernateTemplate;

    private final QuerySpecificationTranslator translator;

    public PersistenceServiceImpl(final HibernateTemplate hibernateTemplate, final QuerySpecificationTranslator translator)
    {
        this.hibernateTemplate = hibernateTemplate;
        this.translator = translator;
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

    @Override public PersistenceStrategy findById(final Class<? extends PersistenceStrategy> persistentClass, final Serializable id)
    {
        return readUnique(new QuerySpecificationImpl(persistentClass, new QueryClause(new PrimaryId(), id, EQ)));
    }

    @Override public final PersistenceStrategy readUnique(final QuerySpecification querySpecification)
    {
        Validate.notNull(querySpecification, "query specification should be non-null!");

        return DataAccessUtils.uniqueResult(readList(querySpecification));
    }

    @Override public final List<PersistenceStrategy> readList(final QuerySpecification querySpecification)
    {
        Validate.notNull(querySpecification, "query specification should be non-null!");

        //noinspection unchecked
        return hibernateTemplate.findByCriteria(translator.translate(querySpecification));
    }

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

    private class PrimaryId implements QueryType
    {
        @Override public String getFieldName()
        {
            return "id";
        }
    }
}