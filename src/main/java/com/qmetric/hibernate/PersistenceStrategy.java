// Copyright (c) 2011, QMetric Group Limited. All rights reserved.

package com.qmetric.hibernate;

/**
 * Persistence strategy.
 */
public interface PersistenceStrategy
{
    /**
     * Defines create behaviour.
     *
     * @return <code>true</code> if the implementation should be created; <code>false</code> otherwise.
     */
    boolean isCreatable();

    /**
     * Defines update behaviour.
     *
     * @return <code>true</code> if the implementation should be updated; <code>false</code> otherwise.
     */
    boolean isUpdateable();

    /**
     * Defines delete behaviour.
     *
     * @return <code>true</code> if the implementation should be deleted; <code>false</code> otherwise.
     */
    boolean isDeletable();
}