// Copyright (c) 2010, www.andrew-eells.com. All rights reserved.

package com.andrew_eells.persistence.service;

import com.andrew_eells.persistence.infrastructure.query.QuerySpecification;

import java.util.List;

/**
 * Standardised database repository access.
 */
public interface PersistenceService<PersistentStrategy>
{
    /**
     * Persist new object.
     *
     * @param object New object.
     */
    void create(final PersistentStrategy object);

    /**
     * Read unique result object.
     *
     * @param querySpecification Query specification.
     * @return Unique result or <code>null</code> if none exists.
     */
    PersistentStrategy readUnique(final QuerySpecification querySpecification);

    /**
     * Read collection of result objects.
     *
     * @param querySpecification Query specification.
     * @return Collection of results or empty collection if none exists.
     */
    List<PersistentStrategy> readList(final QuerySpecification querySpecification);

    /**
     * Update existing object.
     *
     * @param object Object to be updated.
     * @return Updated object.
     */
    void update(final PersistentStrategy object);

    /**
     * Delete existing object.
     *
     * @param object Object to be deleted.
     */
    void delete(final PersistentStrategy object);
}