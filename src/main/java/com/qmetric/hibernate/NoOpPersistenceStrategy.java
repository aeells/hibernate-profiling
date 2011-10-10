// Copyright (c) 2011, QMetric Group Limited. All rights reserved.

package com.qmetric.hibernate;

import java.util.ArrayList;
import java.util.List;

/**
 * Persistence strategy implementation that allows no persistence operations.
 */
public class NoOpPersistenceStrategy implements PersistenceStrategy
{
    public static final PersistenceStrategy NO_OP_INSTANCE = new NoOpPersistenceStrategy();

    public static final List<PersistenceStrategy> NO_OP_COLLECTION = new ArrayList<PersistenceStrategy>()
    {
        {
            this.add(NO_OP_INSTANCE);
        }
    };

    NoOpPersistenceStrategy()
    {
    }

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