package com.andrew_eells.persistence.infrastructure.query;

/**
 * query field criteria object.
 */
public class QueryClause
{
    private String fieldName;

    private String fieldValue;

    private QueryClauseOperator operator = null;

    public QueryClause(final String fieldName, final String fieldValue, final QueryClauseOperator operator)
    {
        this.fieldName = fieldName;
        this.fieldValue = fieldValue;
        this.operator = operator;
    }

    public QueryClauseOperator getOperator()
    {
        return operator;
    }

    public String getFieldName()
    {
        return fieldName;
    }

    public String getFieldValue()
    {
        return fieldValue;
    }
}
