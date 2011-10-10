// Copyright (c) 2011, QMetric Group Limited. All rights reserved.

package com.qmetric.hibernate.service;

import com.qmetric.hibernate.ResultSetLimit;
import org.hibernate.criterion.DetachedCriteria;

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

    PersistenceStrategy findById(final Class daoClass, final String id);

    PersistenceStrategy findUnique(final DetachedCriteria criteria);

    List<PersistenceStrategy> find(final DetachedCriteria criteria);

    List<PersistenceStrategy> find(final DetachedCriteria criteria, final ResultSetLimit limit);
}