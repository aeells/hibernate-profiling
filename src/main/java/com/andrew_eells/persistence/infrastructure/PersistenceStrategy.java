// Copyright (c) 2010, www.andrew-eells.com. All rights reserved.

package com.andrew_eells.persistence.infrastructure;

import java.util.Date;

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
    boolean isCreate();

    /**
     * Defines update behaviour.
     *
     * @return <code>true</code> if the implementation should be updated; <code>false</code> otherwise.
     */
    boolean isUpdate();

    /**
     * Defines delete behaviour.
     *
     * @return <code>true</code> if the implementation should be deleted; <code>false</code> otherwise.
     */
    boolean isDelete();

    /**
     * Sets the last modified time.
     * @param time the time the implementation was last modified.
     */
    void setLastModified(Date time);
}