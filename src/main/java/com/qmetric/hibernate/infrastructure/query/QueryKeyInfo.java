// Copyright (c) 2011, QMetric Group Limited. All rights reserved.

package com.qmetric.hibernate.infrastructure.query;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

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

    @Override
    public int hashCode()
    {
        return HashCodeBuilder.reflectionHashCode(this);
    }

    @Override
    public boolean equals(final Object obj)
    {
        return EqualsBuilder.reflectionEquals(this, obj);
    }

    @Override
    public String toString()
    {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.SIMPLE_STYLE);
    }
}