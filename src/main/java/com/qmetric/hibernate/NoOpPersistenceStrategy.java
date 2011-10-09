// Copyright (c) 2011, QMetric Group Limited. All rights reserved.

package com.qmetric.hibernate;

/**
 * Persistence strategy implementation that allows no persistence operations.
 */
public class NoOpPersistenceStrategy implements PersistenceStrategy
{
    @Override public boolean isCreatable()
    {
        return false;
    }

    @Override public boolean isUpdateable()
    {
        return false;
    }

    @Override public boolean isDeletable()
    {
        return false;
    }
}