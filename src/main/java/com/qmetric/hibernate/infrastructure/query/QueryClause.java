// Copyright (c) 2011, QMetric Group Limited. All rights reserved.

package com.qmetric.hibernate.infrastructure.query;

/**
 * query field criteria object.
 */
public class QueryClause
{
    private String fieldName;

    private Object fieldValue;

    private QueryClauseOperator operator;

    public QueryClause(final QueryType queryType, final Object fieldValue, final QueryClauseOperator operator)
    {
        this.fieldName = queryType.getFieldName();
        this.fieldValue = fieldValue;
        this.operator = operator;
    }

    public QueryClause(final QueryType queryType, final QueryClauseOperator operator)
    {
        this.fieldName = queryType.getFieldName();
        this.operator = operator;
    }

    public String getFieldName()
    {
        return fieldName;
    }

    public Object getFieldValue()
    {
        return fieldValue;
    }

    public QueryClauseOperator getOperator()
    {
        return operator;
    }
}