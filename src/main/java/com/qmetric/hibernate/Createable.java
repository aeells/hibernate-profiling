// Copyright (c) 2011, QMetric Group Limited. All rights reserved.

package com.qmetric.hibernate;

/**
 * Persistence strategy.
 */
public interface Createable
{
    /**
     * Defines create behaviour.
     *
     * @return <code>true</code> if the object can be created; <code>false</code> otherwise.
     */
    boolean isCreateAllowed();
}