package com.andrew_eells.persistence.infrastructure.query;

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
    NOT_EQ
}
