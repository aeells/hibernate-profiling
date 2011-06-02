// Copyright (c) 2011, QMetric Group Limited. All rights reserved.

package com.qmetric.hibernate.infrastructure.query;

import com.qmetric.hibernate.PersistenceStrategy;

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