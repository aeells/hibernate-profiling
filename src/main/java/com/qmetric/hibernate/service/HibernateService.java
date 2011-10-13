// Copyright (c) 2011, QMetric Group Limited. All rights reserved.

package com.qmetric.hibernate.service;

import com.qmetric.hibernate.Createable;
import com.qmetric.hibernate.Deleteable;
import com.qmetric.hibernate.Updateable;
import org.hibernate.criterion.DetachedCriteria;

import java.util.List;

/**
 * Standardised database access specification.
 */
public interface HibernateService<T>
{
    void create(final Createable object);

    void update(final Updateable object);

    void delete(final Deleteable object);

    T findById(final Class daoClass, final String id);

    T findUnique(final DetachedCriteria criteria);

    T findFirstOrderedBy(final DetachedCriteria criteria);

    List<T> find(final DetachedCriteria criteria);

    List<T> find(final DetachedCriteria criteria, final int firstResult, final int maxResults);
}