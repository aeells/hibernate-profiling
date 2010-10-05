// Copyright (c) 2010, www.andrew-eells.com. All rights reserved.

package com.andrew_eells.persistence.infrastructure.query;

/**
 * Container that allows for more complicated query functions than simple key-value based operations.
 */
public class QueryKeyInfo
{
    private String key;

    private boolean caseSensitive;

    public QueryKeyInfo(final String key, final boolean caseSensitive)
    {
        this.key = key;
        this.caseSensitive = caseSensitive;
    }

    public final String getKey()
    {
        return key;
    }

    public final boolean isCaseSensitive()
    {
        return caseSensitive;
    }
}