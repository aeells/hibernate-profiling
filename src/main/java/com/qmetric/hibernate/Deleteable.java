// Copyright (c) 2011, QMetric Group Limited. All rights reserved.

package com.qmetric.hibernate;

public interface Deleteable
{
    /**
     * Defines delete behaviour.
     *
     * @return <code>true</code> if the object can be deleted; <code>false</code> otherwise.
     */
    boolean isDeleteAllowed();
}