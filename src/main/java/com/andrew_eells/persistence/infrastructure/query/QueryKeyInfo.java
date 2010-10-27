// Copyright (c) 2010, www.andrew-eells.com. All rights reserved.

package com.andrew_eells.persistence.infrastructure.query;

/**
 * Container that allows for more complicated query functions than simple key-value based operations.
 */
public class QueryKeyInfo
{
    private String key;

    private boolean caseSensitive;

    private QueryClauseOperator operator = QueryClauseOperator.EQ;

    public QueryKeyInfo(final String key, final boolean caseSensitive, final QueryClauseOperator operator)
    {
        this.key = key;
        this.caseSensitive = caseSensitive;
        this.operator = operator;
    }

    public final String getKey()
    {
        return key;
    }

    public final boolean isCaseSensitive()
    {
        return caseSensitive;
    }

    public QueryClauseOperator getOperator()
    {
        return operator;
    }
}