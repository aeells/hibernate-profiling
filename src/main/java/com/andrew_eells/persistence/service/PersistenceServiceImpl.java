// Copyright (c) 2010, www.andrew-eells.com. All rights reserved.

package com.andrew_eells.persistence.service;

import com.andrew_eells.persistence.infrastructure.PersistenceStrategy;
import com.andrew_eells.persistence.infrastructure.query.QuerySpecification;
import com.andrew_eells.persistence.infrastructure.query.QuerySpecificationImpl;
import org.apache.commons.lang.Validate;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.hibernate3.HibernateTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Standardised database repository access implementation.
 */
@Service("persistenceService") @Transactional
public class PersistenceServiceImpl implements PersistenceService<PersistenceStrategy>
{
    private final HibernateTemplate hibernateTemplate;
    private final QuerySpecificationTranslator translator;

    @Autowired
    public PersistenceServiceImpl(final HibernateTemplate hibernateTemplate, final QuerySpecificationTranslator translator)
    {
        this.hibernateTemplate = hibernateTemplate;
        this.translator = translator;
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

        return QuerySpecificationImpl.uniqueElement(readList(querySpecification));
    }

    @Override public final List<PersistenceStrategy> readList(final QuerySpecification querySpecification)
    {
        Validate.notNull(querySpecification, "query specification should be non-null!");

        return hibernateTemplate.findByCriteria(translator.translate(querySpecification));
    }

    private void saveOrUpdate(final PersistenceStrategy model)
    {
        hibernateTemplate.saveOrUpdate(model);
    }
}