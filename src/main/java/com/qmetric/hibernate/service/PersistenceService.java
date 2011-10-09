// Copyright (c) 2011, QMetric Group Limited. All rights reserved.

package com.qmetric.hibernate.service;

import com.qmetric.hibernate.HibernateQueryWrapper;

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

    PersistenceStrategy findUnique(final HibernateQueryWrapper query);

    List<PersistenceStrategy> find(final HibernateQueryWrapper query);
}