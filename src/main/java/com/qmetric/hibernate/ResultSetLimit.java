// Copyright (c) 2011, QMetric Group Limited. All rights reserved.

package com.qmetric.hibernate;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

public class ResultSetLimit
{
    private final int firstResult;

    private final int maxResults;

    public ResultSetLimit(final int firstResult, final int maxResults)
    {
        this.firstResult = firstResult;
        this.maxResults = maxResults;
    }

    public int getFirstResult()
    {
        return firstResult;
    }

    public int getMaxResults()
    {
        return maxResults;
    }

    @Override public int hashCode()
    {
        return HashCodeBuilder.reflectionHashCode(this);
    }

    @Override public boolean equals(final Object o)
    {
        return EqualsBuilder.reflectionEquals(this, o);
    }

    @Override public String toString()
    {
        return ToStringBuilder.reflectionToString(this);
    }
}