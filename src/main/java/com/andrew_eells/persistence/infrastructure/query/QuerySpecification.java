// Copyright (c) 2010, www.andrew-eells.com. All rights reserved.

package com.andrew_eells.persistence.infrastructure.query;

import com.andrew_eells.persistence.infrastructure.PersistenceStrategy;

import java.util.Map;

/**
 * Specification pattern to decouple requesting client from Hibernate implementation details.
 * <p/>
 * See the pattern spec pdf on Martin Fowler's {@link -link http://www.martinfowler.com/apsupp/spec.pdf site}.
 */
public interface QuerySpecification
{
    /**
     * Indicates the query operator.
     *
     * @return Query operator.
     */
    QuerySpecificationOperator getQueryOperator();

    /**
     * Indicates the query parameters.
     *
     * @return Query parameters.
     */
    Map<QueryKeyInfo, Object> getQueryParams();

    /**
     * Indicates query the sort key.
     *
     * @return Sort key.
     */
    SortKeyInfo getSortKey();

    /**
     * Returns the class of the object to be queried for.
     * @return Repository class.
     */
    Class<? extends PersistenceStrategy> getPersistentClass();
}