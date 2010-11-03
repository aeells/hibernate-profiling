// Copyright (c) 2010, www.andrew-eells.com. All rights reserved.

package com.andrew_eells.persistence.infrastructure;

import org.joda.time.DateTime;

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

    /**
     * Sets the last modified time.
     * @param time the time the implementation was last modified.
     */
    void setLastModified(DateTime time);
}