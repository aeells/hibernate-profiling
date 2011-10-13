// Copyright (c) 2011, QMetric Group Limited. All rights reserved.

package com.qmetric.hibernate;

public interface Updateable
{
    /**
     * Defines update behaviour.
     *
     * @return <code>true</code> if the object can be updated; <code>false</code> otherwise.
     */
    boolean isUpdateAllowed();
}