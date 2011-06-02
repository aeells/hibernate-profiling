// Copyright (c) 2011, QMetric Group Limited. All rights reserved.

package com.qmetric.hibernate.infrastructure.query;

/**
 * Enum for QueryClause Operators
 */
public enum QueryClauseOperator
{
    /**
     * Equals
     */
    EQ,

    /**
     * Greater than or equals
     */
    GT_OR_EQ,

    /**
     * Less than or equals
     */
    LT_OR_EQ,

    /**
     * Greater than
     */
    GT,

    /**
     * Less than
     */
    LT,

    /**
     * Not equal
     */
    NOT_EQ,

    /**
     * Not null
     */
    NOT_NULL
}