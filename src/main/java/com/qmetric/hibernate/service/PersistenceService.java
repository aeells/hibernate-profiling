// Copyright (c) 2011, QMetric Group Limited. All rights reserved.

package com.qmetric.hibernate.service;

import org.hibernate.criterion.Criterion;

import java.util.List;

/**
 * Standardised database access specification.
 */
public interface PersistenceService<PersistenceStrategy>
{
    void create(final PersistenceStrategy object);

    void update(final PersistenceStrategy object);

    void delete(final PersistenceStrategy object);

    void flush();

    PersistenceStrategy findByPrimaryKey(final Class<? extends PersistenceStrategy> daoClass, final String pkFieldName, final String pk);

    List<PersistenceStrategy> findByForeignKey(final Class<? extends PersistenceStrategy> daoClass, final String fkFieldName, final com.qmetric.hibernate.PersistenceStrategy fk);

    PersistenceStrategy findUnique(final Class<? extends PersistenceStrategy> daoClass, final Criterion... criterion);

    List<PersistenceStrategy> findCollection(final Class<? extends PersistenceStrategy> daoClass, final Criterion... criterion);
}